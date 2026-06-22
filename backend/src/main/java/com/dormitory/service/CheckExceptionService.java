package com.dormitory.service;

import com.dormitory.mapper.CheckExceptionMapper;
import com.dormitory.model.CheckException;
import com.dormitory.util.MapValueUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CheckExceptionService {

    @Autowired
    private CheckExceptionMapper checkExceptionMapper;

    @Autowired
    private OperationLogService operationLogService;

    public CheckException findById(Long id) {
        return checkExceptionMapper.findById(id);
    }

    public List<CheckException> findAll(int page, int size) {
        int offset = (page - 1) * size;
        return checkExceptionMapper.findAll(offset, size);
    }

    public int count() {
        return checkExceptionMapper.count();
    }

    public List<CheckException> findByDate(LocalDate date) {
        return checkExceptionMapper.findByDate(date);
    }

    public List<CheckException> findByStudentId(Long studentId) {
        return checkExceptionMapper.findByStudentId(studentId);
    }

    public List<CheckException> findByHandled(Integer handled) {
        return checkExceptionMapper.findByHandled(handled);
    }

    public List<CheckException> search(LocalDate startDate, LocalDate endDate, 
                                       Long buildingId, Integer exceptionType, Integer handled) {
        LocalDate[] range = normalizeRange(startDate, endDate);
        return checkExceptionMapper.search(range[0], range[1], buildingId, exceptionType, handled);
    }

    public List<CheckException> searchScoped(LocalDate startDate, LocalDate endDate,
                                             String buildingIdsCsv, String classNamesCsv,
                                             Integer exceptionType, Integer handled) {
        LocalDate[] range = normalizeRange(startDate, endDate);
        return checkExceptionMapper.searchScoped(range[0], range[1], blankToNull(buildingIdsCsv), blankToNull(classNamesCsv), exceptionType, handled);
    }

    @Transactional
    public void handle(Long id, Long handlerId, String handlerName, String handleResult, String handleNote) {
        CheckException exception = checkExceptionMapper.findById(id);
        if (exception == null) {
            throw new RuntimeException("异常记录不存在");
        }
        
        if (exception.getHandled() == 1) {
            throw new RuntimeException("该异常已处理");
        }
        
        checkExceptionMapper.handle(id, handlerId, handleResult, handleNote);
        operationLogService.log(exception.getStudentId(), "manager", handlerName, "check_exception.handle", Map.of(
                "exceptionId", id,
                "result", handleResult == null ? "" : handleResult,
                "note", handleNote == null ? "" : handleNote
        ));
    }

    /**
     * 获取异常统计
     */
    public Map<String, Object> getStatistics(LocalDate date) {
        Map<String, Object> stats = new HashMap<>();
        
        List<Map<String, Object>> typeCounts = checkExceptionMapper.countByDateGroupByType(date);
        int lateCount = 0, absentCount = 0, missingCount = 0;
        
        for (Map<String, Object> item : typeCounts) {
            int type = MapValueUtils.intValue(item, "exception_type", "type", "TYPE");
            int count = MapValueUtils.intValue(item, "count", "COUNT");
            switch (type) {
                case 1 -> lateCount = count;
                case 2 -> absentCount = count;
                case 3 -> missingCount = count;
            }
        }
        
        stats.put("lateCount", lateCount);
        stats.put("absentCount", absentCount);
        stats.put("missingCount", missingCount);
        stats.put("totalCount", lateCount + absentCount + missingCount);
        
        // 未处理数量
        List<CheckException> unhandled = checkExceptionMapper.findByHandled(0);
        stats.put("unhandledCount", unhandled.size());
        
        return stats;
    }
    
    /**
     * 获取日期范围内的异常数量
     */
    public int countBetweenDates(LocalDate startDate, LocalDate endDate) {
        LocalDate[] range = normalizeRange(startDate, endDate);
        return checkExceptionMapper.countBetweenDates(range[0], range[1]);
    }

    public Map<String, Object> getTrendStatistics(LocalDate startDate, LocalDate endDate) {
        LocalDate[] range = normalizeRange(startDate, endDate);
        Map<String, Object> stats = new HashMap<>();
        stats.put("byBuilding", checkExceptionMapper.countByBuilding(range[0], range[1]));
        stats.put("byClass", checkExceptionMapper.countByClassName(range[0], range[1]));
        stats.put("summary", buildExceptionSummary(range[0], range[1], null, null));
        return stats;
    }

    public Map<String, Object> getScopedTrendStatistics(LocalDate startDate, LocalDate endDate,
                                                        String buildingIdsCsv, String classNamesCsv) {
        LocalDate[] range = normalizeRange(startDate, endDate);
        List<CheckException> exceptions = searchScoped(startDate, endDate, buildingIdsCsv, classNamesCsv, null, null);
        Map<String, Map<String, Integer>> building = new HashMap<>();
        Map<String, Map<String, Integer>> className = new HashMap<>();

        for (CheckException exception : exceptions) {
            addCount(building, exception.getBuildingName(), exception.getExceptionType(), exception.getHandled());
            addCount(className, exception.getClassName(), exception.getExceptionType(), exception.getHandled());
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("byBuilding", flattenCounts(building));
        stats.put("byClass", flattenCounts(className));
        stats.put("summary", buildExceptionSummary(range[0], range[1], buildingIdsCsv, classNamesCsv));
        return stats;
    }

    private Map<String, Object> buildExceptionSummary(LocalDate start, LocalDate end,
                                                       String buildingIdsCsv, String classNamesCsv) {
        Map<String, Object> summary = new HashMap<>();
        int lateCount = 0, absentCount = 0, missingCount = 0;
        int absentHandledCount = 0, absentUnhandledCount = 0;

        for (Map<String, Object> item : checkExceptionMapper.countRangeGroupByTypeAndHandled(
                start, end, blankToNull(buildingIdsCsv), blankToNull(classNamesCsv))) {
            int type = MapValueUtils.intValue(item, "type", "exception_type", "TYPE");
            int handled = MapValueUtils.intValue(item, "handled", "HANDLED");
            int count = MapValueUtils.intValue(item, "count", "COUNT");
            switch (type) {
                case 1 -> lateCount += count;
                case 2 -> {
                    absentCount += count;
                    if (handled == 1) {
                        absentHandledCount += count;
                    } else {
                        absentUnhandledCount += count;
                    }
                }
                case 3 -> missingCount += count;
            }
        }

        summary.put("lateCount", lateCount);
        summary.put("absentCount", absentCount);
        summary.put("absentHandledCount", absentHandledCount);
        summary.put("absentUnhandledCount", absentUnhandledCount);
        summary.put("missingCount", missingCount);
        summary.put("totalCount", lateCount + absentCount + missingCount);
        summary.put("unhandledCount", checkExceptionMapper.countUnhandledInRange(
                start, end, blankToNull(buildingIdsCsv), blankToNull(classNamesCsv)));
        return summary;
    }

    private void addCount(Map<String, Map<String, Integer>> target, String name, Integer type, Integer handled) {
        String key = (name == null || name.isBlank()) ? "未分组" : name;
        String bucket = type + ":" + (handled != null && handled == 1 ? "1" : "0");
        target.computeIfAbsent(key, ignored -> new HashMap<>())
                .merge(bucket, 1, Integer::sum);
    }

    private List<Map<String, Object>> flattenCounts(Map<String, Map<String, Integer>> source) {
        return source.entrySet().stream()
                .flatMap(entry -> entry.getValue().entrySet().stream().map(typeEntry -> {
                    String[] parts = typeEntry.getKey().split(":");
                    Map<String, Object> row = new HashMap<>();
                    row.put("name", entry.getKey());
                    row.put("type", Integer.parseInt(parts[0]));
                    row.put("handled", Integer.parseInt(parts[1]));
                    row.put("count", typeEntry.getValue());
                    return row;
                }))
                .toList();
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