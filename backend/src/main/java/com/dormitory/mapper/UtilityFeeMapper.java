package com.dormitory.mapper;

import com.dormitory.model.UtilityFee;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UtilityFeeMapper {
    
    @Select("SELECT u.*, r.room_number, b.name as building_name " +
             "FROM utility_fees u " +
             "LEFT JOIN rooms r ON u.room_id = r.id " +
             "LEFT JOIN buildings b ON r.building_id = b.id " +
             "ORDER BY u.year DESC, u.month DESC, b.id, r.room_number")
    List<UtilityFee> findAll();
    
    @Select("SELECT u.*, r.room_number, b.name as building_name " +
             "FROM utility_fees u " +
             "LEFT JOIN rooms r ON u.room_id = r.id " +
             "LEFT JOIN buildings b ON r.building_id = b.id " +
             "WHERE u.room_id = #{roomId} " +
             "ORDER BY u.year DESC, u.month DESC")
    List<UtilityFee> findByRoomId(Long roomId);
    
    @Select("SELECT u.*, r.room_number, b.name as building_name " +
             "FROM utility_fees u " +
             "LEFT JOIN rooms r ON u.room_id = r.id " +
             "LEFT JOIN buildings b ON r.building_id = b.id " +
             "WHERE u.id = #{id}")
    UtilityFee findById(Long id);
    
    @Select("SELECT u.*, r.room_number, b.name as building_name " +
             "FROM utility_fees u " +
             "LEFT JOIN rooms r ON u.room_id = r.id " +
             "LEFT JOIN buildings b ON r.building_id = b.id " +
             "WHERE u.room_id = #{roomId} AND u.year = #{year} AND u.month = #{month}")
    UtilityFee findByRoomAndMonth(@Param("roomId") Long roomId, 
                                   @Param("year") Integer year, 
                                   @Param("month") Integer month);
    
    @Select("SELECT u.*, r.room_number, b.name as building_name " +
             "FROM utility_fees u " +
             "LEFT JOIN rooms r ON u.room_id = r.id " +
             "LEFT JOIN buildings b ON r.building_id = b.id " +
             "WHERE u.status = #{status} " +
             "ORDER BY u.year DESC, u.month DESC")
    List<UtilityFee> findByStatus(Integer status);
    
    @Insert("INSERT INTO utility_fees(room_id, year, month, electricity_start, electricity_end, " +
             "electricity_usage, electricity_fee, water_start, water_end, water_usage, water_fee, " +
             "total_fee, status) " +
             "VALUES(#{roomId}, #{year}, #{month}, #{electricityStart}, #{electricityEnd}, " +
             "#{electricityUsage}, #{electricityFee}, #{waterStart}, #{waterEnd}, " +
             "#{waterUsage}, #{waterFee}, #{totalFee}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UtilityFee fee);
    
    @Update("UPDATE utility_fees SET room_id=#{roomId}, year=#{year}, month=#{month}, " +
             "electricity_start=#{electricityStart}, electricity_end=#{electricityEnd}, " +
             "electricity_usage=#{electricityUsage}, electricity_fee=#{electricityFee}, " +
             "water_start=#{waterStart}, water_end=#{waterEnd}, water_usage=#{waterUsage}, " +
             "water_fee=#{waterFee}, total_fee=#{totalFee}, status=#{status}, " +
             "pay_time=#{payTime} WHERE id=#{id}")
    int update(UtilityFee fee);
    
    @Delete("DELETE FROM utility_fees WHERE id = #{id}")
    int deleteById(Long id);
}