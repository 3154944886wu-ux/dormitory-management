package com.dormitory.service;

import com.dormitory.mapper.*;
import com.dormitory.model.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 演示数据：覆盖各楼栋/班级教师绑定，以及打卡/请假/晚归/未归等场景。
 * 启用：app.seed-demo.enabled=true 后重启后端（仅建议在开发环境使用）。
 */
@Service
public class DemoCheckInSeedService {

    private static final LocalDate D1 = LocalDate.of(2026, 6, 20);
    private static final LocalDate D2 = LocalDate.of(2026, 6, 21);
    private static final LocalDate D3 = LocalDate.of(2026, 6, 22);
    private static final LocalDate D4 = LocalDate.of(2026, 6, 23);
    private static final LocalDate D5 = LocalDate.of(2026, 6, 24);
    private static final List<LocalDate> FILL_DATES = List.of(D1, D2, D3);
    private static final List<LocalDate> ALL_NORMAL_DATES = List.of(D4, D5);

    /** 仅异常、无打卡的演示学号+日期（批量正常打卡时跳过） */
    private static final Map<String, Set<LocalDate>> EXCEPTION_ONLY_SLOTS = Map.of(
            "20230007", Set.of(D1),
            "20230018", Set.of(D2),
            "20230019", Set.of(D2),
            "20230021", Set.of(D1)
    );

    private final TeacherService teacherService;
    private final UserMapper userMapper;
    private final ManagerScopeMapper managerScopeMapper;
    private final StudentMapper studentMapper;
    private final BuildingMapper buildingMapper;
    private final RoomMapper roomMapper;
    private final CheckInMapper checkInMapper;
    private final CheckExceptionMapper checkExceptionMapper;
    private final LeaveRequestMapper leaveRequestMapper;
    private final CheckRuleMapper checkRuleMapper;
    private final JdbcTemplate jdbcTemplate;

    public DemoCheckInSeedService(TeacherService teacherService,
                                  UserMapper userMapper,
                                  ManagerScopeMapper managerScopeMapper,
                                  StudentMapper studentMapper,
                                  BuildingMapper buildingMapper,
                                  RoomMapper roomMapper,
                                  CheckInMapper checkInMapper,
                                  CheckExceptionMapper checkExceptionMapper,
                                  LeaveRequestMapper leaveRequestMapper,
                                  CheckRuleMapper checkRuleMapper,
                                  JdbcTemplate jdbcTemplate) {
        this.teacherService = teacherService;
        this.userMapper = userMapper;
        this.managerScopeMapper = managerScopeMapper;
        this.studentMapper = studentMapper;
        this.buildingMapper = buildingMapper;
        this.roomMapper = roomMapper;
        this.checkInMapper = checkInMapper;
        this.checkExceptionMapper = checkExceptionMapper;
        this.leaveRequestMapper = leaveRequestMapper;
        this.checkRuleMapper = checkRuleMapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    /** 补齐演示数据依赖的列（如 migration_checkin_manager 未执行时） */
    public void ensureSchema() {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.COLUMNS " +
                        "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'check_exceptions' AND COLUMN_NAME = 'handle_result'",
                Integer.class);
        if (count != null && count == 0) {
            jdbcTemplate.execute(
                    "ALTER TABLE check_exceptions ADD COLUMN handle_result VARCHAR(50) " +
                            "COMMENT '处理结果: safe_return/reported_stay_out/unreachable/other' AFTER handler_id");
        }
    }

    @Transactional
    public void seed() {
        ensureDefaultCheckRule();
        assignDemoRooms();
        Map<String, Long> buildingIds = loadBuildingIds();
        List<String> classes = studentMapper.findDistinctClassNames();
        if (classes.isEmpty()) {
            System.out.println("⚠ 无学生班级数据，请先执行 database/test_data.sql");
            return;
        }

        seedTeachers(buildingIds, classes);
        seedLeaveRequests();
        seedCheckInsAndExceptions();

        System.out.println("✅ 演示数据已生成：教师绑定 + 打卡/请假/晚归/未归记录");
    }

    private void ensureDefaultCheckRule() {
        if (checkRuleMapper.findDefault() != null) {
            return;
        }
        CheckRule rule = new CheckRule();
        rule.setName("默认归寝规则");
        rule.setCheckStartTime(LocalTime.of(22, 0));
        rule.setCheckEndTime(LocalTime.of(23, 0));
        rule.setAbsentDeadline(LocalTime.MIDNIGHT);
        rule.setApplyDays("1,2,3,4,5,6,7");
        rule.setIsDefault(1);
        rule.setStatus(1);
        rule.setRequireLocation(0);
        rule.setAllowedRadius(500);
        rule.setMaxLocationAccuracy(200);
        rule.setExceptionThreshold(3);
        checkRuleMapper.insert(rule);
    }

    private void assignDemoRooms() {
        Map<String, Long> buildingIds = loadBuildingIds();
        Long maleBuilding = buildingIds.getOrDefault("1号楼", buildingIds.get("2号楼"));
        Long femaleBuilding = buildingIds.get("3号楼");
        int maleIdx = 0;
        int femaleIdx = 0;
        List<Room> maleRooms = maleBuilding != null ? roomMapper.findByBuildingId(maleBuilding) : List.of();
        List<Room> femaleRooms = femaleBuilding != null ? roomMapper.findByBuildingId(femaleBuilding) : List.of();

        for (Student student : studentMapper.findAll()) {
            if (student.getRoomId() != null) {
                continue;
            }
            if ("女".equals(student.getGender()) && !femaleRooms.isEmpty()) {
                Room room = femaleRooms.get(femaleIdx % femaleRooms.size());
                femaleIdx++;
                assignRoom(student, room, "A");
            } else if (maleBuilding != null && !maleRooms.isEmpty()) {
                Room room = maleRooms.get(maleIdx % maleRooms.size());
                maleIdx++;
                assignRoom(student, room, "A");
            }
        }
    }

    private void assignRoom(Student student, Room room, String bed) {
        student.setRoomId(room.getId());
        student.setBedNumber(bed);
        student.setCheckInDate(LocalDateTime.now().minusDays(30));
        student.setStatus(1);
        studentMapper.update(student);
    }

    private Map<String, Long> loadBuildingIds() {
        Map<String, Long> map = new LinkedHashMap<>();
        for (Building b : buildingMapper.findAll()) {
            map.put(b.getName(), b.getId());
        }
        return map;
    }

    private void seedTeachers(Map<String, Long> buildingIds, List<String> classes) {
        teacherService.ensureTeacher("010001", "张扬");
        teacherService.ensureTeacher("010002", "李昊");

        Long b1 = buildingIds.get("1号楼");
        Long b2 = buildingIds.get("2号楼");
        Long b3 = buildingIds.get("3号楼");

        if (b1 != null) {
            ensureTeacherWithScope("010003", "王一栋", b1, null);
        }
        if (b2 != null) {
            ensureTeacherWithScope("010004", "李二栋", b2, null);
        }
        if (b3 != null) {
            ensureTeacherWithScope("010005", "赵三栋", b3, null);
        }

        int maxClassTeachers = Math.min(classes.size(), 8);
        for (int i = 0; i < maxClassTeachers; i++) {
            String className = classes.get(i);
            ensureTeacherWithScope(String.format("010%03d", 6 + i), classTeacherName(className), null, className);
        }

        if (b1 != null && !classes.isEmpty()) {
            ensureTeacherWithScope("010014", "交叉A", b1, classes.get(0));
        }
        if (b2 != null && classes.size() > 1) {
            ensureTeacherWithScope("010015", "交叉B", b2, classes.get(1));
        }
        if (b3 != null && classes.size() > 2) {
            ensureTeacherWithScope("010016", "交叉C", b3, classes.get(2));
        }

        Long user17 = ensureTeacherWithScope("010017", "陈多绑", null, null);
        if (user17 != null && b1 != null && !classes.isEmpty()) {
            addScopeIfAbsent(user17, b1, classes.get(0));
            if (b2 != null && classes.size() > 1) {
                addScopeIfAbsent(user17, b2, classes.get(1));
            }
        }

        ensureTeacherWithScope("010018", "刘无绑", null, null);
    }

    private void addScopeIfAbsent(Long userId, Long buildingId, String className) {
        for (ManagerScope scope : managerScopeMapper.findActiveByUserId(userId)) {
            boolean sameBuilding = (scope.getBuildingId() == null && buildingId == null)
                    || (scope.getBuildingId() != null && scope.getBuildingId().equals(buildingId));
            boolean sameClass = (scope.getClassName() == null && (className == null || className.isBlank()))
                    || (scope.getClassName() != null && scope.getClassName().equals(className));
            if (sameBuilding && sameClass) {
                return;
            }
        }
        ManagerScope scope = new ManagerScope();
        scope.setBuildingId(buildingId);
        scope.setClassName(className);
        teacherService.addScope(userId, scope);
    }

    private Long ensureTeacherWithScope(String employeeNo, String name, Long buildingId, String className) {
        teacherService.ensureTeacher(employeeNo, name);
        User user = userMapper.findByUsername(employeeNo);
        if (user == null) {
            return null;
        }
        if (buildingId != null || (className != null && !className.isBlank())) {
            addScopeIfAbsent(user.getId(), buildingId, className);
        }
        return user.getId();
    }

    private String classTeacherName(String className) {
        if (className.length() <= 4) {
            return className + "师";
        }
        return className.substring(0, 4) + "师";
    }

    private void seedLeaveRequests() {
        insertLeave("20230008", 0, "[演示]事假-待审批", D1.atTime(18, 0), D3.atTime(8, 0), 0);
        insertLeave("20230009", 1, "[演示]病假-待审批", D2.atTime(12, 0), D2.atTime(23, 59), 0);
        insertLeave("20230010", 0, "[演示]事假-已批准", D1.atTime(8, 0), D1.atTime(20, 0), 1);
        insertLeave("20230011", 2, "[演示]其他-已拒绝", D2.atTime(8, 0), D2.atTime(18, 0), 2);
        insertLeave("20230012", 0, "[演示]事假-已撤销", D1.atTime(0, 0), D1.atTime(23, 59), 3);
        insertLeave("20230013", 1, "[演示]病假-已批准", D1.atTime(8, 0), D2.atTime(8, 0), 1);
        insertLeave("20230014", 0, "[演示]事假-已销假", D1.atTime(8, 0), D2.atTime(8, 0), 4);
    }

    private void insertLeave(String studentNo, int leaveType, String reason,
                             LocalDateTime start, LocalDateTime end, int targetStatus) {
        Student student = studentMapper.findByStudentNo(studentNo);
        if (student == null) {
            return;
        }
        for (LeaveRequest existing : leaveRequestMapper.findByStudentId(student.getId())) {
            if (reason.equals(existing.getReason())) {
                return;
            }
        }

        LeaveRequest leave = new LeaveRequest();
        leave.setStudentId(student.getId());
        leave.setLeaveType(leaveType);
        leave.setReason(reason);
        leave.setStartTime(start);
        leave.setEndTime(end);
        leave.setContactPhone(student.getPhone());
        leaveRequestMapper.insert(leave);

        User admin = userMapper.findByUsername("admin");
        Long adminId = admin != null ? admin.getId() : null;
        String adminName = admin != null ? (admin.getNickname() != null ? admin.getNickname() : "admin") : "admin";

        if (targetStatus == 1 || targetStatus == 2) {
            leaveRequestMapper.approve(leave.getId(), targetStatus, adminId, adminName,
                    LocalDateTime.now().minusDays(1), targetStatus == 1 ? "同意" : "理由不充分");
        } else if (targetStatus == 3) {
            leaveRequestMapper.cancel(leave.getId());
        } else if (targetStatus == 4) {
            leaveRequestMapper.approve(leave.getId(), 1, adminId, adminName,
                    LocalDateTime.now().minusDays(2), "同意");
            leaveRequestMapper.confirmReturn(leave.getId(), end.minusHours(2));
        }
    }

    private void seedCheckInsAndExceptions() {
        checkExceptionMapper.deleteByDateRange(D1, D5);
        checkInMapper.deleteByDateRange(D1, D5);

        insertCheckIn("20230001", D1, LocalTime.of(22, 15), 0, false);
        insertCheckIn("20230002", D1, LocalTime.of(23, 45), 1, true);
        insertCheckIn("20230003", D1, LocalTime.MIDNIGHT, 2, true);
        insertCheckIn("20230004", D2, LocalTime.of(22, 5), 0, false);
        insertCheckIn("20230005", D2, LocalTime.of(23, 50), 1, true);
        insertCheckIn("20230006", D1, LocalTime.of(22, 20), 0, false);
        insertCheckIn("20230016", D1, LocalTime.of(23, 55), 1, true);
        insertCheckIn("20230026", D2, LocalTime.MIDNIGHT, 2, true);
        insertCheckIn("20230031", D2, LocalTime.of(22, 30), 0, false);
        insertCheckIn("20230032", D2, LocalTime.of(23, 40), 1, true);
        insertCheckIn("20230041", D1, LocalTime.of(22, 10), 0, false);
        insertCheckIn("20230051", D3, LocalTime.of(23, 30), 1, true);
        insertCheckIn("20230061", D3, LocalTime.MIDNIGHT, 2, true);
        insertCheckIn("20230071", D3, LocalTime.of(22, 0), 0, false);
        insertCheckIn("20230010", D2, LocalTime.of(22, 25), 3, false);

        seedBulkNormalCheckIns(FILL_DATES);
        seedBulkNormalCheckIns(ALL_NORMAL_DATES);

        insertExceptionOnly("20230007", D1, 3);
        insertExceptionOnly("20230018", D2, 2);
        insertExceptionOnly("20230019", D2, 1);
        insertHandledException("20230020", D1, 1, "safe_return");
        insertHandledException("20230021", D1, 2, "reported_stay_out");
    }

    /**
     * 为尚未打卡且不在例外名单中的学生批量生成正常归寝记录。
     */
    private void seedBulkNormalCheckIns(List<LocalDate> dates) {
        int count = 0;
        for (Student student : studentMapper.findAll()) {
            if (student.getRoomId() == null) {
                continue;
            }
            String studentNo = student.getStudentNo();
            for (LocalDate date : dates) {
                if (shouldSkipNormalCheckIn(student, studentNo, date)) {
                    continue;
                }
                LocalTime time = normalCheckInTime(student.getId(), date);
                if (insertCheckIn(studentNo, date, time, 0, false)) {
                    count++;
                }
            }
        }
        System.out.println("✅ 批量正常打卡已生成: " + count + " 条（日期 "
                + dates.getFirst() + " ~ " + dates.getLast() + "）");
    }

    private boolean shouldSkipNormalCheckIn(Student student, String studentNo, LocalDate date) {
        Set<LocalDate> skipDates = EXCEPTION_ONLY_SLOTS.get(studentNo);
        if (skipDates != null && skipDates.contains(date)) {
            return true;
        }
        if (checkInMapper.findByStudentAndDate(student.getId(), date) != null) {
            return true;
        }
        LeaveRequest leave = leaveRequestMapper.findActiveLeaveByStudent(
                student.getId(), date.atTime(22, 0));
        return leave != null;
    }

    private LocalTime normalCheckInTime(Long studentId, LocalDate date) {
        int offset = (int) ((studentId + date.toEpochDay()) % 36);
        return LocalTime.of(21, 30).plusMinutes(offset);
    }

    private boolean insertCheckIn(String studentNo, LocalDate date, LocalTime time, int status, boolean withException) {
        Student student = studentMapper.findByStudentNo(studentNo);
        if (student == null || student.getRoomId() == null) {
            return false;
        }
        if (checkInMapper.findByStudentAndDate(student.getId(), date) != null) {
            return false;
        }
        CheckInRecord record = new CheckInRecord();
        record.setStudentId(student.getId());
        record.setRoomId(student.getRoomId());
        record.setCheckDate(date);
        record.setCheckTime(LocalDateTime.of(date, time));
        record.setCheckType(status == 2 ? 2 : 0);
        record.setStatus(status);
        record.setLatitude(new BigDecimal("31.230416"));
        record.setLongitude(new BigDecimal("121.473701"));
        record.setLocationAccuracy(new BigDecimal("30"));
        checkInMapper.insert(record);

        if (withException && (status == 1 || status == 2)) {
            insertException(student.getId(), date, status, record.getId(), false, null);
        }
        return true;
    }

    private void insertExceptionOnly(String studentNo, LocalDate date, int type) {
        Student student = studentMapper.findByStudentNo(studentNo);
        if (student == null) {
            return;
        }
        insertException(student.getId(), date, type, null, false, null);
    }

    private void insertHandledException(String studentNo, LocalDate date, int type, String result) {
        Student student = studentMapper.findByStudentNo(studentNo);
        if (student == null) {
            return;
        }
        insertException(student.getId(), date, type, null, true, result);
    }

    private void insertException(Long studentId, LocalDate date, int type, Long checkRecordId,
                                 boolean handled, String handleResult) {
        if (checkExceptionMapper.countByStudentDateAndType(studentId, date, type) > 0) {
            return;
        }
        CheckException ex = new CheckException();
        ex.setStudentId(studentId);
        ex.setExceptionDate(date);
        ex.setExceptionType(type);
        ex.setCheckRecordId(checkRecordId);
        checkExceptionMapper.insert(ex);
        if (handled) {
            User admin = userMapper.findByUsername("admin");
            checkExceptionMapper.handle(ex.getId(),
                    admin != null ? admin.getId() : null,
                    handleResult,
                    "演示数据：已处理");
        }
    }
}
