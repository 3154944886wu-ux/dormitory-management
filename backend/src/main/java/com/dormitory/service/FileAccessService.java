package com.dormitory.service;

import com.dormitory.mapper.InspectionRecordMapper;
import com.dormitory.mapper.LeaveRequestMapper;
import com.dormitory.mapper.RepairMapper;
import com.dormitory.mapper.StudentMapper;
import com.dormitory.model.InspectionRecord;
import com.dormitory.model.LeaveRequest;
import com.dormitory.model.Repair;
import com.dormitory.model.Student;
import com.dormitory.utils.FileOwnership;
import org.springframework.stereotype.Service;

@Service
public class FileAccessService {

    private final StudentMapper studentMapper;
    private final LeaveRequestMapper leaveRequestMapper;
    private final RepairMapper repairMapper;
    private final InspectionRecordMapper inspectionRecordMapper;

    public FileAccessService(StudentMapper studentMapper,
                             LeaveRequestMapper leaveRequestMapper,
                             RepairMapper repairMapper,
                             InspectionRecordMapper inspectionRecordMapper) {
        this.studentMapper = studentMapper;
        this.leaveRequestMapper = leaveRequestMapper;
        this.repairMapper = repairMapper;
        this.inspectionRecordMapper = inspectionRecordMapper;
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
}
