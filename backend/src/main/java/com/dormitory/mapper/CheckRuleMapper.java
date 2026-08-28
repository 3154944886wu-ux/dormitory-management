package com.dormitory.mapper;

import com.dormitory.model.CheckRule;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CheckRuleMapper {
    
    @Select("SELECT r.*, b.name as building_name FROM check_rules r " +
            "LEFT JOIN buildings b ON r.building_id = b.id WHERE r.id = #{id}")
    CheckRule findById(@Param("id") Long id);
    
    @Select("SELECT r.*, b.name as building_name FROM check_rules r " +
            "LEFT JOIN buildings b ON r.building_id = b.id " +
            "ORDER BY r.is_default DESC, r.id ASC")
    List<CheckRule> findAll();
    
    @Select("SELECT r.*, b.name as building_name FROM check_rules r " +
            "LEFT JOIN buildings b ON r.building_id = b.id " +
            "WHERE r.status = 1 ORDER BY r.is_default DESC, r.id ASC")
    List<CheckRule> findActive();
    
    @Select("SELECT r.*, b.name as building_name FROM check_rules r " +
            "LEFT JOIN buildings b ON r.building_id = b.id " +
            "WHERE r.building_id = #{buildingId} AND r.status = 1 " +
            "ORDER BY r.is_default DESC, r.id DESC LIMIT 1")
    CheckRule findByBuildingId(@Param("buildingId") Long buildingId);
    
    @Select("SELECT * FROM check_rules WHERE is_default = 1 AND status = 1 LIMIT 1")
    CheckRule findDefault();
    
    @Insert("INSERT INTO check_rules (name, building_id, check_start_time, check_end_time, " +
            "late_threshold, absent_deadline, apply_days, allow_late_count, is_default, status, remark, " +
            "allowed_latitude, allowed_longitude, allowed_radius, require_location, max_location_accuracy, exception_threshold) " +
            "VALUES (#{name}, #{buildingId}, #{checkStartTime}, #{checkEndTime}, " +
            "#{lateThreshold}, #{absentDeadline}, #{applyDays}, #{allowLateCount}, #{isDefault}, #{status}, #{remark}, " +
            "#{allowedLatitude}, #{allowedLongitude}, #{allowedRadius}, #{requireLocation}, #{maxLocationAccuracy}, #{exceptionThreshold})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CheckRule rule);
    
    @Update("UPDATE check_rules SET name = #{name}, building_id = #{buildingId}, " +
            "check_start_time = #{checkStartTime}, check_end_time = #{checkEndTime}, " +
            "late_threshold = #{lateThreshold}, absent_deadline = #{absentDeadline}, apply_days = #{applyDays}, " +
            "allow_late_count = #{allowLateCount}, is_default = #{isDefault}, " +
            "status = #{status}, remark = #{remark}, " +
            "allowed_latitude = #{allowedLatitude}, allowed_longitude = #{allowedLongitude}, " +
            "allowed_radius = #{allowedRadius}, require_location = #{requireLocation}, " +
            "max_location_accuracy = #{maxLocationAccuracy}, exception_threshold = #{exceptionThreshold} WHERE id = #{id}")
    int update(CheckRule rule);
    
    @Delete("DELETE FROM check_rules WHERE id = #{id}")
    int delete(@Param("id") Long id);
    
    @Update("UPDATE check_rules SET is_default = 0")
    int clearDefault();
}