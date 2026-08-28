package com.dormitory.mapper;

import com.dormitory.model.Room;
import com.dormitory.utils.OccupancySql;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RoomMapper {
    
    @Select("SELECT r.*, " + OccupancySql.LIVE_IN_ROOM + " AS occupancy, b.name as building_name FROM rooms r " +
             "LEFT JOIN buildings b ON r.building_id = b.id " +
             "ORDER BY b.id, r.floor, r.room_number")
    List<Room> findAll();
    
    @Select("SELECT r.*, " + OccupancySql.LIVE_IN_ROOM + " AS occupancy, b.name as building_name FROM rooms r " +
             "LEFT JOIN buildings b ON r.building_id = b.id " +
             "WHERE r.building_id = #{buildingId} " +
             "ORDER BY r.floor, r.room_number")
    List<Room> findByBuildingId(Long buildingId);
    
    @Select("SELECT r.*, " + OccupancySql.LIVE_IN_ROOM + " AS occupancy, b.name as building_name FROM rooms r " +
             "LEFT JOIN buildings b ON r.building_id = b.id " +
             "WHERE r.id = #{id}")
    Room findById(Long id);
    
    @Select("SELECT r.*, " + OccupancySql.LIVE_IN_ROOM + " AS occupancy, b.name as building_name FROM rooms r " +
             "LEFT JOIN buildings b ON r.building_id = b.id " +
             "WHERE r.building_id = #{buildingId} AND r.room_number = #{roomNumber}")
    Room findByBuildingAndNumber(@Param("buildingId") Long buildingId, 
                                  @Param("roomNumber") String roomNumber);
    
    @Insert("INSERT INTO rooms(building_id, room_number, floor, capacity, current_count, status, " +
             "room_type, window_beds_count, corridor_beds_count, special_tag, is_active) " +
             "VALUES(#{buildingId}, #{roomNumber}, #{floor}, #{capacity}, #{currentCount}, #{status}, " +
             "#{roomType}, #{windowBedsCount}, #{corridorBedsCount}, #{specialTag}, #{isActive})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Room room);
    
    @Update("UPDATE rooms SET building_id=#{buildingId}, room_number=#{roomNumber}, " +
             "floor=#{floor}, capacity=#{capacity}, current_count=#{currentCount}, " +
             "status=#{status}, room_type=#{roomType}, window_beds_count=#{windowBedsCount}, " +
             "corridor_beds_count=#{corridorBedsCount}, special_tag=#{specialTag}, is_active=#{isActive} WHERE id=#{id}")
    int update(Room room);
    
    @Delete("DELETE FROM rooms WHERE id = #{id}")
    int deleteById(Long id);
    
    @Update("UPDATE rooms SET current_count = current_count + 1 WHERE id = #{id} AND current_count < capacity")
    int incrementCount(Long id);
    
    @Update("UPDATE rooms SET current_count = current_count - 1 WHERE id = #{id} AND current_count > 0")
    int decrementCount(Long id);

    @Update("UPDATE rooms SET current_count = #{count} WHERE id = #{id}")
    int setCurrentCount(@Param("id") Long id, @Param("count") int count);
    
    @Select("SELECT COUNT(*) FROM rooms WHERE building_id = #{buildingId}")
    int countByBuildingId(Long buildingId);
    
    @Select("SELECT COUNT(*) FROM rooms")
    int count();
    
    @Select("SELECT COUNT(*) FROM rooms r WHERE " + OccupancySql.LIVE_IN_ROOM + " > 0")
    int countOccupied();

    @Select("SELECT COUNT(*) FROM rooms r WHERE " + OccupancySql.LIVE_IN_ROOM + " = 0")
    int countFree();

    @Select("SELECT COUNT(*) FROM rooms r WHERE " + OccupancySql.LIVE_IN_ROOM + " > 0 AND " + OccupancySql.LIVE_IN_ROOM + " < r.capacity")
    int countPartial();

    @Select("SELECT COUNT(*) FROM rooms r WHERE " + OccupancySql.LIVE_IN_ROOM + " >= r.capacity AND " + OccupancySql.LIVE_IN_ROOM + " > 0")
    int countFull();

    @Select("SELECT r.*, " + OccupancySql.LIVE_IN_ROOM + " AS occupancy, b.name as building_name FROM rooms r " +
            "LEFT JOIN buildings b ON r.building_id = b.id " +
            "ORDER BY b.id, r.floor, r.room_number " +
            "LIMIT #{size} OFFSET #{offset}")
    List<Room> findAllWithPagination(@Param("offset") int offset, @Param("size") int size);

    @Select("SELECT r.*, " + OccupancySql.LIVE_IN_ROOM + " AS occupancy, b.name as building_name FROM rooms r " +
            "LEFT JOIN buildings b ON r.building_id = b.id " +
            "WHERE r.building_id = #{buildingId} " +
            "ORDER BY r.floor, r.room_number " +
            "LIMIT #{size} OFFSET #{offset}")
    List<Room> findByBuildingIdWithPagination(@Param("buildingId") Long buildingId,
                                              @Param("offset") int offset,
                                              @Param("size") int size);
}