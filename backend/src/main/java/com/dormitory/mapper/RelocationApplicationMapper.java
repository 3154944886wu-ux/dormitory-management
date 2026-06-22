package com.dormitory.mapper;

import com.dormitory.model.RelocationApplication;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RelocationApplicationMapper {

    @Select("SELECT ra.*, s.name as student_name, s.student_no, " +
            "r1.room_number as current_room_number, bk1.bed_number as current_bed_number, " +
            "bd1.name as current_building_name, " +
            "r2.room_number as new_room_number, bk2.bed_number as new_bed_number, " +
            "bd2.name as new_building_name " +
            "FROM relocation_application ra " +
            "LEFT JOIN students s ON ra.student_id = s.id " +
            "LEFT JOIN rooms r1 ON ra.current_room_id = r1.id " +
            "LEFT JOIN bed bk1 ON ra.current_bed_id = bk1.id " +
            "LEFT JOIN buildings bd1 ON r1.building_id = bd1.id " +
            "LEFT JOIN rooms r2 ON ra.new_room_id = r2.id " +
            "LEFT JOIN bed bk2 ON ra.new_bed_id = bk2.id " +
            "LEFT JOIN buildings bd2 ON r2.building_id = bd2.id " +
            "ORDER BY ra.created_at DESC")
    List<RelocationApplication> findAll();

    @Select("SELECT ra.*, s.name as student_name, s.student_no, " +
            "r1.room_number as current_room_number, bk1.bed_number as current_bed_number, " +
            "bd1.name as current_building_name, " +
            "r2.room_number as new_room_number, bk2.bed_number as new_bed_number, " +
            "bd2.name as new_building_name " +
            "FROM relocation_application ra " +
            "LEFT JOIN students s ON ra.student_id = s.id " +
            "LEFT JOIN rooms r1 ON ra.current_room_id = r1.id " +
            "LEFT JOIN bed bk1 ON ra.current_bed_id = bk1.id " +
            "LEFT JOIN buildings bd1 ON r1.building_id = bd1.id " +
            "LEFT JOIN rooms r2 ON ra.new_room_id = r2.id " +
            "LEFT JOIN bed bk2 ON ra.new_bed_id = bk2.id " +
            "LEFT JOIN buildings bd2 ON r2.building_id = bd2.id " +
            "WHERE ra.id = #{id}")
    RelocationApplication findById(Long id);

    @Select("SELECT ra.*, s.name as student_name, s.student_no, " +
            "r1.room_number as current_room_number, bk1.bed_number as current_bed_number, " +
            "bd1.name as current_building_name, " +
            "r2.room_number as new_room_number, bk2.bed_number as new_bed_number, " +
            "bd2.name as new_building_name " +
            "FROM relocation_application ra " +
            "LEFT JOIN students s ON ra.student_id = s.id " +
            "LEFT JOIN rooms r1 ON ra.current_room_id = r1.id " +
            "LEFT JOIN bed bk1 ON ra.current_bed_id = bk1.id " +
            "LEFT JOIN buildings bd1 ON r1.building_id = bd1.id " +
            "LEFT JOIN rooms r2 ON ra.new_room_id = r2.id " +
            "LEFT JOIN bed bk2 ON ra.new_bed_id = bk2.id " +
            "LEFT JOIN buildings bd2 ON r2.building_id = bd2.id " +
            "WHERE ra.student_id = #{studentId} " +
            "ORDER BY ra.created_at DESC")
    List<RelocationApplication> findByStudentId(Long studentId);

    @Select("SELECT ra.*, s.name as student_name, s.student_no, " +
            "r1.room_number as current_room_number, bk1.bed_number as current_bed_number, " +
            "bd1.name as current_building_name, " +
            "r2.room_number as new_room_number, bk2.bed_number as new_bed_number, " +
            "bd2.name as new_building_name " +
            "FROM relocation_application ra " +
            "LEFT JOIN students s ON ra.student_id = s.id " +
            "LEFT JOIN rooms r1 ON ra.current_room_id = r1.id " +
            "LEFT JOIN bed bk1 ON ra.current_bed_id = bk1.id " +
            "LEFT JOIN buildings bd1 ON r1.building_id = bd1.id " +
            "LEFT JOIN rooms r2 ON ra.new_room_id = r2.id " +
            "LEFT JOIN bed bk2 ON ra.new_bed_id = bk2.id " +
            "LEFT JOIN buildings bd2 ON r2.building_id = bd2.id " +
            "WHERE ra.status = #{status} " +
            "ORDER BY ra.created_at DESC")
    List<RelocationApplication> findByStatus(String status);

    @Insert("INSERT INTO relocation_application(student_id, batch_id, current_room_id, current_bed_id, " +
            "reason, preferred_building_id, status, created_at, updated_at) " +
            "VALUES(#{studentId}, #{batchId}, #{currentRoomId}, #{currentBedId}, " +
            "#{reason}, #{preferredBuildingId}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(RelocationApplication app);

    @Update("UPDATE relocation_application SET status = #{status}, reviewed_by = #{reviewedBy}, " +
            "review_comment = #{reviewComment}, executed_by = #{executedBy}, " +
            "new_room_id = #{newRoomId}, new_bed_id = #{newBedId}, updated_at = NOW() WHERE id = #{id}")
    int update(RelocationApplication app);

    @Update("UPDATE relocation_application SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    @Delete("DELETE FROM relocation_application WHERE id = #{id}")
    int deleteById(Long id);

    @Delete("DELETE FROM relocation_application WHERE batch_id = #{batchId}")
    int deleteByBatchId(Long batchId);

    @Select("SELECT COUNT(*) FROM relocation_application " +
            "WHERE student_id = #{studentId} AND status IN ('approved', 'executed') " +
            "AND YEAR(created_at) = #{year}")
    int countApprovedOrExecutedInYear(@Param("studentId") Long studentId, @Param("year") int year);

    @Select("SELECT COUNT(*) FROM relocation_application " +
            "WHERE student_id = #{studentId} AND status IN ('pending', 'approved')")
    int countPendingOrApproved(Long studentId);

    @Select("SELECT COUNT(*) FROM relocation_application WHERE batch_id = #{batchId} AND status = #{status}")
    int countByBatchIdAndStatus(@Param("batchId") Long batchId, @Param("status") String status);
}
