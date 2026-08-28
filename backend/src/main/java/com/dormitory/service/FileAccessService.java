package com.dormitory.service;

import com.dormitory.mapper.InspectionRecordMapper;
import com.dormitory.mapper.LeaveRequestMapper;
import com.dormitory.mapper.RepairMapper;
import com.dormitory.mapper.StudentMapper;
import com.dormitory.mapper.UserMapper;
import com.dormitory.model.InspectionRecord;
import com.dormitory.model.LeaveRequest;
import com.dormitory.model.Repair;
import com.dormitory.model.Student;
import com.dormitory.model.User;
import com.dormitory.utils.FileOwnership;
import com.dormitory.utils.ManagerScopeMatcher;
import org.springframework.stereotype.Service;

@Service
public class FileAccessService {

    private final StudentMapper studentMapper;
    private final LeaveRequestMapper leaveRequestMapper;
    private final RepairMapper repairMapper;
    private final InspectionRecordMapper inspectionRecordMapper;
    private final UserMapper userMapper;
    private final ManagerScopeService managerScopeService;

    public FileAccessService(StudentMapper studentMapper,
                             LeaveRequestMapper leaveRequestMapper,
                             RepairMapper repairMapper,
                             InspectionRecordMapper inspectionRecordMapper,
                             UserMapper userMapper,
                             ManagerScopeService managerScopeService) {
        this.studentMapper = studentMapper;
        this.leaveRequestMapper = leaveRequestMapper;
        this.repairMapper = repairMapper;
        this.inspectionRecordMapper = inspectionRecordMapper;
        this.userMapper = userMapper;
        this.managerScopeService = managerScopeService;
    }

    public boolean studentOwns(String username, String publicUrl) {
        Student student = studentMapper.findByStudentNo(username);
        if (student == null) {
            return false;
        }
        for (LeaveRequest leave : leaveRequestMapper.findByStudentId(student.getId())) {
            if (FileOwnership.containsUrl(leave.getAttachment(), publicUrl)) {
                return true;
            }
        }
        for (Repair repair : repairMapper.findByStudentId(student.getId())) {
            if (FileOwnership.containsUrl(repair.getImages(), publicUrl)) {
                return true;
            }
        }
        if (student.getRoomId() != null) {
            for (InspectionRecord record : inspectionRecordMapper.findByRoomId(student.getRoomId())) {
                if (FileOwnership.containsUrl(record.getPhotos(), publicUrl)
                        || FileOwnership.containsUrl(record.getRectificationPhotos(), publicUrl)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean managerInScope(String username, String publicUrl) {
        if (username == null || username.isBlank() || publicUrl == null) {
            return false;
        }
        User user = userMapper.findByUsername(username);
        if (user == null || user.getId() == null) {
            return false;
        }
        var scopes = managerScopeService.findActiveByUserId(user.getId());
        if (scopes == null || scopes.isEmpty()) {
            return false;
        }
        for (Repair repair : repairMapper.findAll()) {
            if (FileOwnership.containsUrl(repair.getImages(), publicUrl)) {
                return ManagerScopeMatcher.isVisible(scopes, repair.getBuildingId(), repair.getClassName());
            }
        }
        int leaveTotal = leaveRequestMapper.count();
        for (LeaveRequest leave : leaveRequestMapper.findAll(0, Math.max(leaveTotal, 1))) {
            if (FileOwnership.containsUrl(leave.getAttachment(), publicUrl)) {
                return ManagerScopeMatcher.isVisible(scopes, leave.getBuildingId(), leave.getClassName());
            }
        }
        for (InspectionRecord record : inspectionRecordMapper.findAll()) {
            if (FileOwnership.containsUrl(record.getPhotos(), publicUrl)
                    || FileOwnership.containsUrl(record.getRectificationPhotos(), publicUrl)) {
                return managerScopeService.canSeeRoom(user.getId(), record.getBuildingId(), record.getRoomId());
            }
        }
        return false;
    }
}
