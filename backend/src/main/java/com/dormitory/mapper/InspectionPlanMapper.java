package com.dormitory.mapper;

import com.dormitory.model.InspectionPlan;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface InspectionPlanMapper {

    @Select("SELECT ip.*, u.nickname as creator_name " +
            "FROM inspection_plans ip " +
            "LEFT JOIN users u ON ip.creator_id = u.id " +
            "WHERE ip.id = #{id}")
    InspectionPlan findById(Long id);

    @Select("SELECT ip.*, u.nickname as creator_name " +
            "FROM inspection_plans ip " +
            "LEFT JOIN users u ON ip.creator_id = u.id " +
            "ORDER BY ip.scheduled_date DESC, ip.create_time DESC")
    List<InspectionPlan> findAll();

    @Select("SELECT ip.*, u.nickname as creator_name " +
            "FROM inspection_plans ip " +
            "LEFT JOIN users u ON ip.creator_id = u.id " +
            "ORDER BY ip.scheduled_date DESC, ip.create_time DESC " +
            "LIMIT #{offset}, #{limit}")
    List<InspectionPlan> findAllPaginated(@Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM inspection_plans")
    int count();

    @Select("SELECT ip.*, u.nickname as creator_name " +
            "FROM inspection_plans ip " +
            "LEFT JOIN users u ON ip.creator_id = u.id " +
            "WHERE ip.status = #{status} " +
            "ORDER BY ip.scheduled_date ASC")
    List<InspectionPlan> findByStatus(String status);

    @Select("SELECT ip.*, u.nickname as creator_name " +
            "FROM inspection_plans ip " +
            "LEFT JOIN users u ON ip.creator_id = u.id " +
            "WHERE ip.inspection_type = #{type} " +
            "ORDER BY ip.scheduled_date DESC")
    List<InspectionPlan> findByType(String type);

    @Insert("INSERT INTO inspection_plans (name, description, inspection_type, status, scheduled_date, " +
            "building_ids, inspector_ids, total_rooms, completed_rooms, creator_id, create_time, update_time) " +
            "VALUES (#{name}, #{description}, #{inspectionType}, #{status}, #{scheduledDate}, " +
            "#{buildingIds}, #{inspectorIds}, #{totalRooms}, #{completedRooms}, #{creatorId}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(InspectionPlan plan);

    @Update("UPDATE inspection_plans SET name = #{name}, description = #{description}, " +
            "inspection_type = #{inspectionType}, scheduled_date = #{scheduledDate}, " +
            "building_ids = #{buildingIds}, inspector_ids = #{inspectorIds}, " +
            "update_time = NOW() WHERE id = #{id}")
    int update(InspectionPlan plan);

    @Update("UPDATE inspection_plans SET status = #{status}, update_time = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    @Update("UPDATE inspection_plans SET completed_rooms = completed_rooms + 1, update_time = NOW() WHERE id = #{id}")
    int incrementCompletedRooms(@Param("id") Long id);

    @Update("UPDATE inspection_plans SET completed_rooms = GREATEST(completed_rooms - 1, 0), update_time = NOW() WHERE id = #{id}")
    int decrementCompletedRooms(@Param("id") Long id);

    @Delete("DELETE FROM inspection_plans WHERE id = #{id}")
    int delete(Long id);
}
