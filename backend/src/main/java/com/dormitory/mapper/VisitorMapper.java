package com.dormitory.mapper;

import com.dormitory.model.Visitor;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface VisitorMapper {
    
    @Select("SELECT v.*, r.room_number, b.name as building_name " +
             "FROM visitors v " +
             "LEFT JOIN rooms r ON v.room_id = r.id " +
             "LEFT JOIN buildings b ON r.building_id = b.id " +
             "ORDER BY v.visit_time DESC")
    List<Visitor> findAll();
    
    @Select("SELECT v.*, r.room_number, b.name as building_name " +
             "FROM visitors v " +
             "LEFT JOIN rooms r ON v.room_id = r.id " +
             "LEFT JOIN buildings b ON r.building_id = b.id " +
             "WHERE v.id = #{id}")
    Visitor findById(Long id);
    
    @Select("SELECT v.*, r.room_number, b.name as building_name " +
             "FROM visitors v " +
             "LEFT JOIN rooms r ON v.room_id = r.id " +
             "LEFT JOIN buildings b ON r.building_id = b.id " +
             "WHERE v.room_id = #{roomId} " +
             "ORDER BY v.visit_time DESC")
    List<Visitor> findByRoomId(Long roomId);
    
    @Select("SELECT v.*, r.room_number, b.name as building_name " +
             "FROM visitors v " +
             "LEFT JOIN rooms r ON v.room_id = r.id " +
             "LEFT JOIN buildings b ON r.building_id = b.id " +
             "WHERE v.status = #{status} " +
             "ORDER BY v.visit_time DESC")
    List<Visitor> findByStatus(Integer status);
    
    @Select("SELECT v.*, r.room_number, b.name as building_name " +
             "FROM visitors v " +
             "LEFT JOIN rooms r ON v.room_id = r.id " +
             "LEFT JOIN buildings b ON r.building_id = b.id " +
             "WHERE v.visitor_name LIKE CONCAT('%', #{name}, '%') " +
             "ORDER BY v.visit_time DESC")
    List<Visitor> findByName(String name);
    
    @Select("SELECT v.*, r.room_number, b.name as building_name " +
             "FROM visitors v " +
             "LEFT JOIN rooms r ON v.room_id = r.id " +
             "LEFT JOIN buildings b ON r.building_id = b.id " +
             "WHERE DATE(v.visit_time) = #{date} " +
             "ORDER BY v.visit_time DESC")
    List<Visitor> findByDate(LocalDateTime date);
    
    @Insert("INSERT INTO visitors(room_id, visitor_name, visitor_phone, visitor_id_card, " +
             "relation, purpose, visit_time, status, note) " +
             "VALUES(#{roomId}, #{visitorName}, #{visitorPhone}, #{visitorIdCard}, " +
             "#{relation}, #{purpose}, #{visitTime}, #{status}, #{note})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Visitor visitor);
    
    @Update("UPDATE visitors SET room_id=#{roomId}, visitor_name=#{visitorName}, " +
             "visitor_phone=#{visitorPhone}, visitor_id_card=#{visitorIdCard}, " +
             "relation=#{relation}, purpose=#{purpose}, visit_time=#{visitTime}, " +
             "leave_time=#{leaveTime}, status=#{status}, note=#{note} WHERE id=#{id}")
    int update(Visitor visitor);
    
    @Delete("DELETE FROM visitors WHERE id = #{id}")
    int deleteById(Long id);
    
    @Select("SELECT COUNT(*) FROM visitors WHERE status = 1")
    int countActive();
}