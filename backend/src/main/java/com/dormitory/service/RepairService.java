package com.dormitory.service;

import com.dormitory.mapper.RepairMapper;
import com.dormitory.mapper.RoomMapper;
import com.dormitory.mapper.StudentMapper;
import com.dormitory.model.Repair;
import com.dormitory.model.Room;
import com.dormitory.model.Student;
import com.dormitory.utils.RepairCompletion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RepairService {
    
    private final RepairMapper repairMapper;
    private final StudentMapper studentMapper;
    private final RoomMapper roomMapper;
    
    public RepairService(RepairMapper repairMapper, StudentMapper studentMapper, 
                        RoomMapper roomMapper) {
        this.repairMapper = repairMapper;
        this.studentMapper = studentMapper;
        this.roomMapper = roomMapper;
    }
    
    public List<Repair> findAll() {
        return repairMapper.findAll();
    }
    
    public Repair findById(Long id) {
        return repairMapper.findById(id);
    }
    
    public List<Repair> findByStudentId(Long studentId) {
        return repairMapper.findByStudentId(studentId);
    }
    
    public List<Repair> findByRoomId(Long roomId) {
        return repairMapper.findByRoomId(roomId);
    }
    
    public List<Repair> findByStatus(Integer status) {
        return repairMapper.findByStatus(status);
    }

    public List<Repair> findByRoomNumber(String roomNumber) {
        return repairMapper.findByRoomNumber(roomNumber);
    }
    
    public int getPendingCount() {
        return repairMapper.countPending();
    }
    
    public int getProcessingCount() {
        return repairMapper.countProcessing();
    }
    
    @Transactional
    public Long create(Repair repair) {
        if (repair.getRoomId() == null) {
            throw new RuntimeException("请选择房间");
        }
        Room room = roomMapper.findById(repair.getRoomId());
        if (room == null) {
            throw new RuntimeException("房间不存在");
        }
        if (repair.getStudentId() != null) {
            Student student = studentMapper.findById(repair.getStudentId());
            if (student == null) {
                throw new RuntimeException("学生不存在");
            }
        }

        repair.setStatus(0); // 待处理
        repairMapper.insert(repair);
        return repair.getId();
    }

    @Transactional
    public void update(Repair repair) {
        Repair existing = repairMapper.findById(repair.getId());
        if (existing == null) {
            throw new RuntimeException("报修记录不存在");
        }
        if (repair.getRoomId() == null) {
            throw new RuntimeException("请选择房间");
        }
        Room room = roomMapper.findById(repair.getRoomId());
        if (room == null) {
            throw new RuntimeException("房间不存在");
        }
        existing.setRoomId(repair.getRoomId());
        existing.setType(repair.getType());
        existing.setDescription(repair.getDescription());
        repairMapper.update(existing);
    }
    
    @Transactional
    public void handle(Long id, String handler, String note) {
        Repair repair = repairMapper.findById(id);
        if (repair == null) {
            throw new RuntimeException("报修记录不存在");
        }
        if (repair.getStatus() != 0) {
            throw new RuntimeException("该报修已在处理中或已完成");
        }
        
        repair.setStatus(1); // 处理中
        repair.setHandler(handler);
        repair.setHandlerNote(note);
        repair.setHandleTime(LocalDateTime.now());
        repairMapper.update(repair);
    }
    
    @Transactional
    public void complete(Long id, String note) {
        Repair repair = repairMapper.findById(id);
        if (repair == null) {
            throw new RuntimeException("报修记录不存在");
        }
        if (!RepairCompletion.canComplete(repair.getStatus())) {
            throw new RuntimeException(repair.getStatus() != null && repair.getStatus() == 3
                    ? "该报修已关闭" : "该报修已完成");
        }
        
        repair.setStatus(2); // 已完成
        if (note != null && !note.isEmpty()) {
            repair.setHandlerNote(
                (repair.getHandlerNote() != null ? repair.getHandlerNote() + "\n" : "") + note);
        }
        repair.setCompleteTime(LocalDateTime.now());
        repairMapper.update(repair);
    }
    
    @Transactional
    public void close(Long id, String note) {
        Repair repair = repairMapper.findById(id);
        if (repair == null) {
            throw new RuntimeException("报修记录不存在");
        }
        
        repair.setStatus(3); // 已关闭
        if (note != null && !note.isEmpty()) {
            repair.setHandlerNote(
                (repair.getHandlerNote() != null ? repair.getHandlerNote() + "\n" : "") + note);
        }
        repairMapper.update(repair);
    }
    
    @Transactional
    public void delete(Long id) {
        Repair repair = repairMapper.findById(id);
        if (repair == null) {
            throw new RuntimeException("报修记录不存在");
        }
        repairMapper.deleteById(id);
    }
}