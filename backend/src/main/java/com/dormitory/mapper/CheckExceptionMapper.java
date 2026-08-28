package com.dormitory.mapper;

import com.dormitory.model.CheckException;
import org.apache.ibatis.annotations.*;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface CheckExceptionMapper {
    
    @Select("SELECT e.*, s.name as student_name, s.student_no, s.department, s.class_name, " +
            "r.room_number, r.building_id as buildingId, b.name as building_name " +
            "FROM check_exceptions e " +
            "LEFT JOIN students s ON e.student_id = s.id " +
            "LEFT JOIN rooms r ON s.room_id = r.id " +
            "LEFT JOIN buildings b ON r.building_id = b.id " +
            "WHERE e.id = #{id}")
    CheckException findById(@Param("id") Long id);
    
    @Select("SELECT e.*, s.name as student_name, s.student_no, s.department, s.class_name, " +
            "r.room_number, r.building_id as buildingId, b.name as building_name " +
            "FROM check_exceptions e " +
            "LEFT JOIN students s ON e.student_id = s.id " +
            "LEFT JOIN rooms r ON s.room_id = r.id " +
            "LEFT JOIN buildings b ON r.building_id = b.id " +
            "ORDER BY e.exception_date DESC, e.create_time DESC " +
            "LIMIT #{offset}, #{limit}")
    List<CheckException> findAll(@Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT COUNT(*) FROM check_exceptions")
    int count();
    
    @Select("SELECT e.*, s.name as student_name, s.student_no, s.department, s.class_name, " +
            "r.room_number, r.building_id as buildingId, b.name as building_name " +
            "FROM check_exceptions e " +
            "LEFT JOIN students s ON e.student_id = s.id " +
            "LEFT JOIN rooms r ON s.room_id = r.id " +
            "LEFT JOIN buildings b ON r.building_id = b.id " +
            "WHERE e.exception_date = #{date} ORDER BY e.create_time DESC")
    List<CheckException> findByDate(@Param("date") LocalDate date);
    
    @Select("SELECT e.*, s.name as student_name, s.student_no, s.department, s.class_name, " +
            "r.room_number, r.building_id as buildingId, b.name as building_name " +
            "FROM check_exceptions e " +
            "LEFT JOIN students s ON e.student_id = s.id " +
            "LEFT JOIN rooms r ON s.room_id = r.id " +
            "LEFT JOIN buildings b ON r.building_id = b.id " +
            "WHERE e.student_id = #{studentId} " +
            "ORDER BY e.exception_date DESC")
    List<CheckException> findByStudentId(@Param("studentId") Long studentId);
    
    @Select("SELECT e.*, s.name as student_name, s.student_no, s.department, s.class_name, " +
            "r.room_number, r.building_id as buildingId, b.name as building_name " +
            "FROM check_exceptions e " +
            "LEFT JOIN students s ON e.student_id = s.id " +
            "LEFT JOIN rooms r ON s.room_id = r.id " +
            "LEFT JOIN buildings b ON r.building_id = b.id " +
            "WHERE e.handled = #{handled} ORDER BY e.exception_date DESC")
    List<CheckException> findByHandled(@Param("handled") Integer handled);
    
    @Select("SELECT e.*, s.name as student_name, s.student_no, s.department, s.class_name, " +
            "r.room_number, r.building_id as buildingId, b.name as building_name " +
            "FROM check_exceptions e " +
            "LEFT JOIN students s ON e.student_id = s.id " +
            "LEFT JOIN rooms r ON s.room_id = r.id " +
            "LEFT JOIN buildings b ON r.building_id = b.id " +
            "WHERE e.exception_date BETWEEN #{startDate} AND #{endDate} " +
            "AND (#{buildingId} IS NULL OR r.building_id = #{buildingId}) " +
            "AND (#{exceptionType} IS NULL OR e.exception_type = #{exceptionType}) " +
            "AND (#{handled} IS NULL OR e.handled = #{handled}) " +
            "ORDER BY e.exception_date DESC")
    List<CheckException> search(@Param("startDate") LocalDate startDate,
                                @Param("endDate") LocalDate endDate,
                                @Param("buildingId") Long buildingId,
                                @Param("exceptionType") Integer exceptionType,
                                @Param("handled") Integer handled);
    
    @Insert("INSERT INTO check_exceptions (student_id, exception_date, exception_type, check_record_id, handled) " +
            "VALUES (#{studentId}, #{exceptionDate}, #{exceptionType}, #{checkRecordId}, 0)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CheckException exception);
    
    @Update("UPDATE check_exceptions SET handled = 1, handler_id = #{handlerId}, " +
            "handle_result = #{handleResult}, handle_time = NOW(), handle_note = #{handleNote} " +
            "WHERE id = #{id} AND handled = 0")
    int handle(@Param("id") Long id,
               @Param("handlerId") Long handlerId,
               @Param("handleResult") String handleResult,
               @Param("handleNote") String handleNote);

    @Update("UPDATE check_exceptions SET handled = 1, handle_result = #{handleResult}, " +
            "handle_note = #{handleNote}, handle_time = NOW() " +
            "WHERE student_id = #{studentId} AND exception_date = #{date} AND handled = 0")
    int markHandledByStudentAndDate(@Param("studentId") Long studentId,
                                    @Param("date") LocalDate date,
                                    @Param("handleResult") String handleResult,
                                    @Param("handleNote") String handleNote);

    @Select("SELECT COUNT(*) FROM check_exceptions WHERE student_id = #{studentId} " +
            "AND exception_date = #{date} AND exception_type = #{type}")
    int countByStudentDateAndType(@Param("studentId") Long studentId,
                                  @Param("date") LocalDate date,
                                  @Param("type") Integer type);

    @Delete("DELETE FROM check_exceptions WHERE exception_date BETWEEN #{startDate} AND #{endDate}")
    int deleteByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Select("SELECT COUNT(*) FROM check_exceptions WHERE exception_date = #{date} AND exception_type = #{type}")
    int countByDateAndType(@Param("date") LocalDate date, @Param("type") Integer type);
    
    @Select("SELECT COUNT(*) FROM check_exceptions WHERE handled = 0 AND exception_date = #{date}")
    int countUnhandledByDate(@Param("date") LocalDate date);

    @Select("SELECT COUNT(*) FROM check_exceptions WHERE exception_date BETWEEN #{startDate} AND #{endDate}")
    int countBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Select("SELECT exception_type, COUNT(*) as count FROM check_exceptions " +
            "WHERE exception_date = #{date} GROUP BY exception_type")
    List<java.util.Map<String, Object>> countByDateGroupByType(@Param("date") LocalDate date);

    @Select("SELECT e.*, s.name as student_name, s.student_no, s.department, s.class_name, " +
            "r.room_number, r.building_id as buildingId, b.name as building_name " +
            "FROM check_exceptions e " +
            "LEFT JOIN students s ON e.student_id = s.id " +
            "LEFT JOIN rooms r ON s.room_id = r.id " +
            "LEFT JOIN buildings b ON r.building_id = b.id " +
            "WHERE e.exception_date BETWEEN #{startDate} AND #{endDate} " +
            "AND (#{exceptionType} IS NULL OR e.exception_type = #{exceptionType}) " +
            "AND (#{handled} IS NULL OR e.handled = #{handled}) " +
            "AND (#{scopesJson} IS NULL OR EXISTS (SELECT 1 FROM JSON_TABLE(CAST(#{scopesJson} AS JSON), '$[*]' COLUMNS (scope_building_id BIGINT PATH '$.buildingId', scope_class_name VARCHAR(128) PATH '$.className')) scope_tbl WHERE (scope_tbl.scope_building_id IS NULL OR scope_tbl.scope_building_id = r.building_id) AND (scope_tbl.scope_class_name IS NULL OR scope_tbl.scope_class_name = IFNULL(s.class_name,'')))) " +
            "ORDER BY e.exception_date DESC, e.create_time DESC")
    List<CheckException> searchScoped(@Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate,
                                      @Param("scopesJson") String scopesJson,
                                      @Param("exceptionType") Integer exceptionType,
                                      @Param("handled") Integer handled);

    @Select("SELECT b.name AS name, e.exception_type AS type, e.handled, COUNT(*) AS count " +
            "FROM check_exceptions e " +
            "LEFT JOIN students s ON e.student_id = s.id " +
            "LEFT JOIN rooms r ON s.room_id = r.id " +
            "LEFT JOIN buildings b ON r.building_id = b.id " +
            "WHERE e.exception_date BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY b.name, e.exception_type, e.handled ORDER BY b.name")
    List<java.util.Map<String, Object>> countByBuilding(@Param("startDate") LocalDate startDate,
                                                        @Param("endDate") LocalDate endDate);

    @Select("SELECT s.class_name AS name, e.exception_type AS type, e.handled, COUNT(*) AS count " +
            "FROM check_exceptions e " +
            "LEFT JOIN students s ON e.student_id = s.id " +
            "WHERE e.exception_date BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY s.class_name, e.exception_type, e.handled ORDER BY s.class_name")
    List<java.util.Map<String, Object>> countByClassName(@Param("startDate") LocalDate startDate,
                                                         @Param("endDate") LocalDate endDate);

    @Select("SELECT e.exception_type AS type, e.handled, COUNT(*) AS count FROM check_exceptions e " +
            "LEFT JOIN students s ON e.student_id = s.id " +
            "LEFT JOIN rooms r ON s.room_id = r.id " +
            "WHERE e.exception_date BETWEEN #{startDate} AND #{endDate} " +
            "AND (#{scopesJson} IS NULL OR EXISTS (SELECT 1 FROM JSON_TABLE(CAST(#{scopesJson} AS JSON), '$[*]' COLUMNS (scope_building_id BIGINT PATH '$.buildingId', scope_class_name VARCHAR(128) PATH '$.className')) scope_tbl WHERE (scope_tbl.scope_building_id IS NULL OR scope_tbl.scope_building_id = r.building_id) AND (scope_tbl.scope_class_name IS NULL OR scope_tbl.scope_class_name = IFNULL(s.class_name,'')))) " +
            "GROUP BY e.exception_type, e.handled")
    List<java.util.Map<String, Object>> countRangeGroupByTypeAndHandled(@Param("startDate") LocalDate startDate,
                                                                       @Param("endDate") LocalDate endDate,
                                                                       @Param("scopesJson") String scopesJson);

    @Select("SELECT e.exception_type AS type, COUNT(*) AS count FROM check_exceptions e " +
            "LEFT JOIN students s ON e.student_id = s.id " +
            "LEFT JOIN rooms r ON s.room_id = r.id " +
            "WHERE e.exception_date BETWEEN #{startDate} AND #{endDate} " +
            "AND (#{scopesJson} IS NULL OR EXISTS (SELECT 1 FROM JSON_TABLE(CAST(#{scopesJson} AS JSON), '$[*]' COLUMNS (scope_building_id BIGINT PATH '$.buildingId', scope_class_name VARCHAR(128) PATH '$.className')) scope_tbl WHERE (scope_tbl.scope_building_id IS NULL OR scope_tbl.scope_building_id = r.building_id) AND (scope_tbl.scope_class_name IS NULL OR scope_tbl.scope_class_name = IFNULL(s.class_name,'')))) " +
            "GROUP BY e.exception_type")
    List<java.util.Map<String, Object>> countRangeGroupByType(@Param("startDate") LocalDate startDate,
                                                              @Param("endDate") LocalDate endDate,
                                                              @Param("scopesJson") String scopesJson);

    @Select("SELECT COUNT(*) FROM check_exceptions e " +
            "LEFT JOIN students s ON e.student_id = s.id " +
            "LEFT JOIN rooms r ON s.room_id = r.id " +
            "WHERE e.handled = 0 AND e.exception_date BETWEEN #{startDate} AND #{endDate} " +
            "AND (#{scopesJson} IS NULL OR EXISTS (SELECT 1 FROM JSON_TABLE(CAST(#{scopesJson} AS JSON), '$[*]' COLUMNS (scope_building_id BIGINT PATH '$.buildingId', scope_class_name VARCHAR(128) PATH '$.className')) scope_tbl WHERE (scope_tbl.scope_building_id IS NULL OR scope_tbl.scope_building_id = r.building_id) AND (scope_tbl.scope_class_name IS NULL OR scope_tbl.scope_class_name = IFNULL(s.class_name,''))))")
    int countUnhandledInRange(@Param("startDate") LocalDate startDate,
                              @Param("endDate") LocalDate endDate,
                              @Param("scopesJson") String scopesJson);
}