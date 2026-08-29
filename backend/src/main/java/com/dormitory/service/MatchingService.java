package com.dormitory.service;

import com.dormitory.mapper.*;
import com.dormitory.model.*;
import com.dormitory.utils.BedSelection;
import com.dormitory.utils.GenderMatcher;
import com.dormitory.utils.MatchingCapacity;
import com.dormitory.utils.MatchingGroups;
import com.dormitory.utils.MatchingMajors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatchingService {

    private final DormBatchMapper batchMapper;
    private final StudentMapper studentMapper;
    private final StudentAnswerMapper studentAnswerMapper;
    private final QuestionnaireMapper questionnaireMapper;
    private final QuestionOptionMapper questionOptionMapper;
    private final BatchRoomMapper batchRoomMapper;
    private final RoomMapper roomMapper;
    private final BedMapper bedMapper;
    private final BuildingMapper buildingMapper;
    private final RoommateGroupMapper roommateGroupMapper;
    private final AllocationResultMapper allocationResultMapper;
    private final NotificationMapper notificationMapper;

    public MatchingService(DormBatchMapper batchMapper, StudentMapper studentMapper,
                           StudentAnswerMapper studentAnswerMapper,
                           QuestionnaireMapper questionnaireMapper,
                           QuestionOptionMapper questionOptionMapper,
                           BatchRoomMapper batchRoomMapper, RoomMapper roomMapper,
                           BedMapper bedMapper, BuildingMapper buildingMapper,
                           RoommateGroupMapper roommateGroupMapper,
                           AllocationResultMapper allocationResultMapper,
                           NotificationMapper notificationMapper) {
        this.batchMapper = batchMapper;
        this.studentMapper = studentMapper;
        this.studentAnswerMapper = studentAnswerMapper;
        this.questionnaireMapper = questionnaireMapper;
        this.questionOptionMapper = questionOptionMapper;
        this.batchRoomMapper = batchRoomMapper;
        this.roomMapper = roomMapper;
        this.bedMapper = bedMapper;
        this.buildingMapper = buildingMapper;
        this.roommateGroupMapper = roommateGroupMapper;
        this.allocationResultMapper = allocationResultMapper;
        this.notificationMapper = notificationMapper;
    }

    @Transactional
    public void executeMatching(Long batchId) {
        DormBatch batch = batchMapper.findById(batchId);
        if (batch == null) throw new RuntimeException("批次不存在");
        if (!"running".equals(batch.getMatchStatus())) {
            throw new RuntimeException("只有 running 状态的批次才能执行匹配");
        }

        int claimed = batchMapper.updateStatusIf(batch.getId(), "running", "matching");
        if (claimed == 0) {
            throw new RuntimeException("批次状态已变化，无法执行匹配");
        }

        try {
            // 清理旧的匹配数据（防止重复运行时数据叠加）
            allocationResultMapper.deleteByBatchId(batch.getId());
            roommateGroupMapper.deleteByBatchId(batch.getId());

            // 1. 加载数据
            List<Student> allStudents = studentMapper.findByDormBatchId(batch.getId());
            if (allStudents.isEmpty()) {
                throw new RuntimeException("批次内没有学生");
            }

            List<Questionnaire> allQuestions = questionnaireMapper.findByIsActive(1);
            List<Questionnaire> matchQuestions = allQuestions.stream()
                    .filter(q -> "match".equals(q.getQuestionType()))
                    .collect(Collectors.toList());
            List<Questionnaire> bedQuestions = allQuestions.stream()
                    .filter(q -> "bed".equals(q.getQuestionType()))
                    .collect(Collectors.toList());

            Map<Long, QuestionOption> optionMap = new HashMap<>();
            Map<Long, Questionnaire> questionMap = new HashMap<>();
            for (Questionnaire q : allQuestions) {
                questionMap.put(q.getId(), q);
                for (QuestionOption opt : questionOptionMapper.findByQId(q.getId())) {
                    optionMap.put(opt.getId(), opt);
                }
            }

            List<Long> studentIdList = allStudents.stream().map(Student::getId).collect(Collectors.toList());
            List<StudentAnswer> allAnswers = studentAnswerMapper.findByStudentIds(studentIdList);
            Map<Long, Map<Long, Long>> studentAnswers = new HashMap<>();
            for (StudentAnswer ans : allAnswers) {
                studentAnswers.computeIfAbsent(ans.getStudentId(), k -> new HashMap<>())
                        .put(ans.getQId(), ans.getOptionId());
            }
            Set<Long> submittedStudentIds = studentAnswers.keySet();

            List<BatchRoom> batchRooms = batchRoomMapper.findByBatchId(batch.getId());
            if (batchRooms.isEmpty()) {
                throw new RuntimeException("批次房源池为空");
            }

            List<Room> allRooms = new ArrayList<>();
            Map<Long, Building> buildingMap = new HashMap<>();
            for (BatchRoom br : batchRooms) {
                Room room = roomMapper.findById(br.getRoomId());
                if (room != null && room.getIsActive() == 1 && room.getStatus() == 1) {
                    allRooms.add(room);
                    if (!buildingMap.containsKey(room.getBuildingId())) {
                        Building b = buildingMapper.findById(room.getBuildingId());
                        if (b != null) buildingMap.put(room.getBuildingId(), b);
                    }
                }
            }

            // 按 occupancy 分为空房和未满房
            List<Room> allEmptyRooms = allRooms.stream()
                    .filter(r -> r.getCurrentCount() == null || r.getCurrentCount() == 0)
                    .collect(Collectors.toList());
            List<Room> allPartialRooms = allRooms.stream()
                    .filter(r -> r.getCurrentCount() != null && r.getCurrentCount() > 0 && !r.isFull())
                    .collect(Collectors.toList());

            // 2. 按性别分组
            Map<String, List<Student>> byGender = allStudents.stream()
                    .collect(Collectors.groupingBy(Student::getGender));

            List<RoommateGroup> allGroups = new ArrayList<>();
            List<AllocationResult> allResults = new ArrayList<>();
            Set<Long> reservedBedIds = new HashSet<>();
            Map<Long, Integer> roomExtraOccupants = new HashMap<>();

            // 3. 对每个性别组执行匹配
            for (Map.Entry<String, List<Student>> entry : byGender.entrySet()) {
                String gender = entry.getKey();
                List<Student> genderStudents = entry.getValue();

                // 获取适用于该性别的空房和未满房
                List<Room> compatibleEmptyRooms = allEmptyRooms.stream()
                        .filter(r -> {
                            Building b = buildingMap.get(r.getBuildingId());
                            if (b == null) return false;
                            return isGenderCompatible(b, gender);
                        })
                        .collect(Collectors.toList());
                List<Room> compatiblePartialRooms = allPartialRooms.stream()
                        .filter(r -> {
                            Building b = buildingMap.get(r.getBuildingId());
                            if (b == null) return false;
                            return isGenderCompatible(b, gender);
                        })
                        .collect(Collectors.toList());

                if (compatibleEmptyRooms.isEmpty() && compatiblePartialRooms.isEmpty()) {
                    throw new RuntimeException("没有适用于性别[" + gender + "]的房源");
                }

                // 拆分为已提交和未提交
                List<Student> submitted = genderStudents.stream()
                        .filter(s -> submittedStudentIds.contains(s.getId()))
                        .collect(Collectors.toList());
                List<Student> unsubmitted = genderStudents.stream()
                        .filter(s -> !submittedStudentIds.contains(s.getId()))
                        .collect(Collectors.toList());

                // 处理已提交学生
                if (!submitted.isEmpty()) {
                    List<Room> capacityRooms = new ArrayList<>();
                    capacityRooms.addAll(compatibleEmptyRooms);
                    capacityRooms.addAll(compatiblePartialRooms);
                    List<StudentGroup> groups = matchSubmitted(submitted, matchQuestions, optionMap,
                            questionMap, studentAnswers, batch, capacityRooms);
                    assignRoomsAndBeds(groups, compatibleEmptyRooms, compatiblePartialRooms, batch, buildingMap, reservedBedIds, roomExtraOccupants);
                    for (StudentGroup g : groups) {
                        RoommateGroup rg = createRoommateGroup(g, batch.getId());
                        allGroups.add(rg);
                        for (int i = 0; i < g.students.size(); i++) {
                            allResults.add(createAllocationResult(g, i, rg.getId(), batch.getId()));
                        }
                    }
                }

                // 处理未提交学生
                if (!unsubmitted.isEmpty()) {
                    List<StudentGroup> groups = matchUnsubmitted(unsubmitted, batch,
                            compatibleEmptyRooms, buildingMap);
                    assignRoomsAndBeds(groups, compatibleEmptyRooms, compatiblePartialRooms, batch, buildingMap, reservedBedIds, roomExtraOccupants);
                    for (StudentGroup g : groups) {
                        RoommateGroup rg = createRoommateGroup(g, batch.getId());
                        allGroups.add(rg);
                        for (int i = 0; i < g.students.size(); i++) {
                            allResults.add(createAllocationResult(g, i, rg.getId(), batch.getId()));
                        }
                    }
                }
            }

            // 4. 持久化
            for (RoommateGroup rg : allGroups) {
                roommateGroupMapper.insert(rg);
            }
            for (AllocationResult ar : allResults) {
                allocationResultMapper.insert(ar);
            }

            // 5. 发送通知
            for (AllocationResult ar : allResults) {
                Notification notif = new Notification();
                notif.setRecipientId(ar.getStudentId());
                notif.setBatchId(ar.getBatchId());
                notif.setType("推荐生成");
                notif.setContent("您的宿舍分配推荐已生成，请登录系统查看并确认");
                notif.setChannel("inner");
                notif.setStatus("sent");
                notificationMapper.insert(notif);
            }

            batchMapper.updateStatus(batch.getId(), "confirming");
        } catch (Exception e) {
            batchMapper.updateStatusIf(batch.getId(), "matching", "running");
            if (e instanceof RuntimeException runtime) {
                throw runtime;
            }
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private List<StudentGroup> matchSubmitted(List<Student> students,
                                              List<Questionnaire> matchQuestions,
                                              Map<Long, QuestionOption> optionMap,
                                              Map<Long, Questionnaire> questionMap,
                                              Map<Long, Map<Long, Long>> studentAnswers,
                                              DormBatch batch,
                                              List<Room> rooms) {

        int capacity = MatchingCapacity.mostCommon(rooms, 4);

        // 计算两两匹配度
        int n = students.size();
        double[][] scores = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                double score = computeCompatibility(students.get(i), students.get(j),
                        matchQuestions, optionMap, questionMap, studentAnswers, batch);
                scores[i][j] = score;
                scores[j][i] = score;
            }
        }

        boolean[] grouped = new boolean[n];
        List<StudentGroup> groups = new ArrayList<>();

        while (true) {
            // find highest scoring pair among ungrouped
            double bestScore = -Double.MAX_VALUE;
            int bestI = -1, bestJ = -1;
            for (int i = 0; i < n; i++) {
                if (grouped[i]) continue;
                for (int j = i + 1; j < n; j++) {
                    if (grouped[j]) continue;
                    if (!MatchingMajors.canGroup(batch.getAllowMixMajor(),
                            students.get(i).getMajorId(), students.get(j).getMajorId())) {
                        continue;
                    }
                    if (scores[i][j] > bestScore) {
                        bestScore = scores[i][j];
                        bestI = i;
                        bestJ = j;
                    }
                }
            }

            if (bestI == -1) break;

            StudentGroup group = new StudentGroup();
            group.students.add(students.get(bestI));
            group.students.add(students.get(bestJ));
            grouped[bestI] = true;
            grouped[bestJ] = true;

            // greedily add best-matching remaining students
            while (group.students.size() < capacity) {
                double bestAvg = -Double.MAX_VALUE;
                int bestK = -1;
                for (int k = 0; k < n; k++) {
                    if (grouped[k]) continue;
                    if (!MatchingMajors.canGroup(batch.getAllowMixMajor(),
                            group.students.get(0).getMajorId(), students.get(k).getMajorId())) {
                        continue;
                    }
                    double avg = 0;
                    for (Student member : group.students) {
                        int idx = students.indexOf(member);
                        avg += scores[idx][k];
                    }
                    avg /= group.students.size();
                    if (avg > bestAvg) {
                        bestAvg = avg;
                        bestK = k;
                    }
                }
                if (bestK == -1) break;
                group.students.add(students.get(bestK));
                grouped[bestK] = true;
            }

            groups.add(group);
        }

        // 处理剩余未分组学生
        List<Student> leftover = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (!grouped[i]) leftover.add(students.get(i));
        }
        if (!leftover.isEmpty()) {
            List<Student> still = new ArrayList<>();
            for (Student s : leftover) {
                boolean placed = false;
                for (StudentGroup g : groups) {
                    if (g.students.size() >= capacity) {
                        continue;
                    }
                    if (!MatchingMajors.canGroup(batch.getAllowMixMajor(),
                            g.students.get(0).getMajorId(), s.getMajorId())) {
                        continue;
                    }
                    g.students.add(s);
                    placed = true;
                    break;
                }
                if (!placed) {
                    still.add(s);
                }
            }
            if (!still.isEmpty()) {
                List<List<Student>> extraGroups = new ArrayList<>();
                if (batch.getAllowMixMajor() != null && batch.getAllowMixMajor() == 0) {
                    still.stream()
                            .collect(Collectors.groupingBy(s -> s.getMajorId() != null ? s.getMajorId() : 0))
                            .values()
                            .forEach(majorLeft -> MatchingGroups.appendLeftovers(extraGroups, majorLeft, capacity));
                } else {
                    MatchingGroups.appendLeftovers(extraGroups, still, capacity);
                }
                for (List<Student> extra : extraGroups) {
                    StudentGroup g = new StudentGroup();
                    g.students.addAll(extra);
                    groups.add(g);
                }
            }
        }

        // 计算每个学生在组内的平均匹配度得分
        for (StudentGroup g : groups) {
            for (Student s : g.students) {
                int si = students.indexOf(s);
                double total = 0;
                int count = 0;
                for (Student m : g.students) {
                    if (s.equals(m)) continue;
                    int mi = students.indexOf(m);
                    total += scores[si][mi];
                    count++;
                }
                g.matchScores.put(s.getId(), count > 0 ? total / count : 0);
            }
        }

        return groups;
    }

    private double computeCompatibility(Student a, Student b,
                                        List<Questionnaire> matchQuestions,
                                        Map<Long, QuestionOption> optionMap,
                                        Map<Long, Questionnaire> questionMap,
                                        Map<Long, Map<Long, Long>> studentAnswers,
                                        DormBatch batch) {
        double score = 0;
        Map<Long, Long> ansA = studentAnswers.getOrDefault(a.getId(), Collections.emptyMap());
        Map<Long, Long> ansB = studentAnswers.getOrDefault(b.getId(), Collections.emptyMap());

        for (Questionnaire q : matchQuestions) {
            Long optAId = ansA.get(q.getId());
            Long optBId = ansB.get(q.getId());
            if (optAId == null || optBId == null) continue;
            if (optAId.equals(optBId)) {
                QuestionOption opt = optionMap.get(optAId);
                if (opt != null && opt.getOptionValue() != null) {
                    score += (double) opt.getOptionValue() * q.getWeight();
                }
            }
        }

        if (a.getMajorId() != null && b.getMajorId() != null
                && a.getMajorId().equals(b.getMajorId())) {
            score += batch.getMajorBonus() != null ? batch.getMajorBonus() : 0;
        }

        return score;
    }

    private List<StudentGroup> matchUnsubmitted(List<Student> students, DormBatch batch,
                                                 List<Room> rooms,
                                                 Map<Long, Building> buildingMap) {
        int capacity = MatchingCapacity.mostCommon(rooms, 4);
        List<StudentGroup> groups = new ArrayList<>();

        if (batch.getAllowMixMajor() != null && batch.getAllowMixMajor() == 0) {
            Map<Integer, List<Student>> byMajor = students.stream()
                    .collect(Collectors.groupingBy(
                            s -> s.getMajorId() != null ? s.getMajorId() : 0));

            for (List<Student> majorStudents : byMajor.values()) {
                for (int i = 0; i < majorStudents.size(); i += capacity) {
                    StudentGroup group = new StudentGroup();
                    int end = Math.min(i + capacity, majorStudents.size());
                    group.students.addAll(majorStudents.subList(i, end));
                    groups.add(group);
                }
            }
        } else {
            for (int i = 0; i < students.size(); i += capacity) {
                StudentGroup group = new StudentGroup();
                int end = Math.min(i + capacity, students.size());
                group.students.addAll(students.subList(i, end));
                groups.add(group);
            }
        }

        return groups;
    }

    private List<StudentGroup> splitIntoCapacityGroups(List<Student> students, int capacity) {
        int size = capacity > 0 ? capacity : 4;
        List<StudentGroup> groups = new ArrayList<>();
        for (int i = 0; i < students.size(); i += size) {
            StudentGroup group = new StudentGroup();
            group.students.addAll(students.subList(i, Math.min(i + size, students.size())));
            groups.add(group);
        }
        return groups;
    }

    private void assignRoomsAndBeds(List<StudentGroup> groups, List<Room> emptyRooms,
                                     List<Room> partialRooms, DormBatch batch,
                                     Map<Long, Building> buildingMap,
                                     Set<Long> reservedBedIds,
                                     Map<Long, Integer> roomExtraOccupants) {
        boolean preferSameFloor = batch.getPreferSameFloor() != null
                && batch.getPreferSameFloor() == 1;

        // 排序空房和未满房
        emptyRooms.sort(Comparator.comparingLong(Room::getBuildingId)
                .thenComparingInt(Room::getFloor)
                .thenComparing(Room::getRoomNumber));
        partialRooms.sort(Comparator.comparingLong(Room::getBuildingId)
                .thenComparingInt(Room::getFloor)
                .thenComparing(Room::getRoomNumber));

        for (StudentGroup group : groups) {
            int needed = group.students.size();
            Room assigned = null;

            // Round 1: 优先使用空房
            if (preferSameFloor && !emptyRooms.isEmpty()) {
                Map<Integer, List<Room>> byFloor = emptyRooms.stream()
                        .filter(r -> r.getAvailableBeds()
                                - roomExtraOccupants.getOrDefault(r.getId(), 0) >= needed)
                        .collect(Collectors.groupingBy(Room::getFloor));
                for (Map.Entry<Integer, List<Room>> fe : byFloor.entrySet()) {
                    if (fe.getValue().size() >= groups.size()) {
                        assigned = fe.getValue().get(0);
                        break;
                    }
                }
            }

            if (assigned == null) {
                assigned = findFirstSuitable(emptyRooms, needed, roomExtraOccupants);
            }

            // Round 2: 空房不足，从未满房补位
            if (assigned == null) {
                assigned = findFirstSuitable(partialRooms, needed, roomExtraOccupants);
            }

            if (assigned == null) {
                throw new RuntimeException("可用房间不足，无法分配" + needed + "人小组");
            }

            group.room = assigned;
            roomExtraOccupants.merge(assigned.getId(), needed, Integer::sum);

            // 分配床位
            assignBeds(group, assigned, reservedBedIds);
        }
    }

    private Room findFirstSuitable(List<Room> rooms, int needed, Map<Long, Integer> roomExtra) {
        for (Room r : rooms) {
            int extra = roomExtra.getOrDefault(r.getId(), 0);
            if (r.getAvailableBeds() - extra >= needed) {
                return r;
            }
        }
        return null;
    }

    private void assignBeds(StudentGroup group, Room room, Set<Long> reservedBedIds) {
        List<Bed> availableBeds = bedMapper.findAvailableByRoomId(room.getId());
        group.beds = new ArrayList<>();
        for (Student student : group.students) {
            String preference = getStudentBedPreference(student);
            Bed assigned = BedSelection.pick(availableBeds, reservedBedIds, preference);
            if (assigned == null) {
                throw new RuntimeException("房间[" + room.getRoomNumber() + "]无可用床位");
            }
            group.beds.add(assigned);
            if (assigned.getId() != null) {
                reservedBedIds.add(assigned.getId());
            }
        }
    }

    private String getStudentBedPreference(Student student) {
        List<StudentAnswer> answers = studentAnswerMapper.findByStudentId(student.getId());
        for (StudentAnswer ans : answers) {
            QuestionOption opt = questionOptionMapper.findById(ans.getOptionId());
            if (opt != null) {
                String text = opt.getOptionText();
                if (text != null) {
                    if (text.contains("靠窗") || text.contains("窗户")) return "window";
                    if (text.contains("走廊") || text.contains("靠走廊")) return "corridor";
                }
            }
        }
        return null;
    }

    private RoommateGroup createRoommateGroup(StudentGroup group, Long batchId) {
        RoommateGroup rg = new RoommateGroup();
        rg.setBatchId(batchId);
        rg.setRoomId(group.room.getId());
        rg.setMemberIdList(group.students.stream().map(Student::getId).collect(Collectors.toList()));
        return rg;
    }

    private AllocationResult createAllocationResult(StudentGroup group, int index,
                                                     Long groupId, Long batchId) {
        Student student = group.students.get(index);
        Bed bed = group.beds.get(index);

        AllocationResult ar = new AllocationResult();
        ar.setStudentId(student.getId());
        ar.setBatchId(batchId);
        ar.setRoommateGroupId(groupId);
        ar.setRoomId(group.room.getId());
        ar.setBedId(bed.getId());
        Double score = group.matchScores.getOrDefault(student.getId(), 0.0);
        ar.setMatchScore(BigDecimal.valueOf(score).setScale(2, RoundingMode.HALF_UP));
        ar.setReallocationCount(0);
        ar.setStatus("recommended");
        return ar;
    }

    /** 重新匹配单个学生 */
    @Transactional
    public void rematchStudent(Long studentId, Long batchId) {
        DormBatch batch = batchMapper.findById(batchId);
        if (batch == null) throw new RuntimeException("批次不存在");

        AllocationResult oldResult = allocationResultMapper
                .findByStudentIdAndBatchId(studentId, batchId);
        if (oldResult == null) throw new RuntimeException("没有原分配记录");

        Student student = studentMapper.findById(studentId);
        if (student == null) throw new RuntimeException("学生不存在");

        // 从旧室友组中移除该学生（匹配阶段无物理资源占用，无需释放床位和房间）
        if (oldResult.getRoommateGroupId() != null) {
            RoommateGroup oldGroup = roommateGroupMapper.findById(oldResult.getRoommateGroupId());
            if (oldGroup != null) {
                List<Long> members = oldGroup.getMemberIdList();
                members.remove(studentId);
                oldGroup.setMemberIdList(members);
                roommateGroupMapper.update(oldGroup);
            }
        }

        // 获取可用房间（排除已满的）
        List<BatchRoom> batchRooms = batchRoomMapper.findByBatchId(batchId);
        List<Room> compatibleRooms = new ArrayList<>();
        for (BatchRoom br : batchRooms) {
            Room room = roomMapper.findById(br.getRoomId());
            if (room != null && room.getIsActive() == 1 && room.getStatus() == 1 && room.getAvailableBeds() >= 1) {
                Building b = buildingMapper.findById(room.getBuildingId());
                if (b != null && isGenderCompatible(b, student.getGender())) {
                    compatibleRooms.add(room);
                }
            }
        }

        if (compatibleRooms.isEmpty()) {
            throw new RuntimeException("无可用房间重新分配");
        }

        // 对每个候选房间计算与已入住室友的兼容性，选最高分
        List<Questionnaire> matchQuestions = questionnaireMapper.findByIsActive(1).stream()
                .filter(q -> "match".equals(q.getQuestionType()))
                .collect(Collectors.toList());
        Map<Long, QuestionOption> optionMap = new HashMap<>();
        Map<Long, Questionnaire> questionMap = new HashMap<>();
        for (Questionnaire q : matchQuestions) {
            questionMap.put(q.getId(), q);
            for (QuestionOption opt : questionOptionMapper.findByQId(q.getId())) {
                optionMap.put(opt.getId(), opt);
            }
        }
        Map<Long, Map<Long, Long>> studentAnswerMap = new HashMap<>();
        Map<Long, Long> myAnswers = new HashMap<>();
        List<StudentAnswer> answers = studentAnswerMapper.findByStudentId(studentId);
        for (StudentAnswer ans : answers) {
            myAnswers.put(ans.getQId(), ans.getOptionId());
        }
        studentAnswerMap.put(studentId, myAnswers);

        Room bestRoom = null;
        double bestScore = -Double.MAX_VALUE;

        for (Room room : compatibleRooms) {
            if (room.getAvailableBeds() <= 0) continue;
            // 获取该房间已入住的学生
            List<Student> occupants = studentMapper.findByRoomId(room.getId());
            if (occupants.isEmpty()) {
                if (bestRoom == null) bestRoom = room;
                continue;
            }
            double totalScore = 0;
            int count = 0;
            for (Student occupant : occupants) {
                if (!studentAnswerMap.containsKey(occupant.getId())) {
                    List<StudentAnswer> occAnswers = studentAnswerMapper.findByStudentId(occupant.getId());
                    Map<Long, Long> occAnsMap = new HashMap<>();
                    for (StudentAnswer ans : occAnswers) {
                        occAnsMap.put(ans.getQId(), ans.getOptionId());
                    }
                    studentAnswerMap.put(occupant.getId(), occAnsMap);
                }
                double s = computeCompatibility(student, occupant, matchQuestions,
                        optionMap, questionMap, studentAnswerMap, batch);
                totalScore += s;
                count++;
            }
            double avgScore = count > 0 ? totalScore / count : 0;
            if (avgScore > bestScore) {
                bestScore = avgScore;
                bestRoom = room;
            }
        }

        Room targetRoom = bestRoom != null ? bestRoom : compatibleRooms.get(0);

        List<Bed> availableBeds = bedMapper.findAvailableByRoomId(targetRoom.getId());
        if (availableBeds.isEmpty()) {
            throw new RuntimeException("房间[" + targetRoom.getRoomNumber() + "]无可用床位");
        }

        // 排除本批次内已被其他学生推荐/分配的床位，避免重复推荐同一床位
        Set<Long> reservedBedIds = new HashSet<>();
        for (AllocationResult ar : allocationResultMapper.findByRoomIdAndBatchId(targetRoom.getId(), batchId)) {
            if (ar.getBedId() != null && !ar.getStudentId().equals(studentId)) {
                reservedBedIds.add(ar.getBedId());
            }
        }

        String preference = getStudentBedPreference(student);
        Bed assigned = BedSelection.pick(availableBeds, reservedBedIds, preference);
        if (assigned == null) {
            throw new RuntimeException("房间[" + targetRoom.getRoomNumber() + "]无可用床位（本批次床位已被占用）");
        }

        // 匹配阶段仅更新逻辑推荐，不占用物理资源（床位/房间在确认时占用）

        // 加入新房间的室友组（或新建）
        List<RoommateGroup> roomGroups = roommateGroupMapper.findByRoomId(targetRoom.getId());
        RoommateGroup targetGroup = null;
        int roomCapacity = targetRoom.getCapacity() != null ? targetRoom.getCapacity() : 4;
        for (RoommateGroup rg : roomGroups) {
            if (rg.getBatchId() != null && !rg.getBatchId().equals(batchId)) {
                continue;
            }
            if (rg.getMemberIdList() != null && rg.getMemberIdList().size() < roomCapacity) {
                targetGroup = rg;
                break;
            }
        }
        if (targetGroup != null) {
            List<Long> members = targetGroup.getMemberIdList();
            members.add(studentId);
            targetGroup.setMemberIdList(members);
            roommateGroupMapper.update(targetGroup);
        } else {
            targetGroup = new RoommateGroup();
            targetGroup.setBatchId(batchId);
            targetGroup.setRoomId(targetRoom.getId());
            targetGroup.setMemberIdList(new ArrayList<>(List.of(studentId)));
            roommateGroupMapper.insert(targetGroup);
        }

        // 更新已有分配记录（不插入新行，uk_student_batch 唯一约束禁止重复 student_id+batch_id）
        oldResult.setRoomId(targetRoom.getId());
        oldResult.setBedId(assigned.getId());
        oldResult.setRoommateGroupId(targetGroup.getId());
        oldResult.setMatchScore(BigDecimal.valueOf(Math.max(0, bestScore)));
        oldResult.setReallocationCount(
                oldResult.getReallocationCount() != null ? oldResult.getReallocationCount() + 1 : 1);
        oldResult.setStatus("recommended");
        allocationResultMapper.update(oldResult);
    }

    private boolean isGenderCompatible(Building building, String gender) {
        return GenderMatcher.isCompatible(gender, building == null ? null : building.getGenderType());
    }

    // ---- 内部数据结构 ----

    static class StudentGroup {
        List<Student> students = new ArrayList<>();
        Room room;
        List<Bed> beds = new ArrayList<>();
        Map<Long, Double> matchScores = new HashMap<>();
    }
}
