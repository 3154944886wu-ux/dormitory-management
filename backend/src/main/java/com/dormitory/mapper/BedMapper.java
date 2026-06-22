package com.dormitory.mapper;

import com.dormitory.model.Bed;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BedMapper {

    @Select("SELECT b.*, r.room_number, bd.name as building_name " +
            "FROM bed b " +
            "LEFT JOIN rooms r ON b.room_id = r.id " +
            "LEFT JOIN buildings bd ON r.building_id = bd.id " +
            "ORDER BY r.room_number, b.bed_number")
    List<Bed> findAll();

    @Select("SELECT b.*, r.room_number, bd.name as building_name " +
            "FROM bed b " +
            "LEFT JOIN rooms r ON b.room_id = r.id " +
            "LEFT JOIN buildings bd ON r.building_id = bd.id " +
            "WHERE b.id = #{id}")
    Bed findById(Long id);

    @Select("SELECT b.*, r.room_number, bd.name as building_name " +
            "FROM bed b " +
            "LEFT JOIN rooms r ON b.room_id = r.id " +
            "LEFT JOIN buildings bd ON r.building_id = bd.id " +
            "WHERE b.room_id = #{roomId} " +
            "ORDER BY b.bed_number")
    List<Bed> findByRoomId(Long roomId);

    @Select("SELECT b.*, r.room_number, bd.name as building_name " +
            "FROM bed b " +
            "LEFT JOIN rooms r ON b.room_id = r.id " +
            "LEFT JOIN buildings bd ON r.building_id = bd.id " +
            "WHERE b.room_id = #{roomId} AND b.is_occupied = 0 " +
            "ORDER BY b.bed_number")
    List<Bed> findAvailableByRoomId(Long roomId);

    @Insert("INSERT INTO bed(bed_number, room_id, bed_type, is_occupied) " +
            "VALUES(#{bedNumber}, #{roomId}, #{bedType}, #{isOccupied})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Bed bed);

    @Update("UPDATE bed SET bed_number = #{bedNumber}, room_id = #{roomId}, " +
            "bed_type = #{bedType}, is_occupied = #{isOccupied} WHERE id = #{id}")
    int update(Bed bed);

    @Update("UPDATE bed SET is_occupied = #{isOccupied} WHERE id = #{id}")
    int updateOccupied(@Param("id") Long id, @Param("isOccupied") Integer isOccupied);

    @Update("UPDATE bed SET is_occupied = 1 WHERE id = #{id} AND is_occupied = 0")
    int tryOccupy(@Param("id") Long id);

    @Delete("DELETE FROM bed WHERE id = #{id}")
    int deleteById(Long id);

    @Select("SELECT COUNT(*) FROM bed WHERE room_id = #{roomId}")
    int countByRoomId(Long roomId);

    @Select("SELECT COUNT(*) FROM bed WHERE room_id = #{roomId} AND is_occupied = 0")
    int countAvailableByRoomId(Long roomId);
}
