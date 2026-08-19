package com.dormitory.mapper;

import com.dormitory.model.Repair;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RepairMapper {
    
    @Select("SELECT r.*, s.name as student_name, s.student_no, " +
             "rm.room_number, b.name as building_name " +
             "FROM repairs r " +
             "LEFT JOIN students s ON r.student_id = s.id " +
             "LEFT JOIN rooms rm ON r.room_id = rm.id " +
             "LEFT JOIN buildings b ON rm.building_id = b.id " +
             "ORDER BY r.status, r.create_time DESC")
    List<Repair> findAll();
    
    @Select("SELECT r.*, s.name as student_name, s.student_no, " +
             "rm.room_number, b.name as building_name " +
             "FROM repairs r " +
             "LEFT JOIN students s ON r.student_id = s.id " +
             "LEFT JOIN rooms rm ON r.room_id = rm.id " +
             "LEFT JOIN buildings b ON rm.building_id = b.id " +
             "WHERE r.id = #{id}")
    Repair findById(Long id);
    
    @Select("SELECT r.*, s.name as student_name, s.student_no, " +
             "rm.room_number, b.name as building_name " +
             "FROM repairs r " +
             "LEFT JOIN students s ON r.student_id = s.id " +
             "LEFT JOIN rooms rm ON r.room_id = rm.id " +
             "LEFT JOIN buildings b ON rm.building_id = b.id " +
             "WHERE r.student_id = #{studentId} " +
             "ORDER BY r.create_time DESC")
    List<Repair> findByStudentId(Long studentId);
    
    @Select("SELECT r.*, s.name as student_name, s.student_no, " +
             "rm.room_number, b.name as building_name " +
             "FROM repairs r " +
             "LEFT JOIN students s ON r.student_id = s.id " +
             "LEFT JOIN rooms rm ON r.room_id = rm.id " +
             "LEFT JOIN buildings b ON rm.building_id = b.id " +
             "WHERE r.room_id = #{roomId} " +
             "ORDER BY r.create_time DESC")
    List<Repair> findByRoomId(Long roomId);
    
    @Select("SELECT r.*, s.name as student_name, s.student_no, " +
             "rm.room_number, b.name as building_name " +
             "FROM repairs r " +
             "LEFT JOIN students s ON r.student_id = s.id " +
             "LEFT JOIN rooms rm ON r.room_id = rm.id " +
             "LEFT JOIN buildings b ON rm.building_id = b.id " +
             "WHERE r.status = #{status} " +
             "ORDER BY r.create_time DESC")
    List<Repair> findByStatus(Integer status);

    @Select("SELECT r.*, s.name as student_name, s.student_no, " +
             "rm.room_number, b.name as building_name " +
             "FROM repairs r " +
             "LEFT JOIN students s ON r.student_id = s.id " +
             "LEFT JOIN rooms rm ON r.room_id = rm.id " +
             "LEFT JOIN buildings b ON rm.building_id = b.id " +
             "WHERE rm.room_number LIKE CONCAT('%', #{roomNumber}, '%') " +
             "ORDER BY r.status, r.create_time DESC")
    List<Repair> findByRoomNumber(String roomNumber);
    
    @Insert("INSERT INTO repairs(student_id, room_id, type, description, images, status) " +
             "VALUES(#{studentId}, #{roomId}, #{type}, #{description}, #{images}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Repair repair);
    
    @Update("UPDATE repairs SET student_id=#{studentId}, room_id=#{roomId}, " +
             "type=#{type}, description=#{description}, images=#{images}, " +
             "status=#{status}, handler=#{handler}, handler_note=#{handlerNote}, " +
             "handle_time=#{handleTime}, complete_time=#{completeTime} WHERE id=#{id}")
    int update(Repair repair);
    
    @Delete("DELETE FROM repairs WHERE id = #{id}")
    int deleteById(Long id);
    
    @Select("SELECT COUNT(*) FROM repairs WHERE status = 0")
    int countPending();
    
    @Select("SELECT COUNT(*) FROM repairs WHERE status = 1")
    int countProcessing();
    
    @Select("SELECT COUNT(*) FROM repairs WHERE status = #{status}")
    int countByStatus(Integer status);
}