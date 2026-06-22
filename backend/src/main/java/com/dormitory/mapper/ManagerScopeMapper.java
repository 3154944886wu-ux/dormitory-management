package com.dormitory.mapper;

import com.dormitory.model.ManagerScope;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ManagerScopeMapper {

    @Select("SELECT ms.*, u.username, u.nickname, b.name AS building_name " +
            "FROM manager_scope ms " +
            "LEFT JOIN users u ON ms.user_id = u.id " +
            "LEFT JOIN buildings b ON ms.building_id = b.id " +
            "WHERE ms.id = #{id}")
    ManagerScope findById(Long id);

    @Select("SELECT ms.*, u.username, u.nickname, b.name AS building_name " +
            "FROM manager_scope ms " +
            "LEFT JOIN users u ON ms.user_id = u.id " +
            "LEFT JOIN buildings b ON ms.building_id = b.id " +
            "WHERE ms.status = 1 ORDER BY u.username, b.name, ms.class_name")
    List<ManagerScope> findAllActive();

    @Select("SELECT ms.*, u.username, u.nickname, b.name AS building_name " +
            "FROM manager_scope ms " +
            "LEFT JOIN users u ON ms.user_id = u.id " +
            "LEFT JOIN buildings b ON ms.building_id = b.id " +
            "WHERE ms.user_id = #{userId} AND ms.status = 1")
    List<ManagerScope> findActiveByUserId(Long userId);

    @Insert("INSERT INTO manager_scope(user_id, building_id, class_name, status) " +
            "VALUES(#{userId}, #{buildingId}, #{className}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ManagerScope scope);

    @Update("UPDATE manager_scope SET building_id = #{buildingId}, class_name = #{className}, " +
            "status = #{status} WHERE id = #{id}")
    int update(ManagerScope scope);

    @Update("UPDATE manager_scope SET status = 0 WHERE id = #{id}")
    int disable(Long id);

    @Update("UPDATE manager_scope SET status = 0 WHERE user_id = #{userId}")
    int disableByUserId(Long userId);
}
