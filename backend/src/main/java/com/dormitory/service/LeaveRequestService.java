package com.dormitory.service;

import com.dormitory.mapper.LeaveRequestMapper;
import com.dormitory.mapper.StudentMapper;
import com.dormitory.model.LeaveRequest;
import com.dormitory.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LeaveRequestService {

    @Autowired
    private LeaveRequestMapper leaveRequestMapper;
    
    @Autowired
    private StudentMapper studentMapper;

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
        
        if (request.getStatus() != 1) {
            throw new RuntimeException("请假申请未批准");
        }
        
        leaveRequestMapper.confirmReturn(id, LocalDateTime.now());
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
        int offset = (page - 1) * size;
        return leaveRequestMapper.findAll(offset, size);
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