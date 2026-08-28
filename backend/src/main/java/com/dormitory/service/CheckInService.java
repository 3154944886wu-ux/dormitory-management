package com.dormitory.service;

import com.dormitory.mapper.CheckInMapper;
import com.dormitory.mapper.CheckRuleMapper;
import com.dormitory.mapper.LeaveRequestMapper;
import com.dormitory.mapper.CheckExceptionMapper;
import com.dormitory.mapper.RoomMapper;
import com.dormitory.mapper.StudentMapper;
import com.dormitory.model.CheckInRecord;
import com.dormitory.model.CheckRule;
import com.dormitory.model.CheckException;
import com.dormitory.model.LeaveRequest;
import com.dormitory.model.Room;
import com.dormitory.model.Student;
import com.dormitory.util.MapValueUtils;
import com.dormitory.utils.CheckWindow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CheckInService {

    @Autowired
    private CheckInMapper checkInMapper;
    
    @Autowired
    private CheckRuleMapper checkRuleMapper;
    
    @Autowired
    private LeaveRequestMapper leaveRequestMapper;
    
    @Autowired
    private CheckExceptionMapper checkExceptionMapper;
    
    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private RoomMapper roomMapper;

    @Autowired
    private OperationLogService operationLogService;

    /**
     * 学生打卡
     */
    @Transactional
    public CheckInRecord checkIn(Long studentId, Integer checkType, BigDecimal latitude, 
                                  BigDecimal longitude, BigDecimal locationAccuracy,
                                  String deviceInfo, String ipAddress) {
        LocalDateTime now = CheckWindow.now();
        LocalTime currentTime = now.toLocalTime();

        Student student = studentMapper.findById(studentId);
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }
        if (student.getRoomId() == null || student.getStatus() == null || student.getStatus() != 1) {
            throw new RuntimeException("未分配宿舍，无法打卡");
        }

        CheckRule rule = getCheckRule(student);
        if (rule == null || !CheckWindow.isInCheckWindow(currentTime, rule)) {
            throw new RuntimeException("当前不在打卡时段");
        }

        LocalDate businessDate = CheckWindow.businessDate(now, rule);
        if (!CheckWindow.appliesOn(businessDate, rule)) {
            throw new RuntimeException("今日无需归寝打卡");
        }

        CheckInRecord existing = checkInMapper.findByStudentAndDate(studentId, businessDate);
        if (existing != null) {
            throw new RuntimeException("今日已打卡");
        }

        validateLocation(studentId, latitude, longitude, locationAccuracy, rule);

        Integer status = calculateStatus(studentId, businessDate, currentTime, rule);

        CheckInRecord record = new CheckInRecord();
        record.setStudentId(studentId);
        record.setRoomId(student.getRoomId());
        record.setCheckDate(businessDate);
        record.setCheckTime(now);
        record.setCheckType(checkType);
        record.setLatitude(latitude);
        record.setLongitude(longitude);
        record.setLocationAccuracy(locationAccuracy);
        record.setDeviceInfo(deviceInfo);
        record.setIpAddress(ipAddress);
        record.setStatus(status);

        try {
            checkInMapper.insert(record);
        } catch (DuplicateKeyException e) {
            throw new RuntimeException("今日已打卡");
        }
        Map<String, Object> logDetail = new HashMap<>();
        logDetail.put("status", status);
        logDetail.put("latitude", latitude);
        logDetail.put("longitude", longitude);
        logDetail.put("accuracy", locationAccuracy);
        operationLogService.log(studentId, "student", student.getStudentNo(), "checkin.submit", logDetail);

        if (status == 1 || status == 2) {
            createException(studentId, businessDate, status, record.getId());
        }

        return checkInMapper.findById(record.getId());
    }
    
    /**
     * 获取适用的归寝规则
     */
    private CheckRule getCheckRule(Student student) {
        CheckRule rule = null;
        if (student.getRoomId() != null) {
            Room room = roomMapper.findById(student.getRoomId());
            if (room != null && room.getBuildingId() != null) {
                rule = checkRuleMapper.findByBuildingId(room.getBuildingId());
            }
        }
        if (rule == null) {
            rule = checkRuleMapper.findDefault();
        }
        return rule;
    }
    
    /**
     * 计算打卡状态：结束前的已归(0)，结束后至未归截止前为晚归(1)，未归截止后为未归(2)
     */
    private Integer calculateStatus(Long studentId, LocalDate date, LocalTime currentTime, CheckRule rule) {
        LeaveRequest leave = leaveRequestMapper.findActiveLeaveByStudent(studentId, CheckWindow.now());
        if (leave != null) {
            return 3;
        }
        if (rule == null || !CheckWindow.appliesOn(date, rule)) {
            return 0;
        }
        return CheckWindow.statusOf(currentTime, rule);
    }
    
    /**
     * 验证位置是否在允许范围内
     */
    private void validateLocation(Long studentId, BigDecimal latitude, BigDecimal longitude,
                                  BigDecimal locationAccuracy, CheckRule rule) {
        if (rule == null) {
            return;
        }

        boolean requireLocation = rule.getRequireLocation() == null || rule.getRequireLocation() == 1;
        if (!requireLocation && (rule.getAllowedLatitude() == null || rule.getAllowedLongitude() == null)) {
            return;
        }

        if (latitude == null || longitude == null) {
            logCheckInFailure(studentId, "missing_location", Map.of("ruleId", rule.getId()));
            throw new RuntimeException("请允许浏览器获取当前位置后再打卡");
        }

        if (rule.getAllowedLatitude() == null || rule.getAllowedLongitude() == null) {
            logCheckInFailure(studentId, "missing_geofence", Map.of("ruleId", rule.getId()));
            throw new RuntimeException("管理员尚未配置宿舍电子围栏，暂不能定位打卡");
        }

        int maxAccuracy = rule.getMaxLocationAccuracy() != null ? rule.getMaxLocationAccuracy() : 200;
        if (locationAccuracy != null && locationAccuracy.doubleValue() > maxAccuracy) {
            logCheckInFailure(studentId, "low_accuracy", Map.of(
                    "accuracy", locationAccuracy,
                    "maxAccuracy", maxAccuracy
            ));
            throw new RuntimeException("当前定位精度过低，请移动到开阔区域后重试");
        }

        // 计算距离（使用 Haversine 公式）
        double distance = calculateDistance(
            latitude.doubleValue(), longitude.doubleValue(),
            rule.getAllowedLatitude().doubleValue(), rule.getAllowedLongitude().doubleValue()
        );
        
        int radius = rule.getAllowedRadius() != null ? rule.getAllowedRadius() : 500;
        
        if (distance > radius) {
            logCheckInFailure(studentId, "outside_geofence", Map.of(
                    "distance", Math.round(distance),
                    "radius", radius
            ));
            throw new RuntimeException(
                String.format("您不在允许打卡范围内，当前距离：%.0f米，允许范围：%d米", distance, radius)
            );
        }
    }
    
    /**
     * 计算两点之间的距离（米），使用 Haversine 公式
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final double EARTH_RADIUS = 6371000; // 地球半径（米）
        
        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLon = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                   Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                   Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS * c;
    }

    private void logCheckInFailure(Long studentId, String reason, Map<String, Object> detail) {
        Map<String, Object> payload = new HashMap<>(detail);
        payload.put("reason", reason);
        operationLogService.log(studentId, "student", String.valueOf(studentId), "checkin.failed", payload);
    }
    
    /**
     * 创建异常记录
     */
    private void createException(Long studentId, LocalDate date, Integer exceptionType, Long checkRecordId) {
        CheckException exception = new CheckException();
        exception.setStudentId(studentId);
        exception.setExceptionDate(date);
        exception.setExceptionType(exceptionType);
        exception.setCheckRecordId(checkRecordId);
        if (checkExceptionMapper.countByStudentDateAndType(studentId, date, exceptionType) == 0) {
            try {
                checkExceptionMapper.insert(exception);
            } catch (DuplicateKeyException ignored) {
                // 唯一键并发下忽略重复插入
            }
        }
    }
    
    /**
     * 批量生成未归异常（定时任务调用）
     */
    @Transactional
    public int generateMissingCheckIns(LocalDate date) {
        int count = 0;
        for (Student student : studentMapper.findAll()) {
            if (!isResiding(student)) {
                continue;
            }
            CheckRule rule = getCheckRule(student);
            if (rule == null || !CheckWindow.appliesOn(date, rule)) {
                continue;
            }
            if (insertAbsentIfNeeded(student, date, date.atTime(LocalTime.MAX))) {
                count++;
            }
        }
        return count;
    }

    /**
     * 请假批准后：覆盖日期内的未归/晚归改为请假，并关闭对应异常；无记录则补请假打卡行。
     */
    @Transactional
    public void applyApprovedLeave(Long studentId, LocalDateTime start, LocalDateTime end) {
        if (studentId == null || start == null || end == null) {
            return;
        }
        Student student = studentMapper.findById(studentId);
        if (student == null) {
            return;
        }
        LocalDate from = start.toLocalDate();
        LocalDate to = end.toLocalDate();
        for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
            CheckInRecord record = checkInMapper.findByStudentAndDate(studentId, date);
            if (record == null) {
                CheckInRecord leaveRecord = new CheckInRecord();
                leaveRecord.setStudentId(studentId);
                leaveRecord.setRoomId(student.getRoomId());
                leaveRecord.setCheckDate(date);
                leaveRecord.setCheckTime(date.atTime(LocalTime.NOON));
                leaveRecord.setCheckType(0);
                leaveRecord.setStatus(3);
                try {
                    checkInMapper.insert(leaveRecord);
                } catch (DuplicateKeyException ignored) {
                    record = checkInMapper.findByStudentAndDate(studentId, date);
                }
            }
            if (record != null && record.getStatus() != null && record.getStatus() != 3) {
                checkInMapper.updateStatus(record.getId(), 3, "请假已批准");
            }
            checkExceptionMapper.markHandledByStudentAndDate(studentId, date, "leave_approved", "请假已批准自动关闭");
        }
    }

    /**
     * 异常处理为已安全归寝时，同步打卡记录。
     */
    public void syncRecordAfterExceptionHandled(CheckException exception, String handleResult) {
        if (exception == null || exception.getCheckRecordId() == null) {
            return;
        }
        if (!"safe_return".equals(handleResult)) {
            return;
        }
        checkInMapper.updateStatus(exception.getCheckRecordId(), 0, "异常核实：已安全归寝");
    }
    
    public CheckInRecord findById(Long id) {
        return checkInMapper.findById(id);
    }
    
    public List<CheckInRecord> findByStudentId(Long studentId) {
        return checkInMapper.findByStudentId(studentId);
    }
    
    public List<CheckInRecord> findByDate(LocalDate date) {
        return checkInMapper.findByDate(date);
    }
    
    public List<CheckInRecord> search(LocalDate startDate, LocalDate endDate, Long buildingId, Integer status) {
        LocalDate[] range = normalizeRange(startDate, endDate);
        return checkInMapper.search(range[0], range[1], buildingId, status);
    }

    public Map<String, Object> searchPaged(LocalDate startDate, LocalDate endDate, Long buildingId,
                                           Integer status, String studentName, String studentNo,
                                           int page, int size) {
        LocalDate[] range = normalizeRange(startDate, endDate);
        int offset = Math.max(page - 1, 0) * size;
        List<CheckInRecord> records = checkInMapper.searchPaged(
                range[0], range[1], buildingId, status, blankToNull(studentName), blankToNull(studentNo), offset, size);
        int total = checkInMapper.countSearch(
                range[0], range[1], buildingId, status, blankToNull(studentName), blankToNull(studentNo));

        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return result;
    }

    public Map<String, Object> searchScopedPaged(LocalDate startDate, LocalDate endDate,
                                                 String scopesJson, Integer status, int page, int size) {
        LocalDate[] range = normalizeRange(startDate, endDate);
        int offset = Math.max(page - 1, 0) * size;
        List<CheckInRecord> records = checkInMapper.searchScopedPaged(
                range[0], range[1], blankToNull(scopesJson), status, offset, size);
        int total = checkInMapper.countScopedSearch(
                range[0], range[1], blankToNull(scopesJson), status);

        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return result;
    }

    public Map<String, Object> getTodayStatus(Long studentId) {
        LocalDateTime now = CheckWindow.now();
        Student student = studentMapper.findById(studentId);
        CheckRule rule = student != null ? getCheckRule(student) : null;
        LocalDate date = CheckWindow.displayDate(now, rule);
        CheckInRecord record = checkInMapper.findByStudentAndDate(studentId, date);
        LeaveRequest leave = leaveRequestMapper.findActiveLeaveByStudent(studentId, now);

        Map<String, Object> status = new LinkedHashMap<>();
        status.put("checkedIn", record != null);
        status.put("record", record);
        status.put("checkTime", record == null ? null : record.getCheckTime());

        if (leave != null && (record == null || record.getStatus() == 3)) {
            status.put("status", 3);
        } else if (record != null) {
            status.put("status", record.getStatus());
        } else if (rule != null && CheckWindow.isInCheckWindow(now.toLocalTime(), rule)) {
            status.put("status", calculateStatus(studentId, date, now.toLocalTime(), rule));
        } else {
            status.put("status", null);
        }
        return status;
    }
    
    public Map<String, Object> getStatistics(LocalDate date) {
        Map<String, Object> stats = new HashMap<>();
        
        // 当天打卡统计
        List<Map<String, Object>> statusCounts = checkInMapper.countByDateGroupByStatus(date);
        int normalCount = 0, lateCount = 0, absentCount = 0, leaveCount = 0;
        
        for (Map<String, Object> item : statusCounts) {
            int status = MapValueUtils.intValue(item, "status", "STATUS");
            int count = MapValueUtils.intValue(item, "count", "COUNT");
            switch (status) {
                case 0 -> normalCount = count;
                case 1 -> lateCount = count;
                case 2 -> absentCount = count;
                case 3 -> leaveCount = count;
            }
        }
        
        stats.put("normalCount", normalCount);
        stats.put("lateCount", lateCount);
        stats.put("absentCount", absentCount);
        stats.put("leaveCount", leaveCount);
        stats.put("totalCount", normalCount + lateCount + absentCount + leaveCount);
        
        return stats;
    }

    public Map<String, Object> getTrendStatistics(LocalDate startDate, LocalDate endDate,
                                                   String scopesJson) {
        LocalDate[] range = normalizeRange(startDate, endDate);
        Map<String, Object> stats = new HashMap<>();
        stats.put("summary", buildCheckInSummary(range[0], range[1], scopesJson));
        stats.put("dailyTrend", normalizeDailyTrend(checkInMapper.countDailyGroupByStatus(
                range[0], range[1], blankToNull(scopesJson))));
        return stats;
    }

    private Map<String, Object> buildCheckInSummary(LocalDate start, LocalDate end,
                                                     String scopesJson) {
        Map<String, Object> summary = new HashMap<>();
        int normalCount = 0, lateCount = 0, absentCount = 0, leaveCount = 0;
        for (Map<String, Object> item : checkInMapper.countRangeGroupByStatus(
                start, end, blankToNull(scopesJson))) {
            int status = MapValueUtils.intValue(item, "status", "STATUS");
            int count = MapValueUtils.intValue(item, "count", "COUNT");
            switch (status) {
                case 0 -> normalCount = count;
                case 1 -> lateCount = count;
                case 2 -> absentCount = count;
                case 3 -> leaveCount = count;
            }
        }
        summary.put("normalCount", normalCount);
        summary.put("lateCount", lateCount);
        summary.put("absentCount", absentCount);
        summary.put("leaveCount", leaveCount);
        summary.put("totalCount", normalCount + lateCount + absentCount + leaveCount);
        return summary;
    }

    private List<Map<String, Object>> normalizeDailyTrend(List<Map<String, Object>> rows) {
        return rows.stream().map(row -> {
            Map<String, Object> normalized = new HashMap<>();
            Object date = row.get("date");
            if (date == null) {
                date = row.get("checkDate");
            }
            if (date == null) {
                date = row.get("DATE");
            }
            if (date == null) {
                date = row.get("check_date");
            }
            normalized.put("date", date != null ? date.toString().substring(0, 10) : null);
            normalized.put("status", MapValueUtils.intValue(row, "status", "STATUS"));
            normalized.put("count", MapValueUtils.intValue(row, "count", "COUNT"));
            return normalized;
        }).toList();
    }

    /**
     * 超过未归截止后，为当日未打卡且未请假的学生自动生成未归记录。
     */
    @Transactional
    public int generateAbsentAfterDeadline() {
        LocalDateTime now = CheckWindow.now();
        int count = 0;

        for (Student student : studentMapper.findAll()) {
            if (!isResiding(student)) {
                continue;
            }
            CheckRule rule = getCheckRule(student);
            if (rule == null) {
                continue;
            }
            LocalDate businessDate = CheckWindow.businessDate(now, rule);
            if (!CheckWindow.appliesOn(businessDate, rule)) {
                continue;
            }
            if (!CheckWindow.isPastAbsentDeadline(now.toLocalTime(), rule)) {
                continue;
            }
            if (insertAbsentIfNeeded(student, businessDate, now)) {
                count++;
            }
        }

        return count;
    }

    private boolean isResiding(Student student) {
        return student.getRoomId() != null && student.getStatus() != null && student.getStatus() == 1;
    }

    private boolean insertAbsentIfNeeded(Student student, LocalDate date, LocalDateTime leaveAt) {
        if (checkInMapper.findByStudentAndDate(student.getId(), date) != null) {
            return false;
        }
        LeaveRequest leave = leaveRequestMapper.findCoveringLeaveByStudent(student.getId(), leaveAt);
        if (leave == null) {
            leave = leaveRequestMapper.findActiveLeaveByStudent(student.getId(), leaveAt);
        }
        if (leave != null) {
            return false;
        }
        CheckInRecord absentRecord = new CheckInRecord();
        absentRecord.setStudentId(student.getId());
        absentRecord.setRoomId(student.getRoomId());
        absentRecord.setCheckDate(date);
        absentRecord.setCheckTime(date.atStartOfDay());
        absentRecord.setCheckType(2);
        absentRecord.setStatus(2);
        try {
            checkInMapper.insert(absentRecord);
            createException(student.getId(), date, 2, absentRecord.getId());
            return true;
        } catch (DuplicateKeyException ignored) {
            return false;
        }
    }

    private LocalDate[] normalizeRange(LocalDate startDate, LocalDate endDate) {
        LocalDate end = endDate != null ? endDate : CheckWindow.today();
        LocalDate start = startDate != null ? startDate : end.minusDays(30);
        return new LocalDate[]{start, end};
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}