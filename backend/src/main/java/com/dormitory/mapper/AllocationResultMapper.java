package com.dormitory.mapper;

import com.dormitory.model.AllocationResult;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AllocationResultMapper {

    @Select("SELECT ar.*, s.name as student_name, s.student_no, " +
            "r.room_number, bk.bed_number, db.name as batch_name " +
            "FROM allocation_result ar " +
            "LEFT JOIN students s ON ar.student_id = s.id " +
            "LEFT JOIN rooms r ON ar.room_id = r.id " +
            "LEFT JOIN bed bk ON ar.bed_id = bk.id " +
            "LEFT JOIN dorm_batch db ON ar.batch_id = db.id " +
            "ORDER BY ar.id DESC")
    List<AllocationResult> findAll();

    @Select("SELECT ar.*, s.name as student_name, s.student_no, " +
            "r.room_number, bk.bed_number, db.name as batch_name " +
            "FROM allocation_result ar " +
            "LEFT JOIN students s ON ar.student_id = s.id " +
            "LEFT JOIN rooms r ON ar.room_id = r.id " +
            "LEFT JOIN bed bk ON ar.bed_id = bk.id " +
            "LEFT JOIN dorm_batch db ON ar.batch_id = db.id " +
            "WHERE ar.id = #{id}")
    AllocationResult findById(Long id);

    @Select("SELECT ar.*, s.name as student_name, s.student_no, " +
            "r.room_number, bk.bed_number, db.name as batch_name " +
            "FROM allocation_result ar " +
            "LEFT JOIN students s ON ar.student_id = s.id " +
            "LEFT JOIN rooms r ON ar.room_id = r.id " +
            "LEFT JOIN bed bk ON ar.bed_id = bk.id " +
            "LEFT JOIN dorm_batch db ON ar.batch_id = db.id " +
            "WHERE ar.student_id = #{studentId} " +
            "ORDER BY ar.id DESC")
    List<AllocationResult> findByStudentId(Long studentId);

    @Select("SELECT ar.*, s.name as student_name, s.student_no, " +
            "r.room_number, bk.bed_number, db.name as batch_name " +
            "FROM allocation_result ar " +
            "LEFT JOIN students s ON ar.student_id = s.id " +
            "LEFT JOIN rooms r ON ar.room_id = r.id " +
            "LEFT JOIN bed bk ON ar.bed_id = bk.id " +
            "LEFT JOIN dorm_batch db ON ar.batch_id = db.id " +
            "WHERE ar.batch_id = #{batchId} " +
            "ORDER BY ar.match_score DESC")
    List<AllocationResult> findByBatchId(Long batchId);

    @Select("SELECT ar.*, s.name as student_name, s.student_no, " +
            "r.room_number, bk.bed_number, db.name as batch_name " +
            "FROM allocation_result ar " +
            "LEFT JOIN students s ON ar.student_id = s.id " +
            "LEFT JOIN rooms r ON ar.room_id = r.id " +
            "LEFT JOIN bed bk ON ar.bed_id = bk.id " +
            "LEFT JOIN dorm_batch db ON ar.batch_id = db.id " +
            "WHERE ar.batch_id = #{batchId} AND ar.status = #{status} " +
            "ORDER BY ar.match_score DESC")
    List<AllocationResult> findByBatchIdAndStatus(@Param("batchId") Long batchId, @Param("status") String status);

    @Select("SELECT ar.*, s.name as student_name, s.student_no, " +
            "r.room_number, bk.bed_number, db.name as batch_name " +
            "FROM allocation_result ar " +
            "LEFT JOIN students s ON ar.student_id = s.id " +
            "LEFT JOIN rooms r ON ar.room_id = r.id " +
            "LEFT JOIN bed bk ON ar.bed_id = bk.id " +
            "LEFT JOIN dorm_batch db ON ar.batch_id = db.id " +
            "WHERE ar.room_id = #{roomId} " +
            "ORDER BY ar.id DESC")
    List<AllocationResult> findByRoomId(Long roomId);

    @Insert("INSERT INTO allocation_result(student_id, batch_id, roommate_group_id, room_id, bed_id, " +
            "match_score, reallocation_count, status, created_at, updated_at) " +
            "VALUES(#{studentId}, #{batchId}, #{roommateGroupId}, #{roomId}, #{bedId}, " +
            "#{matchScore}, #{reallocationCount}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(AllocationResult result);

    @Update("UPDATE allocation_result SET student_id = #{studentId}, batch_id = #{batchId}, " +
            "roommate_group_id = #{roommateGroupId}, room_id = #{roomId}, bed_id = #{bedId}, " +
            "match_score = #{matchScore}, reallocation_count = #{reallocationCount}, " +
            "status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int update(AllocationResult result);

    @Update("UPDATE allocation_result SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    @Delete("DELETE FROM allocation_result WHERE id = #{id}")
    int deleteById(Long id);

    @Delete("DELETE FROM allocation_result WHERE batch_id = #{batchId}")
    int deleteByBatchId(Long batchId);

    @Select("SELECT ar.*, s.name as student_name, s.student_no, " +
            "r.room_number, bk.bed_number, db.name as batch_name " +
            "FROM allocation_result ar " +
            "LEFT JOIN students s ON ar.student_id = s.id " +
            "LEFT JOIN rooms r ON ar.room_id = r.id " +
            "LEFT JOIN bed bk ON ar.bed_id = bk.id " +
            "LEFT JOIN dorm_batch db ON ar.batch_id = db.id " +
            "WHERE ar.student_id = #{studentId} AND ar.batch_id = #{batchId}")
    AllocationResult findByStudentIdAndBatchId(@Param("studentId") Long studentId, @Param("batchId") Long batchId);

    @Select("SELECT ar.*, s.name as student_name, s.student_no, " +
            "r.room_number, bk.bed_number, db.name as batch_name " +
            "FROM allocation_result ar " +
            "LEFT JOIN students s ON ar.student_id = s.id " +
            "LEFT JOIN rooms r ON ar.room_id = r.id " +
            "LEFT JOIN bed bk ON ar.bed_id = bk.id " +
            "LEFT JOIN dorm_batch db ON ar.batch_id = db.id " +
            "WHERE ar.room_id = #{roomId} AND ar.batch_id = #{batchId} " +
            "ORDER BY ar.id")
    List<AllocationResult> findByRoomIdAndBatchId(@Param("roomId") Long roomId, @Param("batchId") Long batchId);

    @Select("SELECT COUNT(*) FROM allocation_result WHERE batch_id = #{batchId}")
    int countByBatchId(Long batchId);

    @Select("SELECT AVG(match_score) FROM allocation_result WHERE batch_id = #{batchId}")
    java.math.BigDecimal avgMatchScoreByBatchId(Long batchId);

    @Select("SELECT " +
            "COUNT(CASE WHEN match_score >= 0 AND match_score < 20 THEN 1 END) as range_0_20, " +
            "COUNT(CASE WHEN match_score >= 20 AND match_score < 40 THEN 1 END) as range_20_40, " +
            "COUNT(CASE WHEN match_score >= 40 AND match_score < 60 THEN 1 END) as range_40_60, " +
            "COUNT(CASE WHEN match_score >= 60 AND match_score < 80 THEN 1 END) as range_60_80, " +
            "COUNT(CASE WHEN match_score >= 80 THEN 1 END) as range_80_100 " +
            "FROM allocation_result WHERE batch_id = #{batchId}")
    java.util.Map<String, Object> matchScoreDistribution(Long batchId);

    @Select("SELECT " +
            "COUNT(CASE WHEN reallocation_count > 0 THEN 1 END) as reallocated, " +
            "COUNT(*) as total " +
            "FROM allocation_result WHERE batch_id = #{batchId}")
    java.util.Map<String, Object> reallocationStats(Long batchId);

    @Select("SELECT ar.status, COUNT(*) as cnt " +
            "FROM allocation_result ar WHERE ar.batch_id = #{batchId} GROUP BY ar.status")
    List<java.util.Map<String, Object>> countGroupByStatus(Long batchId);

    @Select("SELECT b.id as building_id, b.name as building_name, COUNT(*) as cnt " +
            "FROM allocation_result ar " +
            "JOIN rooms r ON ar.room_id = r.id " +
            "JOIN buildings b ON r.building_id = b.id " +
            "WHERE ar.batch_id = #{batchId} AND ar.status IN ('confirmed','auto_confirmed','manual_assigned','adjusted') " +
            "GROUP BY b.id, b.name ORDER BY cnt DESC")
    List<java.util.Map<String, Object>> countGroupByBuilding(Long batchId);

    @Select("SELECT COALESCE(m.id,0) as major_id, COALESCE(m.name,'未知') as major_name, COUNT(*) as cnt " +
            "FROM allocation_result ar " +
            "JOIN students s ON ar.student_id = s.id " +
            "LEFT JOIN major m ON s.major_id = m.id " +
            "WHERE ar.batch_id = #{batchId} " +
            "GROUP BY m.id, m.name ORDER BY cnt DESC")
    List<java.util.Map<String, Object>> countGroupByMajor(Long batchId);

    @Select("SELECT COUNT(*) FROM allocation_result")
    int countAll();

    @Select("SELECT AVG(match_score) FROM allocation_result")
    java.math.BigDecimal avgTotalMatchScore();

    @Select("SELECT COUNT(*) FROM allocation_result WHERE status = #{status}")
    int countByStatus(@Param("status") String status);
}
