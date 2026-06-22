package com.dormitory.mapper;

import com.dormitory.model.Major;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MajorMapper {

    @Select("SELECT m.*, c.name as college_name " +
            "FROM major m " +
            "LEFT JOIN college c ON m.college_id = c.id " +
            "ORDER BY m.id")
    List<Major> findAll();

    @Select("SELECT m.*, c.name as college_name " +
            "FROM major m " +
            "LEFT JOIN college c ON m.college_id = c.id " +
            "WHERE m.id = #{id}")
    Major findById(Long id);

    @Select("SELECT m.*, c.name as college_name " +
            "FROM major m " +
            "LEFT JOIN college c ON m.college_id = c.id " +
            "WHERE m.college_id = #{collegeId} " +
            "ORDER BY m.id")
    List<Major> findByCollegeId(Long collegeId);

    @Insert("INSERT INTO major(name, college_id) VALUES(#{name}, #{collegeId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Major major);

    @Update("UPDATE major SET name = #{name}, college_id = #{collegeId} WHERE id = #{id}")
    int update(Major major);

    @Delete("DELETE FROM major WHERE id = #{id}")
    int deleteById(Long id);
}
