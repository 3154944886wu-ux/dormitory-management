package com.dormitory.mapper;

import com.dormitory.model.Student;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StudentMapper {
    
    @Select("SELECT s.*, r.room_number, r.floor, r.room_type, r.current_count AS occupancy, r.capacity, r.building_id AS buildingId, b.name as building_name " +
             "FROM students s " +
             "LEFT JOIN rooms r ON s.room_id = r.id " +
             "LEFT JOIN buildings b ON r.building_id = b.id " +
             "ORDER BY s.student_no")
    List<Student> findAll();
    
    @Select("SELECT s.*, r.room_number, r.floor, r.room_type, r.current_count AS occupancy, r.capacity, r.building_id AS buildingId, b.name as building_name " +
             "FROM students s " +
             "LEFT JOIN rooms r ON s.room_id = r.id " +
             "LEFT JOIN buildings b ON r.building_id = b.id " +
             "WHERE s.id = #{id}")
    Student findById(Long id);
    
    @Select("SELECT s.*, r.room_number, r.floor, r.room_type, r.current_count AS occupancy, r.capacity, r.building_id AS buildingId, b.name as building_name " +
             "FROM students s " +
             "LEFT JOIN rooms r ON s.room_id = r.id " +
             "LEFT JOIN buildings b ON r.building_id = b.id " +
             "WHERE s.student_no = #{studentNo}")
    Student findByStudentNo(String studentNo);
    
    @Select("SELECT s.*, r.room_number, r.floor, r.room_type, r.current_count AS occupancy, r.capacity, r.building_id AS buildingId, b.name as building_name " +
             "FROM students s " +
             "LEFT JOIN rooms r ON s.room_id = r.id " +
             "LEFT JOIN buildings b ON r.building_id = b.id " +
             "WHERE s.room_id = #{roomId}")
    List<Student> findByRoomId(Long roomId);
    
    @Select("SELECT s.*, r.room_number, r.floor, r.room_type, r.current_count AS occupancy, r.capacity, r.building_id AS buildingId, b.name as building_name " +
             "FROM students s " +
             "LEFT JOIN rooms r ON s.room_id = r.id " +
             "LEFT JOIN buildings b ON r.building_id = b.id " +
             "WHERE s.name LIKE CONCAT('%', #{name}, '%')")
    List<Student> findByName(String name);

    @Select("SELECT s.*, r.room_number, r.floor, r.room_type, r.current_count AS occupancy, r.capacity, r.building_id AS buildingId, b.name as building_name " +
             "FROM students s " +
             "LEFT JOIN rooms r ON s.room_id = r.id " +
             "LEFT JOIN buildings b ON r.building_id = b.id " +
             "WHERE s.name LIKE CONCAT('%', #{name}, '%') " +
             "ORDER BY s.student_no " +
             "LIMIT #{size} OFFSET #{offset}")
    List<Student> findByNameWithPagination(@Param("name") String name,
                                          @Param("offset") int offset,
                                          @Param("size") int size);

    @Select("SELECT COUNT(*) FROM students WHERE name LIKE CONCAT('%', #{name}, '%')")
    long countByName(String name);

    @Select("SELECT s.*, r.room_number, r.floor, r.room_type, r.current_count AS occupancy, r.capacity, r.building_id AS buildingId, b.name as building_name " +
             "FROM students s " +
             "LEFT JOIN rooms r ON s.room_id = r.id " +
             "LEFT JOIN buildings b ON r.building_id = b.id " +
             "WHERE s.room_id = #{roomId} " +
             "ORDER BY s.student_no " +
             "LIMIT #{size} OFFSET #{offset}")
    List<Student> findByRoomIdWithPagination(@Param("roomId") Long roomId,
                                            @Param("offset") int offset,
                                            @Param("size") int size);

    @Select("SELECT COUNT(*) FROM students WHERE room_id = #{roomId}")
    long countByRoomIdAll(Long roomId);
    
    @Insert("INSERT INTO students(student_no, name, gender, phone, department, class_name, college_id, major_id, " +
             "dorm_batch_id, id_card, room_id, bed_number, check_in_date, status) " +
             "VALUES(#{studentNo}, #{name}, #{gender}, #{phone}, #{department}, #{className}, " +
             "#{collegeId}, #{majorId}, #{dormBatchId}, " +
             "#{idCard}, #{roomId}, #{bedNumber}, #{checkInDate}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Student student);
    
    @Update("UPDATE students SET student_no=#{studentNo}, name=#{name}, gender=#{gender}, " +
             "phone=#{phone}, department=#{department}, class_name=#{className}, " +
             "college_id=#{collegeId}, major_id=#{majorId}, " +
             "dorm_batch_id=#{dormBatchId}, id_card=#{idCard}, " +
             "room_id=#{roomId}, bed_number=#{bedNumber}, check_in_date=#{checkInDate}, " +
             "check_out_date=#{checkOutDate}, status=#{status} WHERE id=#{id}")
    int update(Student student);
    
    @Delete("DELETE FROM students WHERE id = #{id}")
    int deleteById(Long id);

    @Update("UPDATE students SET user_id = #{userId} WHERE id = #{id}")
    int updateUserId(@Param("id") Long id, @Param("userId") Long userId);

    @Select("SELECT COUNT(*) FROM students WHERE user_id = #{userId}")
    int countByUserId(Long userId);

    @Select("SELECT s.*, r.room_number, r.floor, r.room_type, r.current_count AS occupancy, r.capacity, r.building_id AS buildingId, b.name as building_name " +
            "FROM students s " +
            "LEFT JOIN rooms r ON s.room_id = r.id " +
            "LEFT JOIN buildings b ON r.building_id = b.id " +
            "WHERE s.user_id = #{userId}")
    Student findByUserId(Long userId);
    
    @Select("SELECT COUNT(*) FROM students WHERE room_id = #{roomId} AND status = 1")
    int countByRoomId(Long roomId);
    
    @Select("SELECT COUNT(*) FROM students WHERE status = 1")
    int count();

    @Update("UPDATE students SET dorm_batch_id = #{batchId} " +
            "WHERE college_id = #{collegeId} AND status = 1 AND room_id IS NULL AND dorm_batch_id IS NULL")
    int updateDormBatchIdByCollege(@Param("collegeId") Integer collegeId, @Param("batchId") Long batchId);

    @Update("UPDATE students SET dorm_batch_id = NULL WHERE dorm_batch_id = #{batchId}")
    int clearDormBatchIdByBatchId(Long batchId);

    @Select("SELECT s.*, r.room_number, r.floor, r.room_type, r.current_count AS occupancy, r.capacity, r.building_id AS buildingId, b.name as building_name " +
            "FROM students s " +
            "LEFT JOIN rooms r ON s.room_id = r.id " +
            "LEFT JOIN buildings b ON r.building_id = b.id " +
            "WHERE s.dorm_batch_id = #{batchId}")
    List<Student> findByDormBatchId(Long batchId);

    @Select("SELECT COUNT(*) FROM students WHERE dorm_batch_id = #{batchId}")
    int countByDormBatchId(Long batchId);

    @Select("SELECT s.*, r.room_number, r.floor, r.room_type, r.current_count AS occupancy, r.capacity, r.building_id AS buildingId, b.name as building_name " +
            "FROM students s " +
            "LEFT JOIN rooms r ON s.room_id = r.id " +
            "LEFT JOIN buildings b ON r.building_id = b.id " +
            "ORDER BY s.student_no " +
            "LIMIT #{size} OFFSET #{offset}")
    List<Student> findAllWithPagination(@Param("offset") int offset, @Param("size") int size);

    @Select("SELECT DISTINCT class_name FROM students WHERE class_name IS NOT NULL AND class_name != '' ORDER BY class_name")
    List<String> findDistinctClassNames();

    @Select("SELECT COUNT(*) FROM students WHERE class_name = #{className}")
    int countByClassName(String className);
}