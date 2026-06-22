package com.dormitory.mapper;

import com.dormitory.model.College;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CollegeMapper {

    @Select("SELECT * FROM college ORDER BY id")
    List<College> findAll();

    @Select("SELECT * FROM college WHERE id = #{id}")
    College findById(Long id);

    @Select("SELECT * FROM college WHERE name = #{name}")
    College findByName(String name);

    @Insert("INSERT INTO college(name) VALUES(#{name})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(College college);

    @Update("UPDATE college SET name = #{name} WHERE id = #{id}")
    int update(College college);

    @Delete("DELETE FROM college WHERE id = #{id}")
    int deleteById(Long id);
}
