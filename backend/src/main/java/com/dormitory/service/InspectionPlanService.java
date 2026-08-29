package com.dormitory.service;

import com.dormitory.mapper.InspectionPlanMapper;
import com.dormitory.mapper.InspectionRecordMapper;
import com.dormitory.model.InspectionPlan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InspectionPlanService {

    @Autowired
    private InspectionPlanMapper planMapper;

    @Autowired
    private InspectionRecordMapper recordMapper;

    public InspectionPlan findById(Long id) {
        return planMapper.findById(id);
    }

    public List<InspectionPlan> findAll() {
        return planMapper.findAll();
    }

    public List<InspectionPlan> findAll(int page, int size) {
        return planMapper.findAllPaginated(com.dormitory.utils.Pagination.offset(page, size),
                com.dormitory.utils.Pagination.size(size));
    }

    public int count() {
        return planMapper.count();
    }

    public List<InspectionPlan> findByStatus(String status) {
        return planMapper.findByStatus(status);
    }

    public List<InspectionPlan> findByType(String type) {
        return planMapper.findByType(type);
    }

    @Transactional
    public InspectionPlan create(InspectionPlan plan) {
        if (plan.getStatus() == null) {
            plan.setStatus("DRAFT"); // 默认草稿状态
        }
        plan.setCompletedRooms(0);
        if (plan.getTotalRooms() == null) {
            plan.setTotalRooms(0);
        }
        planMapper.insert(plan);
        return plan;
    }

    @Transactional
    public InspectionPlan update(InspectionPlan plan) {
        planMapper.update(plan);
        return planMapper.findById(plan.getId());
    }

    @Transactional
    public InspectionPlan updateStatus(Long id, String status) {
        InspectionPlan plan = planMapper.findById(id);
        if (plan == null) {
            throw new RuntimeException("检查计划不存在");
        }
        // 验证状态流转合法性
        validateStatusTransition(plan.getStatus(), status);
        planMapper.updateStatus(id, status);
        return planMapper.findById(id);
    }

    @Transactional
    public InspectionPlan startPlan(Long id) {
        InspectionPlan plan = planMapper.findById(id);
        if (plan == null) {
            throw new RuntimeException("检查计划不存在");
        }
        if (!"DRAFT".equals(plan.getStatus()) && !"SCHEDULED".equals(plan.getStatus())) {
            throw new RuntimeException("只有草稿或已安排状态的计划才能开始执行");
        }
        planMapper.updateStatus(id, "IN_PROGRESS");
        return planMapper.findById(id);
    }

    @Transactional
    public InspectionPlan completePlan(Long id) {
        InspectionPlan plan = planMapper.findById(id);
        if (plan == null) {
            throw new RuntimeException("检查计划不存在");
        }
        if (!"IN_PROGRESS".equals(plan.getStatus())) {
            throw new RuntimeException("只有进行中的计划才能完成");
        }
        if (recordMapper.countPendingRectificationByPlanId(id) > 0) {
            throw new RuntimeException("仍有待整改记录，不能完成计划");
        }
        planMapper.updateStatus(id, "COMPLETED");
        return planMapper.findById(id);
    }

    @Transactional
    public InspectionPlan cancelPlan(Long id) {
        InspectionPlan plan = planMapper.findById(id);
        if (plan == null) {
            throw new RuntimeException("检查计划不存在");
        }
        if ("COMPLETED".equals(plan.getStatus())) {
            throw new RuntimeException("已完成的计划不能取消");
        }
        planMapper.updateStatus(id, "CANCELLED");
        return planMapper.findById(id);
    }

    @Transactional
    public void delete(Long id) {
        planMapper.delete(id);
    }

    /**
     * 验证状态流转是否合法
     * DRAFT -> SCHEDULED, CANCELLED
     * SCHEDULED -> IN_PROGRESS, CANCELLED
     * IN_PROGRESS -> COMPLETED, CANCELLED
     * COMPLETED -> (终态)
     * CANCELLED -> (终态)
     */
    private void validateStatusTransition(String currentStatus, String newStatus) {
        if (currentStatus.equals(newStatus)) {
            return; // 相同状态允许
        }
        boolean valid = switch (currentStatus) {
            case "DRAFT" -> "SCHEDULED".equals(newStatus) || "CANCELLED".equals(newStatus);
            case "SCHEDULED" -> "IN_PROGRESS".equals(newStatus) || "CANCELLED".equals(newStatus);
            case "IN_PROGRESS" -> "COMPLETED".equals(newStatus) || "CANCELLED".equals(newStatus);
            case "COMPLETED", "CANCELLED" -> false; // 终态不允许变更
            default -> false;
        };
        if (!valid) {
            throw new RuntimeException(
                String.format("不允许从 %s 状态变更到 %s 状态", currentStatus, newStatus));
        }
    }
}
