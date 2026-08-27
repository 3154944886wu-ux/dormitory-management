package com.dormitory.service;

import com.dormitory.mapper.LeaveRequestMapper;
import com.dormitory.mapper.StudentMapper;
import com.dormitory.model.LeaveRequest;
import com.dormitory.model.ManagerScope;
import com.dormitory.model.Student;
import com.dormitory.utils.LeaveReturn;
import com.dormitory.utils.ManagerScopeMatcher;
import com.dormitory.utils.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LeaveRequestService {

    @Autowired
    private LeaveRequestMapper leaveRequestMapper;
    
    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private ManagerScopeService managerScopeService;

    /** 不分页返回全部请假申请（供 manager 范围过滤后再分页） */
    public List<LeaveRequest> findAllList() {
        return leaveRequestMapper.findAll(0, Integer.MAX_VALUE);
    }

    /** 按宿管/辅导员的管理范围过滤请假申请列表 */
    public List<LeaveRequest> filterByManagerScope(List<LeaveRequest> list, Long managerUserId) {
        List<ManagerScope> scopes = managerScopeService.findActiveByUserId(managerUserId);
        List<LeaveRequest> result = new ArrayList<>();
        for (LeaveRequest r : list) {
            if (ManagerScopeMatcher.isVisible(scopes, r.getBuildingId(), r.getClassName())) {
                result.add(r);
            }
        }
        return result;
    }

    /**
     * 提交请假申请
     */
    @Transactional
    public LeaveRequest submit(LeaveRequest request) {
        // 验证学生存在
        Student student = studentMapper.findById(request.getStudentId());
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }
        
        // 检查时间有效性
        if (request.getStartTime() == null || request.getEndTime() == null) {
            throw new RuntimeException("请假开始时间和结束时间不能为空");
        }
        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new RuntimeException("开始时间不能晚于结束时间");
        }
        
        // 检查是否有未结束的请假
        int pendingCount = leaveRequestMapper.countPendingOrApprovedByStudent(request.getStudentId());
        if (pendingCount > 0) {
            throw new RuntimeException("已有待审批或进行中的请假申请");
        }
        
        leaveRequestMapper.insert(request);
        return leaveRequestMapper.findById(request.getId());
    }
    
    /**
     * 审批请假申请
     */
    @Transactional
    public LeaveRequest approve(Long id, Integer status, Long approverId, String approverName, String note) {
        LeaveRequest request = leaveRequestMapper.findById(id);
        if (request == null) {
            throw new RuntimeException("请假申请不存在");
        }
        
        if (request.getStatus() != 0) {
            throw new RuntimeException("该申请已处理");
        }
        
        if (status != 1 && status != 2) {
            throw new RuntimeException("无效的审批状态");
        }
        
        leaveRequestMapper.approve(id, status, approverId, approverName, LocalDateTime.now(), note);
        return leaveRequestMapper.findById(id);
    }
    
    /**
     * 撤销请假申请
     */
    @Transactional
    public void cancel(Long id, Long studentId) {
        LeaveRequest request = leaveRequestMapper.findById(id);
        if (request == null) {
            throw new RuntimeException("请假申请不存在");
        }
        
        if (!request.getStudentId().equals(studentId)) {
            throw new RuntimeException("无权撤销此申请");
        }
        
        if (request.getStatus() != 0) {
            throw new RuntimeException("只能撤销待审批的申请");
        }
        
        leaveRequestMapper.cancel(id);
    }
    
    /**
     * 销假（确认返回）
     */
    @Transactional
    public void confirmReturn(Long id, Long studentId) {
        LeaveRequest request = leaveRequestMapper.findById(id);
        if (request == null) {
            throw new RuntimeException("请假申请不存在");
        }
        
        if (!request.getStudentId().equals(studentId)) {
            throw new RuntimeException("无权操作");
        }
        
        if (!LeaveReturn.canConfirm(request.getStatus())) {
            throw new RuntimeException(request.getStatus() != null && request.getStatus() == 4
                    ? "已经销假" : "请假申请未批准");
        }

        int updated = leaveRequestMapper.confirmReturn(id, LocalDateTime.now());
        if (updated == 0) {
            throw new RuntimeException("销假失败，记录状态已变化");
        }
    }
    
    public LeaveRequest findById(Long id) {
        return leaveRequestMapper.findById(id);
    }
    
    public List<LeaveRequest> findByStudentId(Long studentId) {
        return leaveRequestMapper.findByStudentId(studentId);
    }
    
    public List<LeaveRequest> findByStatus(Integer status) {
        return leaveRequestMapper.findByStatus(status);
    }
    
    public List<LeaveRequest> findAll(int page, int size) {
        return leaveRequestMapper.findAll(Pagination.offset(page, size), Pagination.size(size));
    }
    
    public int count() {
        return leaveRequestMapper.count();
    }
    
    /**
     * 获取请假统计
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        List<LeaveRequest> pending = leaveRequestMapper.findByStatus(0);
        List<LeaveRequest> approved = leaveRequestMapper.findByStatus(1);
        
        stats.put("pendingCount", pending.size());
        stats.put("approvedCount", approved.size());
        stats.put("pending", pending);
        
        return stats;
    }
}