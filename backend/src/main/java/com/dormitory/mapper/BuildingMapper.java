package com.dormitory.mapper;

import com.dormitory.model.Building;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BuildingMapper {
    
    @Select("SELECT * FROM buildings ORDER BY id")
    List<Building> findAll();
    
    @Select("SELECT * FROM buildings WHERE id = #{id}")
    Building findById(Long id);
    
    @Select("SELECT * FROM buildings WHERE name = #{name}")
    Building findByName(String name);
    
    @Insert("INSERT INTO buildings(name, floors, rooms_per_floor, gender_type, gender_limit, manager, manager_phone, remark, status) " +
            "VALUES(#{name}, #{floors}, #{roomsPerFloor}, #{genderType}, #{genderLimit}, #{manager}, #{managerPhone}, #{remark}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Building building);
    
    @Update("UPDATE buildings SET name=#{name}, floors=#{floors}, rooms_per_floor=#{roomsPerFloor}, " +
            "gender_type=#{genderType}, gender_limit=#{genderLimit}, manager=#{manager}, manager_phone=#{managerPhone}, " +
            "remark=#{remark}, status=#{status} WHERE id=#{id}")
    int update(Building building);
    
    @Delete("DELETE FROM buildings WHERE id = #{id}")
    int deleteById(Long id);
    
    @Select("SELECT COUNT(*) FROM buildings")
    int count();
    
    @Select("SELECT COUNT(*) FROM buildings WHERE name = #{name} AND id != #{excludeId}")
    int countByNameExclude(@Param("name") String name, @Param("excludeId") Long excludeId);
}