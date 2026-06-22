package com.dormitory.mapper;

import com.dormitory.model.LeaveRequest;
import org.apache.ibatis.annotations.*;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface LeaveRequestMapper {
    
    @Select("SELECT l.*, s.name as student_name, s.student_no, s.department, s.class_name, " +
            "r.room_number, b.name as building_name " +
            "FROM leave_requests l " +
            "LEFT JOIN students s ON l.student_id = s.id " +
            "LEFT JOIN rooms r ON s.room_id = r.id " +
            "LEFT JOIN buildings b ON r.building_id = b.id " +
            "WHERE l.id = #{id}")
    LeaveRequest findById(@Param("id") Long id);
    
    @Select("SELECT l.*, s.name as student_name, s.student_no, s.department, s.class_name, " +
            "r.room_number, b.name as building_name " +
            "FROM leave_requests l " +
            "LEFT JOIN students s ON l.student_id = s.id " +
            "LEFT JOIN rooms r ON s.room_id = r.id " +
            "LEFT JOIN buildings b ON r.building_id = b.id " +
            "ORDER BY l.create_time DESC " +
            "LIMIT #{offset}, #{limit}")
    List<LeaveRequest> findAll(@Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT COUNT(*) FROM leave_requests")
    int count();
    
    @Select("SELECT l.*, s.name as student_name, s.student_no, s.department, s.class_name, " +
            "r.room_number, b.name as building_name " +
            "FROM leave_requests l " +
            "LEFT JOIN students s ON l.student_id = s.id " +
            "LEFT JOIN rooms r ON s.room_id = r.id " +
            "LEFT JOIN buildings b ON r.building_id = b.id " +
            "WHERE l.student_id = #{studentId} " +
            "ORDER BY l.create_time DESC")
    List<LeaveRequest> findByStudentId(@Param("studentId") Long studentId);
    
    @Select("SELECT l.*, s.name as student_name, s.student_no, s.department, s.class_name, " +
            "r.room_number, b.name as building_name " +
            "FROM leave_requests l " +
            "LEFT JOIN students s ON l.student_id = s.id " +
            "LEFT JOIN rooms r ON s.room_id = r.id " +
            "LEFT JOIN buildings b ON r.building_id = b.id " +
            "WHERE l.status = #{status} " +
            "ORDER BY l.create_time DESC")
    List<LeaveRequest> findByStatus(@Param("status") Integer status);
    
    @Select("SELECT l.*, s.name as student_name, s.student_no, s.department, s.class_name, " +
            "r.room_number, b.name as building_name " +
            "FROM leave_requests l " +
            "LEFT JOIN students s ON l.student_id = s.id " +
            "LEFT JOIN rooms r ON s.room_id = r.id " +
            "LEFT JOIN buildings b ON r.building_id = b.id " +
            "WHERE l.student_id = #{studentId} AND l.status = 1 " +
            "AND #{now} BETWEEN l.start_time AND l.end_time")
    LeaveRequest findActiveLeaveByStudent(@Param("studentId") Long studentId, @Param("now") LocalDateTime now);
    
    @Insert("INSERT INTO leave_requests (student_id, leave_type, reason, start_time, end_time, " +
            "contact_phone, destination, attachment, status) " +
            "VALUES (#{studentId}, #{leaveType}, #{reason}, #{startTime}, #{endTime}, " +
            "#{contactPhone}, #{destination}, #{attachment}, 0)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(LeaveRequest request);
    
    @Update("UPDATE leave_requests SET status = #{status}, approver_id = #{approverId}, " +
            "approver_name = #{approverName}, approve_time = #{approveTime}, approve_note = #{approveNote} " +
            "WHERE id = #{id}")
    int approve(@Param("id") Long id, @Param("status") Integer status, 
                @Param("approverId") Long approverId, @Param("approverName") String approverName,
                @Param("approveTime") LocalDateTime approveTime, @Param("approveNote") String approveNote);
    
    @Update("UPDATE leave_requests SET status = 3 WHERE id = #{id} AND status = 0")
    int cancel(@Param("id") Long id);
    
    @Update("UPDATE leave_requests SET status = 4, actual_return_time = #{actualReturnTime} " +
            "WHERE id = #{id}")
    int confirmReturn(@Param("id") Long id, @Param("actualReturnTime") LocalDateTime actualReturnTime);
    
    @Select("SELECT COUNT(*) FROM leave_requests WHERE student_id = #{studentId} AND status IN (0, 1)")
    int countPendingOrApprovedByStudent(@Param("studentId") Long studentId);
}