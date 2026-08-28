package com.dormitory.service;

import com.dormitory.mapper.BuildingMapper;
import com.dormitory.mapper.RoomMapper;
import com.dormitory.mapper.StudentMapper;
import com.dormitory.mapper.BedMapper;
import com.dormitory.model.Building;
import com.dormitory.model.Room;
import com.dormitory.model.Student;
import com.dormitory.model.Bed;
import com.dormitory.utils.GenderMatcher;
import com.dormitory.utils.RoommateNames;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class StudentService {
    
    private final StudentMapper studentMapper;
    private final RoomMapper roomMapper;
    private final BuildingMapper buildingMapper;
    private final BedMapper bedMapper;

    public StudentService(StudentMapper studentMapper, RoomMapper roomMapper,
                          BuildingMapper buildingMapper, BedMapper bedMapper) {
        this.studentMapper = studentMapper;
        this.roomMapper = roomMapper;
        this.buildingMapper = buildingMapper;
        this.bedMapper = bedMapper;
    }
    
    public List<Student> findAll() {
        return studentMapper.findAll();
    }

    public List<Student> findAllWithPagination(int offset, int size) {
        return studentMapper.findAllWithPagination(offset, size);
    }

    public long countAll() {
        return studentMapper.count();
    }
    
    public Student findById(Long id) {
        return studentMapper.findById(id);
    }
    
    public Student findByStudentNo(String studentNo) {
        return studentMapper.findByStudentNo(studentNo);
    }

    public Student findByStudentNoWithRoommates(String studentNo) {
        Student student = studentMapper.findByStudentNo(studentNo);
        if (student == null || student.getRoomId() == null) {
            return student;
        }
        List<Map<String, Object>> roommates = RoommateNames.summaries(
                studentMapper.findByRoomId(student.getRoomId()), student.getId());
        student.setRoommates(roommates);
        student.setRoommateNames(RoommateNames.display(roommates));
        return student;
    }
    
    public List<Student> findByRoomId(Long roomId) {
        return studentMapper.findByRoomId(roomId);
    }

    public List<Student> findByRoomIdWithPagination(Long roomId, int offset, int size) {
        return studentMapper.findByRoomIdWithPagination(roomId, offset, size);
    }

    public long countByRoomId(Long roomId) {
        return studentMapper.countByRoomIdAll(roomId);
    }
    
    public List<Student> searchByName(String name) {
        return studentMapper.findByName(name);
    }

    public List<Student> searchByName(String name, int offset, int size) {
        return studentMapper.findByNameWithPagination(name, offset, size);
    }

    public long countByName(String name) {
        return studentMapper.countByName(name);
    }
    
    @Transactional
    public Long create(Student student) {
        // 检查学号是否已存在
        Student existing = studentMapper.findByStudentNo(student.getStudentNo());
        if (existing != null) {
            throw new RuntimeException("学号已存在");
        }

        // 如果分配了房间，分配床位
        if (student.getRoomId() != null) {
            assignRoomAndBed(student);
        } else {
            student.setStatus(1);
        }
        if (student.getCheckInDate() == null) {
            student.setCheckInDate(LocalDateTime.now());
        }

        studentMapper.insert(student);
        return student.getId();
    }
    
    @Transactional
    public void update(Student student) {
        Student existing = studentMapper.findById(student.getId());
        if (existing == null) {
            throw new RuntimeException("学生不存在");
        }
        if (existing.getStatus() != 1) {
            throw new RuntimeException("已退宿学生不能编辑");
        }

        // 检查学号是否被其他学生使用
        Student duplicate = studentMapper.findByStudentNo(student.getStudentNo());
        if (duplicate != null && !duplicate.getId().equals(student.getId())) {
            throw new RuntimeException("学号已存在");
        }

        // 管理员清空了房间 → 自动退宿
        if (student.getRoomId() == null && existing.getRoomId() != null) {
            releaseStudentResources(existing);
            student.setStatus(0);
            student.setCheckOutDate(LocalDateTime.now());
            student.setBedNumber(null);
        }
        // 更换房间
        else if (student.getRoomId() != null && !student.getRoomId().equals(existing.getRoomId())) {
            // 释放旧资源
            if (existing.getRoomId() != null) {
                releaseStudentResources(existing);
            }
            // 分配新房间+床位
            assignRoomAndBed(student);
        }
        // 同一房间但更换了床位
        else if (student.getBedNumber() != null && !student.getBedNumber().equals(existing.getBedNumber())
                && student.getRoomId() != null && student.getRoomId().equals(existing.getRoomId())) {
            // 释放旧床位
            if (existing.getBedNumber() != null) {
                releaseBed(existing.getRoomId(), existing.getBedNumber());
            }
            // 占用新床位
            Bed newBed = findBedByNumber(student.getRoomId(), student.getBedNumber());
            if (newBed != null) {
                occupyBed(newBed.getId());
            }
        }

        studentMapper.update(student);
    }

    private void releaseStudentResources(Student student) {
        if (student.getRoomId() != null) {
            int dec = roomMapper.decrementCount(student.getRoomId());
            if (dec == 0) {
                System.err.println("警告: 房间[" + student.getRoomId() + "]人数减减失败(可能已为0)");
            }
            if (student.getBedNumber() != null) {
                releaseBed(student.getRoomId(), student.getBedNumber());
            }
        }
    }

    private void releaseBed(Long roomId, String bedNumber) {
        List<Bed> beds = bedMapper.findByRoomId(roomId);
        for (Bed bed : beds) {
            if (bedNumber.equals(bed.getBedNumber())) {
                bedMapper.updateOccupied(bed.getId(), 0);
                break;
            }
        }
    }

    private Bed findBedByNumber(Long roomId, String bedNumber) {
        List<Bed> beds = bedMapper.findByRoomId(roomId);
        return beds.stream()
                .filter(b -> bedNumber.equals(b.getBedNumber()))
                .findFirst().orElse(null);
    }

    private void assignRoomAndBed(Student student) {
        Room newRoom = roomMapper.findById(student.getRoomId());
        if (newRoom == null) {
            throw new RuntimeException("目标房间不存在");
        }
        if (newRoom.getIsActive() != 1 || newRoom.getStatus() != 1) {
            throw new RuntimeException("目标房间已停用");
        }
        if (newRoom.getCurrentCount() >= newRoom.getCapacity()) {
            throw new RuntimeException("目标房间已满");
        }
        // 检查性别匹配
        Building building = buildingMapper.findById(newRoom.getBuildingId());
        if (!GenderMatcher.isCompatible(student.getGender(), building == null ? null : building.getGenderType())) {
            throw new RuntimeException("学生性别与目标楼栋类型不匹配");
        }

        // 如果指定了床位，尝试分配
        if (student.getBedNumber() != null) {
            Bed bed = findBedByNumber(student.getRoomId(), student.getBedNumber());
            if (bed == null) {
                throw new RuntimeException("床位 " + student.getBedNumber() + " 不存在");
            }
            if (bed.getIsOccupied() != null && bed.getIsOccupied() == 1) {
                Bed available = findAvailableBed(student.getRoomId());
                if (available == null) {
                    throw new RuntimeException("目标房间无可用床位");
                }
                student.setBedNumber(available.getBedNumber());
                occupyBed(available.getId());
            } else {
                occupyBed(bed.getId());
            }
        } else {
            Bed available = findAvailableBed(student.getRoomId());
            if (available == null) {
                throw new RuntimeException("目标房间无可用床位");
            }
            student.setBedNumber(available.getBedNumber());
            occupyBed(available.getId());
        }

        int inc = roomMapper.incrementCount(newRoom.getId());
        if (inc == 0) {
            throw new RuntimeException("目标房间已满，无法入住");
        }
        student.setStatus(1);
        if (student.getCheckInDate() == null) {
            student.setCheckInDate(LocalDateTime.now());
        }
    }

    private Bed findAvailableBed(Long roomId) {
        return bedMapper.findAvailableByRoomId(roomId).stream().findFirst().orElse(null);
    }
    
    @Transactional
    public void checkOut(Long id) {
        Student student = studentMapper.findById(id);
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }
        if (student.getStatus() == 0) {
            throw new RuntimeException("学生已退宿");
        }

        releaseStudentResources(student);

        student.setStatus(0);
        student.setCheckOutDate(LocalDateTime.now());
        student.setRoomId(null);
        student.setBedNumber(null);
        studentMapper.update(student);
    }
    
    @Transactional
    public void delete(Long id) {
        Student student = studentMapper.findById(id);
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }

        // 如果学生还在住，先释放资源
        if (student.getStatus() == 1 && student.getRoomId() != null) {
            releaseStudentResources(student);
        }

        studentMapper.deleteById(id);
    }
    
    @Transactional
    public void relocate(Long studentId, Long newRoomId, Long newBedId) {
        Student student = studentMapper.findById(studentId);
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }
        if (student.getStatus() != 1) {
            throw new RuntimeException("只能对在住学生执行调宿");
        }

        Room newRoom = roomMapper.findById(newRoomId);
        if (newRoom == null) {
            throw new RuntimeException("目标房间不存在");
        }
        if (newRoom.getCurrentCount() >= newRoom.getCapacity()) {
            throw new RuntimeException("目标房间已满");
        }
        if (newRoom.getIsActive() != 1 || newRoom.getStatus() != 1) {
            throw new RuntimeException("目标房间已停用");
        }

        // 校验性别匹配
        Building building = buildingMapper.findById(newRoom.getBuildingId());
        if (!GenderMatcher.isCompatible(student.getGender(), building == null ? null : building.getGenderType())) {
            throw new RuntimeException("学生性别与目标楼栋类型不匹配");
        }

        Bed bed = bedMapper.findById(newBedId);
        if (bed == null || !bed.getRoomId().equals(newRoomId)) {
            throw new RuntimeException("床位不存在或不属于目标房间");
        }

        // 释放旧房间/床位
        releaseStudentResources(student);

        // 占用新房间/床位
        int inc = roomMapper.incrementCount(newRoomId);
        if (inc == 0) {
            throw new RuntimeException("目标房间已满，无法调宿");
        }
        occupyBed(newBedId);

        student.setRoomId(newRoomId);
        student.setBedNumber(bed.getBedNumber());
        student.setCheckInDate(LocalDateTime.now());
        studentMapper.update(student);
    }

    private void occupyBed(Long bedId) {
        if (bedId == null) {
            throw new RuntimeException("床位不存在");
        }
        if (bedMapper.tryOccupy(bedId) == 0) {
            throw new RuntimeException("床位已被占用");
        }
    }
}