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
import org.springframework.beans.factory.annotation.Autowired;
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
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        LocalTime currentTime = now.toLocalTime();
        
        // 检查是否已打卡
        CheckInRecord existing = checkInMapper.findByStudentAndDate(studentId, today);
        if (existing != null) {
            throw new RuntimeException("今日已打卡");
        }
        
        Student student = studentMapper.findById(studentId);
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }
        
        // 获取归寝规则
        CheckRule rule = getCheckRule(student);
        
        // 位置验证
        validateLocation(studentId, latitude, longitude, locationAccuracy, rule);
        
        // 计算打卡状态
        Integer status = calculateStatus(studentId, today, currentTime, rule);
        
        // 创建打卡记录
        CheckInRecord record = new CheckInRecord();
        record.setStudentId(studentId);
        record.setRoomId(student.getRoomId());
        record.setCheckDate(today);
        record.setCheckTime(now);
        record.setCheckType(checkType);
        record.setLatitude(latitude);
        record.setLongitude(longitude);
        record.setLocationAccuracy(locationAccuracy);
        record.setDeviceInfo(deviceInfo);
        record.setIpAddress(ipAddress);
        record.setStatus(status);
        
        checkInMapper.insert(record);
        Map<String, Object> logDetail = new HashMap<>();
        logDetail.put("status", status);
        logDetail.put("latitude", latitude);
        logDetail.put("longitude", longitude);
        logDetail.put("accuracy", locationAccuracy);
        operationLogService.log(studentId, "student", student.getStudentNo(), "checkin.submit", logDetail);
        
        // 如果是晚归或未归，创建异常记录
        if (status == 1 || status == 2) {
            createException(studentId, today, status, record.getId());
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
        LeaveRequest leave = leaveRequestMapper.findActiveLeaveByStudent(studentId, LocalDateTime.now());
        if (leave != null) {
            return 3;
        }

        if (rule == null || !appliesOn(date, rule)) {
            return 0;
        }

        LocalTime start = rule.getCheckStartTime() != null ? rule.getCheckStartTime() : rule.getCheckEndTime();
        LocalTime end = rule.getCheckEndTime();
        LocalTime absent = rule.getAbsentDeadline();
        if (start == null || end == null || absent == null) {
            return 0;
        }

        int currentMin = toWindowMinutes(currentTime, start);
        int endMin = toWindowMinutes(end, start);
        int absentMin = toWindowMinutes(absent, start);

        if (currentMin > absentMin) {
            return 2;
        }
        if (currentMin > endMin) {
            return 1;
        }
        return 0;
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
            checkExceptionMapper.insert(exception);
        }
    }
    
    /**
     * 批量生成未归异常（定时任务调用）
     */
    @Transactional
    public int generateMissingCheckIns(LocalDate date) {
        List<Student> allStudents = studentMapper.findAll();
        int count = 0;
        
        for (Student student : allStudents) {
            if (student.getRoomId() == null) continue;
            CheckRule rule = getCheckRule(student);
            if (rule == null || !appliesOn(date, rule)) continue;
            
            CheckInRecord record = checkInMapper.findByStudentAndDate(student.getId(), date);
            if (record == null) {
                // 检查是否有请假
                LeaveRequest leave = leaveRequestMapper.findActiveLeaveByStudent(
                    student.getId(), 
                    date.atTime(LocalTime.MAX)
                );
                
                if (leave == null) {
                    if (checkExceptionMapper.countByStudentDateAndType(student.getId(), date, 2) == 0) {
                        CheckException exception = new CheckException();
                        exception.setStudentId(student.getId());
                        exception.setExceptionDate(date);
                        exception.setExceptionType(2); // 未归
                        checkExceptionMapper.insert(exception);
                        count++;
                    }
                }
            }
        }
        
        return count;
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
                                                 String buildingIdsCsv, String classNamesCsv,
                                                 Integer status, int page, int size) {
        LocalDate[] range = normalizeRange(startDate, endDate);
        int offset = Math.max(page - 1, 0) * size;
        List<CheckInRecord> records = checkInMapper.searchScopedPaged(
                range[0], range[1], blankToNull(buildingIdsCsv), blankToNull(classNamesCsv), status, offset, size);
        int total = checkInMapper.countScopedSearch(
                range[0], range[1], blankToNull(buildingIdsCsv), blankToNull(classNamesCsv), status);

        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return result;
    }

    public Map<String, Object> getTodayStatus(Long studentId) {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        CheckInRecord record = checkInMapper.findByStudentAndDate(studentId, today);
        LeaveRequest leave = leaveRequestMapper.findActiveLeaveByStudent(studentId, LocalDateTime.now());

        Map<String, Object> status = new LinkedHashMap<>();
        status.put("checkedIn", record != null);
        status.put("record", record);
        status.put("checkTime", record == null ? null : record.getCheckTime());

        if (leave != null && record == null) {
            status.put("status", 3);
        } else if (record != null) {
            status.put("status", record.getStatus());
        } else {
            Student student = studentMapper.findById(studentId);
            CheckRule rule = student != null ? getCheckRule(student) : null;
            status.put("status", calculateStatus(studentId, today, now, rule));
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
                                                   String buildingIdsCsv, String classNamesCsv) {
        LocalDate[] range = normalizeRange(startDate, endDate);
        Map<String, Object> stats = new HashMap<>();
        stats.put("summary", buildCheckInSummary(range[0], range[1], buildingIdsCsv, classNamesCsv));
        stats.put("dailyTrend", normalizeDailyTrend(checkInMapper.countDailyGroupByStatus(
                range[0], range[1], blankToNull(buildingIdsCsv), blankToNull(classNamesCsv))));
        return stats;
    }

    private Map<String, Object> buildCheckInSummary(LocalDate start, LocalDate end,
                                                     String buildingIdsCsv, String classNamesCsv) {
        Map<String, Object> summary = new HashMap<>();
        int normalCount = 0, lateCount = 0, absentCount = 0, leaveCount = 0;
        for (Map<String, Object> item : checkInMapper.countRangeGroupByStatus(
                start, end, blankToNull(buildingIdsCsv), blankToNull(classNamesCsv))) {
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
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        int count = 0;

        for (Student student : studentMapper.findAll()) {
            if (student.getRoomId() == null) {
                continue;
            }
            CheckRule rule = getCheckRule(student);
            if (rule == null || !appliesOn(today, rule)) {
                continue;
            }

            LocalTime start = rule.getCheckStartTime() != null ? rule.getCheckStartTime() : rule.getCheckEndTime();
            LocalTime absent = rule.getAbsentDeadline();
            if (start == null || absent == null) {
                continue;
            }
            if (toWindowMinutes(now, start) <= toWindowMinutes(absent, start)) {
                continue;
            }

            if (checkInMapper.findByStudentAndDate(student.getId(), today) != null) {
                continue;
            }

            LeaveRequest leave = leaveRequestMapper.findActiveLeaveByStudent(student.getId(), LocalDateTime.now());
            if (leave != null) {
                continue;
            }

            CheckInRecord absentRecord = new CheckInRecord();
            absentRecord.setStudentId(student.getId());
            absentRecord.setRoomId(student.getRoomId());
            absentRecord.setCheckDate(today);
            absentRecord.setCheckTime(today.atTime(absent));
            absentRecord.setCheckType(2);
            absentRecord.setStatus(2);
            checkInMapper.insert(absentRecord);
            createException(student.getId(), today, 2, absentRecord.getId());
            count++;
        }

        return count;
    }

    private int toWindowMinutes(LocalTime time, LocalTime windowStart) {
        int minutes = time.toSecondOfDay() / 60;
        int anchor = windowStart.toSecondOfDay() / 60;
        if (minutes <= anchor && !time.equals(windowStart)) {
            minutes += 24 * 60;
        }
        return minutes;
    }

    private boolean appliesOn(LocalDate date, CheckRule rule) {
        if (rule.getApplyDays() == null || rule.getApplyDays().isBlank()) {
            return true;
        }
        int dayOfWeek = date.getDayOfWeek().getValue();
        for (String day : rule.getApplyDays().split(",")) {
            if (!day.isBlank() && Integer.parseInt(day.trim()) == dayOfWeek) {
                return true;
            }
        }
        return false;
    }

    private LocalDate[] normalizeRange(LocalDate startDate, LocalDate endDate) {
        LocalDate end = endDate != null ? endDate : LocalDate.now();
        LocalDate start = startDate != null ? startDate : end.minusDays(30);
        return new LocalDate[]{start, end};
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}