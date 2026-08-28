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
import com.dormitory.utils.FileAccessPolicy;
import com.dormitory.utils.FileOwnership;
import org.springframework.stereotype.Service;

@Service
public class FileAccessService {

    private static final int LEAVE_SCAN_LIMIT = 10_000;

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

    public boolean canAccess(String username, String role, String publicUrl) {
        if (role != null && "ADMIN".equalsIgnoreCase(role.trim())) {
            return true;
        }
        return FileAccessPolicy.canRead(role, ownsForRole(username, role, publicUrl));
    }

    private boolean ownsForRole(String username, String role, String publicUrl) {
        if (role == null) {
            return false;
        }
        String normalized = role.trim().toUpperCase();
        if ("STUDENT".equals(normalized)) {
            return studentOwns(username, publicUrl);
        }
        if ("MANAGER".equals(normalized)) {
            return managerOwns(username, publicUrl);
        }
        return false;
    }

    public boolean studentOwns(String username, String publicUrl) {
        Student student = studentMapper.findByStudentNo(username);
        if (student == null) {
            return false;
        }
        if (student.getRoomId() != null) {
            for (InspectionRecord record : inspectionRecordMapper.findByRoomId(student.getRoomId())) {
                if (FileOwnership.containsUrl(record.getPhotos(), publicUrl)
                        || FileOwnership.containsUrl(record.getRectificationPhotos(), publicUrl)) {
                    return true;
                }
            }
        }
        if (FileOwnership.ownerUserId(publicUrl) != null) {
            return FileOwnership.uploadedBy(publicUrl, student.getUserId());
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
        return false;
    }

    private boolean managerOwns(String username, String publicUrl) {
        User manager = userMapper.findByUsername(username);
        if (manager == null || manager.getId() == null || !managerScopeService.hasScope(manager.getId())) {
            return false;
        }
        Long managerId = manager.getId();
        if (FileOwnership.uploadedBy(publicUrl, managerId)) {
            return true;
        }
        Long ownerId = FileOwnership.ownerUserId(publicUrl);
        if (ownerId != null) {
            Student ownerStudent = studentMapper.findByUserId(ownerId);
            if (ownerStudent != null
                    && managerScopeService.canSee(managerId, ownerStudent.getBuildingId(), ownerStudent.getClassName())) {
                return true;
            }
        }
        return managerCanReadLinkedDocument(managerId, publicUrl);
    }

    private boolean managerCanReadLinkedDocument(Long managerId, String publicUrl) {
        for (InspectionRecord record : inspectionRecordMapper.findAll()) {
            if (FileOwnership.containsUrl(record.getPhotos(), publicUrl)
                    || FileOwnership.containsUrl(record.getRectificationPhotos(), publicUrl)) {
                if (managerScopeService.canSeeBuilding(managerId, record.getBuildingId())) {
                    return true;
                }
            }
        }
        for (LeaveRequest leave : leaveRequestMapper.findAll(0, LEAVE_SCAN_LIMIT)) {
            if (FileOwnership.containsUrl(leave.getAttachment(), publicUrl)
                    && managerScopeService.canSee(managerId, leave.getBuildingId(), leave.getClassName())) {
                return true;
            }
        }
        for (Repair repair : repairMapper.findAll()) {
            if (FileOwnership.containsUrl(repair.getImages(), publicUrl)
                    && managerScopeService.canSee(managerId, repair.getBuildingId(), repair.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
