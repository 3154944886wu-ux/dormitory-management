package com.dormitory.mapper;

import com.dormitory.model.BatchRoom;
import com.dormitory.model.Room;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BatchRoomMapper {

    @Select("SELECT br.*, r.room_number, b.name as building_name " +
            "FROM batch_room br " +
            "LEFT JOIN rooms r ON br.room_id = r.id " +
            "LEFT JOIN buildings b ON r.building_id = b.id " +
            "ORDER BY br.batch_id, r.room_number")
    List<BatchRoom> findAll();

    @Select("SELECT br.*, r.room_number, b.name as building_name " +
            "FROM batch_room br " +
            "LEFT JOIN rooms r ON br.room_id = r.id " +
            "LEFT JOIN buildings b ON r.building_id = b.id " +
            "WHERE br.id = #{id}")
    BatchRoom findById(Long id);

    @Select("SELECT br.*, r.room_number, b.name as building_name " +
            "FROM batch_room br " +
            "LEFT JOIN rooms r ON br.room_id = r.id " +
            "LEFT JOIN buildings b ON r.building_id = b.id " +
            "WHERE br.batch_id = #{batchId} " +
            "ORDER BY r.room_number")
    List<BatchRoom> findByBatchId(Long batchId);

    @Insert("INSERT INTO batch_room(batch_id, room_id) VALUES(#{batchId}, #{roomId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(BatchRoom batchRoom);

    @Delete("DELETE FROM batch_room WHERE id = #{id}")
    int deleteById(Long id);

    @Delete("DELETE FROM batch_room WHERE batch_id = #{batchId} AND room_id = #{roomId}")
    int deleteByBatchAndRoom(@Param("batchId") Long batchId, @Param("roomId") Long roomId);

    @Delete("DELETE FROM batch_room WHERE batch_id = #{batchId}")
    int deleteByBatchId(Long batchId);

    @Select("SELECT COUNT(*) FROM batch_room WHERE batch_id = #{batchId}")
    int countByBatchId(Long batchId);

    @Select("SELECT COUNT(*) FROM batch_room br " +
            "INNER JOIN dorm_batch db ON br.batch_id = db.id " +
            "WHERE br.room_id = #{roomId} " +
            "AND db.match_status NOT IN ('finished', 'cancelled', 'pending')")
    int countByRoomIdAndActiveBatches(Long roomId);

    @Select("SELECT r.*, b.name as building_name " +
            "FROM batch_room br " +
            "JOIN rooms r ON br.room_id = r.id " +
            "JOIN buildings b ON r.building_id = b.id " +
            "WHERE br.batch_id = #{batchId} " +
            "AND (#{buildingId} IS NULL OR r.building_id = #{buildingId}) " +
            "AND (#{roomNumber} IS NULL OR r.room_number LIKE CONCAT('%', #{roomNumber}, '%')) " +
            "ORDER BY b.id, r.floor, r.room_number")
    List<Room> findRoomsByBatchIdWithFilter(@Param("batchId") Long batchId,
                                             @Param("buildingId") Long buildingId,
                                             @Param("roomNumber") String roomNumber);
}
