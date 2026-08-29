package com.dormitory.mapper;

import com.dormitory.model.CheckInRecord;
import org.apache.ibatis.annotations.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface CheckInMapper {
    
    @Select("SELECT c.*, s.name as student_name, s.student_no, s.class_name as className, r.room_number, r.building_id as buildingId, b.name as building_name " +
            "FROM check_in_records c " +
            "LEFT JOIN students s ON c.student_id = s.id " +
            "LEFT JOIN rooms r ON c.room_id = r.id " +
            "LEFT JOIN buildings b ON r.building_id = b.id " +
            "WHERE c.id = #{id}")
    CheckInRecord findById(@Param("id") Long id);
    
    @Select("SELECT c.*, s.name as student_name, s.student_no, s.class_name as className, r.room_number, r.building_id as buildingId, b.name as building_name " +
            "FROM check_in_records c " +
            "LEFT JOIN students s ON c.student_id = s.id " +
            "LEFT JOIN rooms r ON c.room_id = r.id " +
            "LEFT JOIN buildings b ON r.building_id = b.id " +
            "ORDER BY c.check_date DESC, c.check_time DESC " +
            "LIMIT #{offset}, #{limit}")
    List<CheckInRecord> findAll(@Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT COUNT(*) FROM check_in_records")
    int count();
    
    @Select("SELECT c.*, s.name as student_name, s.student_no, s.class_name as className, r.room_number, r.building_id as buildingId, b.name as building_name " +
            "FROM check_in_records c " +
            "LEFT JOIN students s ON c.student_id = s.id " +
            "LEFT JOIN rooms r ON c.room_id = r.id " +
            "LEFT JOIN buildings b ON r.building_id = b.id " +
            "WHERE c.student_id = #{studentId} " +
            "ORDER BY c.check_date DESC")
    List<CheckInRecord> findByStudentId(@Param("studentId") Long studentId);
    
    @Select("SELECT c.*, s.name as student_name, s.student_no, s.class_name as className, r.room_number, r.building_id as buildingId, b.name as building_name " +
            "FROM check_in_records c " +
            "LEFT JOIN students s ON c.student_id = s.id " +
            "LEFT JOIN rooms r ON c.room_id = r.id " +
            "LEFT JOIN buildings b ON r.building_id = b.id " +
            "WHERE c.student_id = #{studentId} AND c.check_date = #{checkDate}")
    CheckInRecord findByStudentAndDate(@Param("studentId") Long studentId, @Param("checkDate") LocalDate checkDate);
    
    @Select("SELECT c.*, s.name as student_name, s.student_no, s.class_name as className, r.room_number, r.building_id as buildingId, b.name as building_name " +
            "FROM check_in_records c " +
            "LEFT JOIN students s ON c.student_id = s.id " +
            "LEFT JOIN rooms r ON c.room_id = r.id " +
            "LEFT JOIN buildings b ON r.building_id = b.id " +
            "WHERE c.check_date = #{checkDate} " +
            "ORDER BY c.check_time DESC")
    List<CheckInRecord> findByDate(@Param("checkDate") LocalDate checkDate);
    
    @Select("SELECT c.*, s.name as student_name, s.student_no, s.class_name as className, r.room_number, r.building_id as buildingId, b.name as building_name " +
            "FROM check_in_records c " +
            "LEFT JOIN students s ON c.student_id = s.id " +
            "LEFT JOIN rooms r ON c.room_id = r.id " +
            "LEFT JOIN buildings b ON r.building_id = b.id " +
            "WHERE c.check_date BETWEEN #{startDate} AND #{endDate} " +
            "AND (#{buildingId} IS NULL OR r.building_id = #{buildingId}) " +
            "AND (#{status} IS NULL OR c.status = #{status}) " +
            "ORDER BY c.check_date DESC, c.check_time DESC")
    List<CheckInRecord> search(@Param("startDate") LocalDate startDate, 
                               @Param("endDate") LocalDate endDate,
                               @Param("buildingId") Long buildingId,
                               @Param("status") Integer status);
    
    @Insert("INSERT INTO check_in_records (student_id, room_id, check_date, check_time, check_type, " +
            "latitude, longitude, location_accuracy, device_info, ip_address, status, remark) " +
            "VALUES (#{studentId}, #{roomId}, #{checkDate}, #{checkTime}, #{checkType}, " +
            "#{latitude}, #{longitude}, #{locationAccuracy}, #{deviceInfo}, #{ipAddress}, #{status}, #{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CheckInRecord record);
    
    @Update("UPDATE check_in_records SET status = #{status}, remark = #{remark} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status, @Param("remark") String remark);

    @Delete("DELETE FROM check_in_records WHERE check_date BETWEEN #{startDate} AND #{endDate}")
    int deleteByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Delete("DELETE FROM check_in_records WHERE student_id = #{studentId} AND status = 3 AND check_date >= #{fromDate}")
    int deleteLeaveRecordsFromDate(@Param("studentId") Long studentId, @Param("fromDate") LocalDate fromDate);
    
    @Select("SELECT COUNT(*) FROM check_in_records WHERE check_date = #{checkDate} AND status = #{status}")
    int countByDateAndStatus(@Param("checkDate") LocalDate checkDate, @Param("status") Integer status);
    
    @Select("SELECT c.status, COUNT(*) as count FROM check_in_records c " +
            "WHERE c.check_date = #{checkDate} GROUP BY c.status")
    List<Map<String, Object>> countByDateGroupByStatus(@Param("checkDate") LocalDate checkDate);

    @Select("SELECT c.*, s.name as student_name, s.student_no, s.class_name as className, r.room_number, r.building_id as buildingId, b.name as building_name " +
            "FROM check_in_records c " +
            "LEFT JOIN students s ON c.student_id = s.id " +
            "LEFT JOIN rooms r ON c.room_id = r.id " +
            "LEFT JOIN buildings b ON r.building_id = b.id " +
            "WHERE c.check_date BETWEEN #{startDate} AND #{endDate} " +
            "AND (#{buildingId} IS NULL OR r.building_id = #{buildingId}) " +
            "AND (#{status} IS NULL OR c.status = #{status}) " +
            "AND (#{studentName} IS NULL OR s.name LIKE CONCAT('%', #{studentName}, '%')) " +
            "AND (#{studentNo} IS NULL OR s.student_no LIKE CONCAT('%', #{studentNo}, '%')) " +
            "ORDER BY c.check_date DESC, c.check_time DESC " +
            "LIMIT #{offset}, #{limit}")
    List<CheckInRecord> searchPaged(@Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate,
                                    @Param("buildingId") Long buildingId,
                                    @Param("status") Integer status,
                                    @Param("studentName") String studentName,
                                    @Param("studentNo") String studentNo,
                                    @Param("offset") int offset,
                                    @Param("limit") int limit);

    @Select("SELECT COUNT(*) " +
            "FROM check_in_records c " +
            "LEFT JOIN students s ON c.student_id = s.id " +
            "LEFT JOIN rooms r ON c.room_id = r.id " +
            "WHERE c.check_date BETWEEN #{startDate} AND #{endDate} " +
            "AND (#{buildingId} IS NULL OR r.building_id = #{buildingId}) " +
            "AND (#{status} IS NULL OR c.status = #{status}) " +
            "AND (#{studentName} IS NULL OR s.name LIKE CONCAT('%', #{studentName}, '%')) " +
            "AND (#{studentNo} IS NULL OR s.student_no LIKE CONCAT('%', #{studentNo}, '%'))")
    int countSearch(@Param("startDate") LocalDate startDate,
                    @Param("endDate") LocalDate endDate,
                    @Param("buildingId") Long buildingId,
                    @Param("status") Integer status,
                    @Param("studentName") String studentName,
                    @Param("studentNo") String studentNo);

    @Select("SELECT c.*, s.name as student_name, s.student_no, s.class_name as className, r.room_number, r.building_id as buildingId, b.name as building_name " +
            "FROM check_in_records c " +
            "LEFT JOIN students s ON c.student_id = s.id " +
            "LEFT JOIN rooms r ON c.room_id = r.id " +
            "LEFT JOIN buildings b ON r.building_id = b.id " +
            "WHERE c.check_date BETWEEN #{startDate} AND #{endDate} " +
            "AND (#{status} IS NULL OR c.status = #{status}) " +
            "AND (#{scopesJson} IS NULL OR EXISTS (SELECT 1 FROM JSON_TABLE(CAST(#{scopesJson} AS JSON), '$[*]' COLUMNS (scope_building_id BIGINT PATH '$.buildingId', scope_class_name VARCHAR(128) PATH '$.className')) scope_tbl WHERE (scope_tbl.scope_building_id IS NULL OR scope_tbl.scope_building_id = r.building_id) AND (scope_tbl.scope_class_name IS NULL OR scope_tbl.scope_class_name = IFNULL(s.class_name,'')))) " +
            "ORDER BY c.check_date DESC, c.check_time DESC " +
            "LIMIT #{offset}, #{limit}")
    List<CheckInRecord> searchScopedPaged(@Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate,
                                          @Param("scopesJson") String scopesJson,
                                          @Param("status") Integer status,
                                          @Param("offset") int offset,
                                          @Param("limit") int limit);

    @Select("SELECT COUNT(*) " +
            "FROM check_in_records c " +
            "LEFT JOIN students s ON c.student_id = s.id " +
            "LEFT JOIN rooms r ON c.room_id = r.id " +
            "WHERE c.check_date BETWEEN #{startDate} AND #{endDate} " +
            "AND (#{status} IS NULL OR c.status = #{status}) " +
            "AND (#{scopesJson} IS NULL OR EXISTS (SELECT 1 FROM JSON_TABLE(CAST(#{scopesJson} AS JSON), '$[*]' COLUMNS (scope_building_id BIGINT PATH '$.buildingId', scope_class_name VARCHAR(128) PATH '$.className')) scope_tbl WHERE (scope_tbl.scope_building_id IS NULL OR scope_tbl.scope_building_id = r.building_id) AND (scope_tbl.scope_class_name IS NULL OR scope_tbl.scope_class_name = IFNULL(s.class_name,''))))")
    int countScopedSearch(@Param("startDate") LocalDate startDate,
                          @Param("endDate") LocalDate endDate,
                          @Param("scopesJson") String scopesJson,
                          @Param("status") Integer status);

    @Select("SELECT c.status, COUNT(*) AS count FROM check_in_records c " +
            "LEFT JOIN students s ON c.student_id = s.id " +
            "LEFT JOIN rooms r ON c.room_id = r.id " +
            "WHERE c.check_date BETWEEN #{startDate} AND #{endDate} " +
            "AND (#{scopesJson} IS NULL OR EXISTS (SELECT 1 FROM JSON_TABLE(CAST(#{scopesJson} AS JSON), '$[*]' COLUMNS (scope_building_id BIGINT PATH '$.buildingId', scope_class_name VARCHAR(128) PATH '$.className')) scope_tbl WHERE (scope_tbl.scope_building_id IS NULL OR scope_tbl.scope_building_id = r.building_id) AND (scope_tbl.scope_class_name IS NULL OR scope_tbl.scope_class_name = IFNULL(s.class_name,'')))) " +
            "GROUP BY c.status")
    List<Map<String, Object>> countRangeGroupByStatus(@Param("startDate") LocalDate startDate,
                                                      @Param("endDate") LocalDate endDate,
                                                      @Param("scopesJson") String scopesJson);

    @Select("SELECT c.check_date AS checkDate, c.status, COUNT(*) AS count FROM check_in_records c " +
            "LEFT JOIN students s ON c.student_id = s.id " +
            "LEFT JOIN rooms r ON c.room_id = r.id " +
            "WHERE c.check_date BETWEEN #{startDate} AND #{endDate} " +
            "AND (#{scopesJson} IS NULL OR EXISTS (SELECT 1 FROM JSON_TABLE(CAST(#{scopesJson} AS JSON), '$[*]' COLUMNS (scope_building_id BIGINT PATH '$.buildingId', scope_class_name VARCHAR(128) PATH '$.className')) scope_tbl WHERE (scope_tbl.scope_building_id IS NULL OR scope_tbl.scope_building_id = r.building_id) AND (scope_tbl.scope_class_name IS NULL OR scope_tbl.scope_class_name = IFNULL(s.class_name,'')))) " +
            "GROUP BY c.check_date, c.status ORDER BY c.check_date")
    List<Map<String, Object>> countDailyGroupByStatus(@Param("startDate") LocalDate startDate,
                                                       @Param("endDate") LocalDate endDate,
                                                       @Param("scopesJson") String scopesJson);
}