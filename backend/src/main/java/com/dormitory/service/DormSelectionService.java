package com.dormitory.service;

import com.dormitory.mapper.*;
import com.dormitory.model.*;
import com.dormitory.utils.OccupancyRelease;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DormSelectionService {

    private final UserMapper userMapper;
    private final StudentMapper studentMapper;
    private final DormBatchMapper dormBatchMapper;
    private final QuestionnaireMapper questionnaireMapper;
    private final QuestionOptionMapper questionOptionMapper;
    private final StudentAnswerMapper studentAnswerMapper;
    private final AllocationResultMapper allocationResultMapper;
    private final MatchingService matchingService;
    private final BedMapper bedMapper;
    private final OperationLogMapper operationLogMapper;
    private final RelocationApplicationMapper relocationApplicationMapper;
    private final RoomMapper roomMapper;
    private final MajorMapper majorMapper;

    public DormSelectionService(UserMapper userMapper, StudentMapper studentMapper,
                                DormBatchMapper dormBatchMapper, QuestionnaireMapper questionnaireMapper,
                                QuestionOptionMapper questionOptionMapper, StudentAnswerMapper studentAnswerMapper,
                                AllocationResultMapper allocationResultMapper,
                                MatchingService matchingService,
                                BedMapper bedMapper,
                                OperationLogMapper operationLogMapper,
                                RelocationApplicationMapper relocationApplicationMapper,
                                RoomMapper roomMapper,
                                MajorMapper majorMapper) {
        this.userMapper = userMapper;
        this.studentMapper = studentMapper;
        this.dormBatchMapper = dormBatchMapper;
        this.questionnaireMapper = questionnaireMapper;
        this.questionOptionMapper = questionOptionMapper;
        this.studentAnswerMapper = studentAnswerMapper;
        this.allocationResultMapper = allocationResultMapper;
        this.matchingService = matchingService;
        this.bedMapper = bedMapper;
        this.operationLogMapper = operationLogMapper;
        this.relocationApplicationMapper = relocationApplicationMapper;
        this.roomMapper = roomMapper;
        this.majorMapper = majorMapper;
    }

    public Map<String, Object> mySurvey(String username) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        Student student = studentMapper.findByUserId(user.getId());
        if (student == null) {
            throw new RuntimeException("学生信息不存在");
        }

        Map<String, Object> studentData = new HashMap<>();
        studentData.put("dormBatchId", student.getDormBatchId());

        Map<String, Object> result = new HashMap<>();
        result.put("student", studentData);

        if (student.getDormBatchId() == null) {
            result.put("batch", null);
            result.put("questions", Collections.emptyList());
            return result;
        }

        DormBatch batch = dormBatchMapper.findById(student.getDormBatchId());
        if (batch == null) {
            throw new RuntimeException("选宿批次不存在");
        }

        Map<String, Object> batchData = new HashMap<>();
        batchData.put("id", batch.getId());
        batchData.put("name", batch.getName());
        batchData.put("startTime", batch.getStartTime() != null ? batch.getStartTime().toString() : null);
        batchData.put("endTime", batch.getEndTime() != null ? batch.getEndTime().toString() : null);
        batchData.put("confirmDeadline", batch.getConfirmDeadline() != null ? batch.getConfirmDeadline().toString() : null);
        batchData.put("matchStatus", batch.getMatchStatus());
        result.put("batch", batchData);

        // 批次不同阶段返回不同数据
        if ("confirming".equals(batch.getMatchStatus()) || "finished".equals(batch.getMatchStatus())) {
            // 返回分配结果
            AllocationResult allocation = allocationResultMapper.findByStudentIdAndBatchId(student.getId(), batch.getId());
            Map<String, Object> allocationData = null;
            if (allocation != null) {
                allocationData = new HashMap<>();
                allocationData.put("id", allocation.getId());
                allocationData.put("roomNumber", allocation.getRoomNumber());
                allocationData.put("bedNumber", allocation.getBedNumber());
                allocationData.put("matchScore", allocation.getMatchScore());
                allocationData.put("status", allocation.getStatus());
                allocationData.put("roommateGroupId", allocation.getRoommateGroupId());

                // 补充楼栋名
                if (allocation.getRoomId() != null) {
                    Room allocatedRoom = roomMapper.findById(allocation.getRoomId());
                    if (allocatedRoom != null && allocatedRoom.getBuildingName() != null) {
                        allocationData.put("buildingName", allocatedRoom.getBuildingName());
                    }
                }
            }
            result.put("allocation", allocationData);

            // 室友脱敏信息
            List<Map<String, Object>> roommates = new ArrayList<>();
            if (allocation != null && allocation.getRoomId() != null) {
                List<AllocationResult> sameRoomAllocs =
                    allocationResultMapper.findByRoomIdAndBatchId(allocation.getRoomId(), batch.getId());

                List<Questionnaire> matchQuestions = questionnaireMapper.findByQuestionType("match");
                Set<Long> matchQIds = matchQuestions.stream()
                    .map(Questionnaire::getId)
                    .collect(Collectors.toSet());

                for (AllocationResult ar : sameRoomAllocs) {
                    if (ar.getStudentId().equals(student.getId())) continue;

                    Map<String, Object> roommateData = new HashMap<>();
                    Student roommate = studentMapper.findById(ar.getStudentId());
                    if (roommate != null && roommate.getMajorId() != null) {
                        Major major = majorMapper.findById(roommate.getMajorId().longValue());
                        roommateData.put("majorName", major != null ? major.getName() : "未知专业");
                    } else {
                        roommateData.put("majorName", "未知专业");
                    }

                    List<StudentAnswer> answers = studentAnswerMapper.findByStudentId(ar.getStudentId());
                    List<String> tags = answers.stream()
                        .filter(a -> matchQIds.contains(a.getQId()))
                        .map(StudentAnswer::getOptionText)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                    roommateData.put("tags", tags);

                    roommates.add(roommateData);
                }
            }
            result.put("roommates", roommates);

            result.put("questions", Collections.emptyList());

            // 检查是否已重新匹配过
            int reallocated = countReallocations(allocation);
            batchData.put("reallocationUsed", reallocated);
            batchData.put("maxReallocation", batch.getMaxReallocation() != null ? batch.getMaxReallocation() : 1);

            // 入住后调换信息
            if (student.getStatus() != null && student.getStatus() == 1 && student.getRoomId() != null) {
                int thisYear = java.time.LocalDateTime.now().getYear();
                int yearlyCount = relocationApplicationMapper.countApprovedOrExecutedInYear(student.getId(), thisYear);
                int pendingCount = relocationApplicationMapper.countPendingOrApproved(student.getId());
                Map<String, Object> relocationInfo = new HashMap<>();
                relocationInfo.put("relocationUsedThisYear", yearlyCount);
                relocationInfo.put("hasPendingApplication", pendingCount > 0);
                relocationInfo.put("maxRelocationPerYear", 1);
                result.put("relocationInfo", relocationInfo);
            }
        } else {
            // running 阶段返回问卷
            List<StudentAnswer> existingAnswers = studentAnswerMapper.findByStudentId(student.getId());
            studentData.put("hasSubmitted", !existingAnswers.isEmpty());

            Set<Long> answeredOptionIds = existingAnswers.stream()
                    .map(StudentAnswer::getOptionId)
                    .collect(Collectors.toSet());

            List<Questionnaire> questions = questionnaireMapper.findByIsActive(1);
            List<Map<String, Object>> questionList = new ArrayList<>();

            for (Questionnaire q : questions) {
                Map<String, Object> qData = new HashMap<>();
                qData.put("id", q.getId());
                qData.put("questionText", q.getQuestionText());
                qData.put("questionType", q.getQuestionType());
                qData.put("isRequired", q.getIsRequired());

                List<QuestionOption> options = questionOptionMapper.findByQId(q.getId());
                List<Map<String, Object>> optionList = new ArrayList<>();
                for (QuestionOption opt : options) {
                    Map<String, Object> optData = new HashMap<>();
                    optData.put("id", opt.getId());
                    optData.put("optionText", opt.getOptionText());
                    optData.put("selected", answeredOptionIds.contains(opt.getId()));
                    optionList.add(optData);
                }
                qData.put("options", optionList);
                questionList.add(qData);
            }
            result.put("questions", questionList);
            result.put("allocation", null);
        }

        return result;
    }

    @Transactional
    public Map<String, Object> submitAnswers(String username, List<AnswerItem> answers) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        Student student = studentMapper.findByUserId(user.getId());
        if (student == null) {
            throw new RuntimeException("学生信息不存在");
        }

        if (student.getDormBatchId() == null) {
            throw new RuntimeException("暂未分配选宿批次");
        }

        DormBatch batch = dormBatchMapper.findById(student.getDormBatchId());
        if (batch == null) {
            throw new RuntimeException("选宿批次不存在");
        }

        if (!"running".equals(batch.getMatchStatus())) {
            throw new RuntimeException("当前批次未开放问卷");
        }

        LocalDateTime now = LocalDateTime.now();
        if (batch.getStartTime() != null && now.isBefore(batch.getStartTime())) {
            throw new RuntimeException("问卷尚未开放");
        }
        if (batch.getEndTime() != null && now.isAfter(batch.getEndTime())) {
            throw new RuntimeException("问卷已截止");
        }

        List<Questionnaire> activeQuestions = questionnaireMapper.findByIsActive(1);
        Set<Long> activeQIds = activeQuestions.stream()
                .map(Questionnaire::getId)
                .collect(Collectors.toSet());

        Set<Long> requiredQIds = activeQuestions.stream()
                .filter(q -> q.getIsRequired() == 1)
                .map(Questionnaire::getId)
                .collect(Collectors.toSet());

        Set<Long> submittedQIds = answers.stream()
                .map(AnswerItem::getQId)
                .collect(Collectors.toSet());

        for (Long requiredQId : requiredQIds) {
            if (!submittedQIds.contains(requiredQId)) {
                throw new RuntimeException("请回答所有必答题目");
            }
        }

        for (AnswerItem item : answers) {
            if (!activeQIds.contains(item.getQId())) {
                throw new RuntimeException("题目 " + item.getQId() + " 不存在或已停用");
            }
            QuestionOption option = questionOptionMapper.findById(item.getOptionId());
            if (option == null || !option.getQId().equals(item.getQId())) {
                throw new RuntimeException("选项 " + item.getOptionId() + " 不属于题目 " + item.getQId());
            }
        }

        studentAnswerMapper.deleteByStudentId(student.getId());

        for (AnswerItem item : answers) {
            StudentAnswer answer = new StudentAnswer();
            answer.setStudentId(student.getId());
            answer.setQId(item.getQId());
            answer.setOptionId(item.getOptionId());
            studentAnswerMapper.insert(answer);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("submitTime", LocalDateTime.now().toString());
        return response;
    }

    @Transactional
    public Map<String, Object> confirmAllocation(String username) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        Student student = studentMapper.findByUserId(user.getId());
        if (student == null) {
            throw new RuntimeException("学生信息不存在");
        }
        if (student.getDormBatchId() == null) {
            throw new RuntimeException("暂未分配选宿批次");
        }
        DormBatch batch = dormBatchMapper.findById(student.getDormBatchId());
        if (batch == null) {
            throw new RuntimeException("选宿批次不存在");
        }
        if (!"confirming".equals(batch.getMatchStatus())) {
            throw new RuntimeException("当前批次不在确认阶段");
        }
        if (batch.getConfirmDeadline() != null && LocalDateTime.now().isAfter(batch.getConfirmDeadline())) {
            throw new RuntimeException("确认已截止");
        }

        AllocationResult allocation = allocationResultMapper.findByStudentIdAndBatchId(student.getId(), batch.getId());
        if (allocation == null) {
            throw new RuntimeException("暂无分配结果");
        }
        if (!"recommended".equals(allocation.getStatus())) {
            throw new RuntimeException("当前分配状态不允许确认，状态: " + allocation.getStatus());
        }

        // 先增加房间人数（容量检查），再占床位，防止人数满后床位幽灵占用
        if (OccupancyRelease.needsRoomIncrement(student.getRoomId(), allocation.getRoomId())) {
            if (student.getRoomId() != null) {
                releaseStudentOccupation(student);
            }
            if (allocation.getRoomId() != null) {
                int inc = roomMapper.incrementCount(allocation.getRoomId());
                if (inc == 0) {
                    throw new RuntimeException("房间人数已满，确认失败");
                }
            }
        }

        if (allocation.getBedId() != null && !alreadyOccupyingBed(student, allocation)) {
            int rows = bedMapper.tryOccupy(allocation.getBedId());
            if (rows == 0) {
                if (OccupancyRelease.needsRoomIncrement(student.getRoomId(), allocation.getRoomId())
                        && allocation.getRoomId() != null) {
                    roomMapper.decrementCount(allocation.getRoomId());
                }
                throw new RuntimeException("床位已被占用，请联系辅导员重新匹配");
            }
        }

        allocationResultMapper.updateStatus(allocation.getId(), "confirmed");
        updateStudentRoom(student, allocation);

        // 写操作日志
        OperationLog log = new OperationLog();
        log.setStudentId(student.getId());
        log.setOperatorType("student");
        log.setOperatorId(student.getStudentNo());
        log.setAction("确认宿舍");
        log.setDetail("{\"allocationId\":" + allocation.getId() + ", \"roomId\":" + allocation.getRoomId() + "}");
        operationLogMapper.insert(log);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "确认成功");
        return result;
    }

    @Transactional
    public Map<String, Object> requestReallocation(String username) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        Student student = studentMapper.findByUserId(user.getId());
        if (student == null) {
            throw new RuntimeException("学生信息不存在");
        }
        if (student.getDormBatchId() == null) {
            throw new RuntimeException("暂未分配选宿批次");
        }
        DormBatch batch = dormBatchMapper.findById(student.getDormBatchId());
        if (batch == null) {
            throw new RuntimeException("选宿批次不存在");
        }
        if (!"confirming".equals(batch.getMatchStatus())) {
            throw new RuntimeException("当前批次不在确认阶段");
        }
        if (batch.getConfirmDeadline() != null && LocalDateTime.now().isAfter(batch.getConfirmDeadline())) {
            throw new RuntimeException("确认已截止");
        }

        AllocationResult allocation = allocationResultMapper.findByStudentIdAndBatchId(student.getId(), batch.getId());
        if (allocation == null) {
            throw new RuntimeException("暂无分配结果");
        }
        if (!"recommended".equals(allocation.getStatus())) {
            throw new RuntimeException("当前状态不允许重新匹配");
        }

        int reallocated = countReallocations(allocation);
        if (reallocated >= (batch.getMaxReallocation() != null ? batch.getMaxReallocation() : 1)) {
            throw new RuntimeException("已超出重新匹配次数限制");
        }

        try {
            matchingService.rematchStudent(student.getId(), batch.getId());
        } catch (RuntimeException e) {
            throw new RuntimeException("重新匹配失败: " + e.getMessage());
        }

        // 写操作日志
        OperationLog log = new OperationLog();
        log.setStudentId(student.getId());
        log.setOperatorType("student");
        log.setOperatorId(student.getStudentNo());
        log.setAction("智能重匹配");
        log.setDetail("{\"oldAllocationId\":" + allocation.getId() + "}");
        operationLogMapper.insert(log);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "已重新匹配，请查看新的分配结果");
        return result;
    }

    private int countReallocations(AllocationResult current) {
        if (current == null || current.getReallocationCount() == null) return 0;
        return current.getReallocationCount();
    }

    private void updateStudentRoom(Student student, AllocationResult allocation) {
        student.setRoomId(allocation.getRoomId());
        student.setBedNumber(allocation.getBedNumber());
        student.setStatus(1);
        student.setCheckInDate(LocalDateTime.now());
        studentMapper.update(student);
        if (student.getRoomId() != null) {
            roomMapper.setCurrentCount(student.getRoomId(), studentMapper.countByRoomId(student.getRoomId()));
        }
    }

    private boolean alreadyOccupyingBed(Student student, AllocationResult allocation) {
        if (student.getRoomId() == null || allocation.getBedId() == null) {
            return false;
        }
        if (!student.getRoomId().equals(allocation.getRoomId()) || student.getBedNumber() == null) {
            return false;
        }
        Bed bed = bedMapper.findById(allocation.getBedId());
        return bed != null && student.getBedNumber().equals(bed.getBedNumber());
    }

    private void releaseStudentOccupation(Student student) {
        if (student.getRoomId() == null) {
            return;
        }
        roomMapper.decrementCount(student.getRoomId());
        if (student.getBedNumber() != null) {
            for (Bed bed : bedMapper.findByRoomId(student.getRoomId())) {
                if (student.getBedNumber().equals(bed.getBedNumber())) {
                    bedMapper.updateOccupied(bed.getId(), 0);
                    break;
                }
            }
        }
    }

    public static class AnswerItem {
        private Long qId;
        private Long optionId;

        public Long getQId() { return qId; }
        public void setQId(Long qId) { this.qId = qId; }
        public Long getOptionId() { return optionId; }
        public void setOptionId(Long optionId) { this.optionId = optionId; }
    }
}
