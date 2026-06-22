package com.dormitory.service;

import com.dormitory.mapper.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class AllocationStatisticsService {

    private final AllocationResultMapper allocationResultMapper;
    private final DormBatchMapper batchMapper;
    private final RelocationApplicationMapper relocationMapper;

    public AllocationStatisticsService(AllocationResultMapper allocationResultMapper,
                                        DormBatchMapper batchMapper,
                                        RelocationApplicationMapper relocationMapper) {
        this.allocationResultMapper = allocationResultMapper;
        this.batchMapper = batchMapper;
        this.relocationMapper = relocationMapper;
    }

    public Map<String, Object> getBatchStats(Long batchId) {
        Map<String, Object> stats = new LinkedHashMap<>();

        var batch = batchMapper.findById(batchId);
        stats.put("batchId", batchId);
        stats.put("batchName", batch != null ? batch.getName() : "");

        // 总分配记录数
        int totalCount = allocationResultMapper.countByBatchId(batchId);
        stats.put("totalCount", totalCount);

        // 各状态分布
        List<Map<String, Object>> statusDistribution = allocationResultMapper.countGroupByStatus(batchId);
        stats.put("statusDistribution", statusDistribution);

        // 平均匹配度
        BigDecimal avgScore = allocationResultMapper.avgMatchScoreByBatchId(batchId);
        stats.put("avgMatchScore", avgScore != null ? avgScore.setScale(2, java.math.RoundingMode.HALF_UP) : BigDecimal.ZERO);

        // 匹配度分布
        Map<String, Object> scoreDistribution = allocationResultMapper.matchScoreDistribution(batchId);
        stats.put("scoreDistribution", scoreDistribution);

        // 各楼栋分布
        List<Map<String, Object>> buildingDistribution = allocationResultMapper.countGroupByBuilding(batchId);
        stats.put("buildingDistribution", buildingDistribution);

        // 各专业分布
        List<Map<String, Object>> majorDistribution = allocationResultMapper.countGroupByMajor(batchId);
        stats.put("majorDistribution", majorDistribution);

        // 重匹配次数使用率
        Map<String, Object> reallocStats = allocationResultMapper.reallocationStats(batchId);
        stats.put("reallocationStats", reallocStats);
        if (totalCount > 0) {
            Object reallocated = reallocStats.get("reallocated");
            double rate = ((Number) reallocated).doubleValue() / totalCount * 100;
            stats.put("reallocationRate", String.format("%.1f%%", rate));
        } else {
            stats.put("reallocationRate", "0.0%");
        }

        // 手动调换比例（从 relocation_application 统计 executed 状态）
        int executedCount = relocationMapper.countByBatchIdAndStatus(batchId, "executed");
        stats.put("relocationExecutedCount", executedCount);
        if (totalCount > 0) {
            double relocateRate = (double) executedCount / totalCount * 100;
            stats.put("relocationRate", String.format("%.1f%%", relocateRate));
        } else {
            stats.put("relocationRate", "0.0%");
        }

        return stats;
    }
}
