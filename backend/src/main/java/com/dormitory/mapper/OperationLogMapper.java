package com.dormitory.mapper;

import com.dormitory.model.OperationLog;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OperationLogMapper {

    @Select("SELECT ol.*, s.name as student_name " +
            "FROM operation_log ol " +
            "LEFT JOIN students s ON ol.student_id = s.id " +
            "ORDER BY ol.create_time DESC")
    List<OperationLog> findAll();

    @Select("SELECT ol.*, s.name as student_name " +
            "FROM operation_log ol " +
            "LEFT JOIN students s ON ol.student_id = s.id " +
            "WHERE ol.id = #{id}")
    OperationLog findById(Long id);

    @Select("SELECT ol.*, s.name as student_name " +
            "FROM operation_log ol " +
            "LEFT JOIN students s ON ol.student_id = s.id " +
            "WHERE ol.student_id = #{studentId} " +
            "ORDER BY ol.create_time DESC")
    List<OperationLog> findByStudentId(Long studentId);

    @Select("SELECT ol.*, s.name as student_name " +
            "FROM operation_log ol " +
            "LEFT JOIN students s ON ol.student_id = s.id " +
            "WHERE ol.action = #{action} " +
            "ORDER BY ol.create_time DESC")
    List<OperationLog> findByAction(String action);

    @Insert("INSERT INTO operation_log(student_id, operator_type, operator_id, action, detail) " +
            "VALUES(#{studentId}, #{operatorType}, #{operatorId}, #{action}, #{detail})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(OperationLog log);

    @Select("SELECT ol.*, s.name as student_name " +
            "FROM operation_log ol " +
            "LEFT JOIN students s ON ol.student_id = s.id " +
            "WHERE (#{operatorType} IS NULL OR ol.operator_type = #{operatorType}) " +
            "AND (#{action} IS NULL OR ol.action = #{action}) " +
            "AND (#{keyword} IS NULL OR ol.operator_id LIKE CONCAT('%', #{keyword}, '%') " +
            "     OR ol.detail LIKE CONCAT('%', #{keyword}, '%') OR s.name LIKE CONCAT('%', #{keyword}, '%')) " +
            "ORDER BY ol.create_time DESC LIMIT #{offset}, #{limit}")
    List<OperationLog> search(@Param("operatorType") String operatorType,
                              @Param("action") String action,
                              @Param("keyword") String keyword,
                              @Param("offset") int offset,
                              @Param("limit") int limit);

    @Select("SELECT COUNT(*) " +
            "FROM operation_log ol " +
            "LEFT JOIN students s ON ol.student_id = s.id " +
            "WHERE (#{operatorType} IS NULL OR ol.operator_type = #{operatorType}) " +
            "AND (#{action} IS NULL OR ol.action = #{action}) " +
            "AND (#{keyword} IS NULL OR ol.operator_id LIKE CONCAT('%', #{keyword}, '%') " +
            "     OR ol.detail LIKE CONCAT('%', #{keyword}, '%') OR s.name LIKE CONCAT('%', #{keyword}, '%'))")
    int countSearch(@Param("operatorType") String operatorType,
                    @Param("action") String action,
                    @Param("keyword") String keyword);
}
