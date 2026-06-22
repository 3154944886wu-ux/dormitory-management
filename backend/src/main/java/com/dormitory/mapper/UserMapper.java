package com.dormitory.mapper;

import com.dormitory.model.User;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface UserMapper {
    
    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(String username);
    
    @Select("SELECT id, username, nickname, phone, email, role, status, create_time, update_time FROM users WHERE id = #{id}")
    User findById(Long id);

    @Select("SELECT * FROM users ORDER BY id")
    List<User> findAll();

    @Select("SELECT id, username, nickname, phone, email, role, status, create_time, update_time FROM users WHERE role = #{role} ORDER BY id")
    List<User> findByRole(@Param("role") String role);

    @Insert("INSERT INTO users(username, password, nickname, phone, email, role, status) VALUES(#{username}, #{password}, #{nickname}, #{phone}, #{email}, #{role}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("UPDATE users SET nickname=#{nickname}, phone=#{phone}, email=#{email}, role=#{role}, status=#{status} WHERE id=#{id}")
    int update(User user);

    @Update("UPDATE users SET password=#{password} WHERE id=#{userId}")
    int updatePassword(@Param("userId") Long userId, @Param("password") String password);

    @Select("SELECT COUNT(*) FROM users WHERE username = #{username}")
    int countByUsername(String username);

    @Delete("DELETE FROM users WHERE id = #{id}")
    int deleteById(Long id);
}