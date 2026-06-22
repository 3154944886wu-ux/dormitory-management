package com.dormitory.mapper;

import com.dormitory.model.RoommateGroup;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RoommateGroupMapper {

    @Select("SELECT * FROM roommate_group ORDER BY id")
    List<RoommateGroup> findAll();

    @Select("SELECT * FROM roommate_group WHERE id = #{id}")
    RoommateGroup findById(Long id);

    @Select("SELECT * FROM roommate_group WHERE batch_id = #{batchId} ORDER BY id")
    List<RoommateGroup> findByBatchId(Long batchId);

    @Select("SELECT * FROM roommate_group WHERE room_id = #{roomId}")
    List<RoommateGroup> findByRoomId(Long roomId);

    @Insert("INSERT INTO roommate_group(batch_id, room_id, member_ids) " +
            "VALUES(#{batchId}, #{roomId}, #{memberIds})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(RoommateGroup group);

    @Update("UPDATE roommate_group SET batch_id = #{batchId}, room_id = #{roomId}, " +
            "member_ids = #{memberIds} WHERE id = #{id}")
    int update(RoommateGroup group);

    @Update("UPDATE roommate_group SET room_id = #{roomId} WHERE id = #{id}")
    int updateRoom(@Param("id") Long id, @Param("roomId") Long roomId);

    @Delete("DELETE FROM roommate_group WHERE id = #{id}")
    int deleteById(Long id);

    @Delete("DELETE FROM roommate_group WHERE batch_id = #{batchId}")
    int deleteByBatchId(Long batchId);
}
