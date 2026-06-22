package com.dormitory.mapper;

import com.dormitory.model.Teacher;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TeacherMapper {

    @Select("SELECT m.*, u.username, u.nickname " +
            "FROM managers m LEFT JOIN users u ON m.user_id = u.id ORDER BY m.employee_no")
    List<Teacher> findAll();

    @Select("SELECT m.*, u.username, u.nickname " +
            "FROM managers m LEFT JOIN users u ON m.user_id = u.id WHERE m.id = #{id}")
    Teacher findById(Long id);

    @Select("SELECT m.*, u.username, u.nickname " +
            "FROM managers m LEFT JOIN users u ON m.user_id = u.id WHERE m.user_id = #{userId}")
    Teacher findByUserId(Long userId);

    @Select("SELECT m.*, u.username, u.nickname " +
            "FROM managers m LEFT JOIN users u ON m.user_id = u.id WHERE m.employee_no = #{employeeNo}")
    Teacher findByEmployeeNo(String employeeNo);

    @Select("SELECT COUNT(*) FROM managers")
    int countAll();

    @Insert("INSERT INTO managers(employee_no, name, phone, email, user_id, status) " +
            "VALUES(#{employeeNo}, #{name}, #{phone}, #{email}, #{userId}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Teacher teacher);

    @Update("UPDATE managers SET name=#{name}, phone=#{phone}, email=#{email}, status=#{status} WHERE id=#{id}")
    int update(Teacher teacher);

    @Update("UPDATE managers SET user_id=#{userId} WHERE id=#{id}")
    int updateUserId(@Param("id") Long id, @Param("userId") Long userId);

    @Delete("DELETE FROM managers WHERE user_id = #{userId}")
    int deleteByUserId(Long userId);
}
