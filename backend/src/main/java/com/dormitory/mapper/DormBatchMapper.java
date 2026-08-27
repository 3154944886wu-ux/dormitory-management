package com.dormitory.mapper;

import com.dormitory.model.DormBatch;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DormBatchMapper {

    @Select("SELECT db.*, c.name as college_name " +
            "FROM dorm_batch db " +
            "LEFT JOIN college c ON db.college_id = c.id " +
            "ORDER BY db.id DESC")
    List<DormBatch> findAll();

    @Select("SELECT db.*, c.name as college_name " +
            "FROM dorm_batch db " +
            "LEFT JOIN college c ON db.college_id = c.id " +
            "WHERE db.id = #{id}")
    DormBatch findById(Long id);

    @Select("SELECT db.*, c.name as college_name " +
            "FROM dorm_batch db " +
            "LEFT JOIN college c ON db.college_id = c.id " +
            "WHERE db.college_id = #{collegeId} " +
            "ORDER BY db.id DESC")
    List<DormBatch> findByCollegeId(Long collegeId);

    @Select("SELECT db.*, c.name as college_name " +
            "FROM dorm_batch db " +
            "LEFT JOIN college c ON db.college_id = c.id " +
            "WHERE db.match_status = #{matchStatus} " +
            "ORDER BY db.id DESC")
    List<DormBatch> findByMatchStatus(String matchStatus);

    @Insert("INSERT INTO dorm_batch(name, college_id, start_time, end_time, confirm_deadline, " +
            "max_reallocation, allow_mix_major, major_bonus, prefer_same_floor, match_status) " +
            "VALUES(#{name}, #{collegeId}, #{startTime}, #{endTime}, #{confirmDeadline}, " +
            "#{maxReallocation}, #{allowMixMajor}, #{majorBonus}, #{preferSameFloor}, #{matchStatus})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(DormBatch batch);

    @Update("UPDATE dorm_batch SET name = #{name}, college_id = #{collegeId}, " +
            "start_time = #{startTime}, end_time = #{endTime}, confirm_deadline = #{confirmDeadline}, " +
            "max_reallocation = #{maxReallocation}, allow_mix_major = #{allowMixMajor}, " +
            "major_bonus = #{majorBonus}, prefer_same_floor = #{preferSameFloor}, " +
            "match_status = #{matchStatus} WHERE id = #{id}")
    int update(DormBatch batch);

    @Update("UPDATE dorm_batch SET match_status = #{matchStatus} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("matchStatus") String matchStatus);

    @Update("UPDATE dorm_batch SET match_status = #{toStatus} WHERE id = #{id} AND match_status = #{fromStatus}")
    int updateStatusIf(@Param("id") Long id,
                       @Param("fromStatus") String fromStatus,
                       @Param("toStatus") String toStatus);

    @Select("SELECT db.*, c.name as college_name " +
            "FROM dorm_batch db " +
            "LEFT JOIN college c ON db.college_id = c.id " +
            "WHERE db.match_status = 'running' AND db.end_time <= NOW()")
    List<DormBatch> findRunningAndPastEndTime();

    @Select("SELECT db.*, c.name as college_name " +
            "FROM dorm_batch db " +
            "LEFT JOIN college c ON db.college_id = c.id " +
            "WHERE db.match_status = 'confirming' AND db.confirm_deadline <= NOW()")
    List<DormBatch> findConfirmingAndPastDeadline();

    @Delete("DELETE FROM dorm_batch WHERE id = #{id}")
    int deleteById(Long id);

    @Select("SELECT COUNT(*) FROM dorm_batch WHERE match_status IN ('running', 'matching', 'confirming')")
    int countActive();
}
