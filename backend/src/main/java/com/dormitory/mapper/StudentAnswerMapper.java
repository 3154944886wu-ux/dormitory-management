package com.dormitory.mapper;

import com.dormitory.model.StudentAnswer;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StudentAnswerMapper {

    @Select("SELECT sa.*, s.name as student_name, q.question_text, qo.option_text " +
            "FROM student_answer sa " +
            "LEFT JOIN students s ON sa.student_id = s.id " +
            "LEFT JOIN questionnaire q ON sa.q_id = q.id " +
            "LEFT JOIN question_option qo ON sa.option_id = qo.id " +
            "ORDER BY sa.submit_time DESC")
    List<StudentAnswer> findAll();

    @Select("SELECT sa.*, s.name as student_name, q.question_text, qo.option_text " +
            "FROM student_answer sa " +
            "LEFT JOIN students s ON sa.student_id = s.id " +
            "LEFT JOIN questionnaire q ON sa.q_id = q.id " +
            "LEFT JOIN question_option qo ON sa.option_id = qo.id " +
            "WHERE sa.id = #{id}")
    StudentAnswer findById(Long id);

    @Select("SELECT sa.*, s.name as student_name, q.question_text, qo.option_text " +
            "FROM student_answer sa " +
            "LEFT JOIN students s ON sa.student_id = s.id " +
            "LEFT JOIN questionnaire q ON sa.q_id = q.id " +
            "LEFT JOIN question_option qo ON sa.option_id = qo.id " +
            "WHERE sa.student_id = #{studentId} " +
            "ORDER BY sa.submit_time DESC")
    List<StudentAnswer> findByStudentId(Long studentId);

    @Select("SELECT sa.*, s.name as student_name, q.question_text, qo.option_text " +
            "FROM student_answer sa " +
            "LEFT JOIN students s ON sa.student_id = s.id " +
            "LEFT JOIN questionnaire q ON sa.q_id = q.id " +
            "LEFT JOIN question_option qo ON sa.option_id = qo.id " +
            "WHERE sa.q_id = #{qId} " +
            "ORDER BY sa.submit_time DESC")
    List<StudentAnswer> findByQId(Long qId);

    @Select("SELECT sa.*, s.name as student_name, q.question_text, qo.option_text " +
            "FROM student_answer sa " +
            "LEFT JOIN students s ON sa.student_id = s.id " +
            "LEFT JOIN questionnaire q ON sa.q_id = q.id " +
            "LEFT JOIN question_option qo ON sa.option_id = qo.id " +
            "WHERE sa.student_id = #{studentId} AND sa.q_id = #{qId}")
    StudentAnswer findByStudentAndQuestion(@Param("studentId") Long studentId, @Param("qId") Long qId);

    @Insert("INSERT INTO student_answer(student_id, q_id, option_id, submit_time) " +
            "VALUES(#{studentId}, #{qId}, #{optionId}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(StudentAnswer answer);

    @Update("UPDATE student_answer SET option_id = #{optionId} WHERE id = #{id}")
    int update(StudentAnswer answer);

    @Select("SELECT COUNT(*) FROM student_answer WHERE q_id = #{qId}")
    int countByQId(Long qId);

    @Delete("DELETE FROM student_answer WHERE id = #{id}")
    int deleteById(Long id);

    @Delete("DELETE FROM student_answer WHERE student_id = #{studentId}")
    int deleteByStudentId(Long studentId);

    @Select("<script>" +
            "SELECT sa.*, s.name as student_name, q.question_text, qo.option_text " +
            "FROM student_answer sa " +
            "LEFT JOIN students s ON sa.student_id = s.id " +
            "LEFT JOIN questionnaire q ON sa.q_id = q.id " +
            "LEFT JOIN question_option qo ON sa.option_id = qo.id " +
            "WHERE sa.student_id IN (" +
            "<foreach collection='ids' item='id' separator=','>#{id}</foreach>" +
            ") ORDER BY sa.student_id, sa.q_id" +
            "</script>")
    List<StudentAnswer> findByStudentIds(@Param("ids") List<Long> studentIds);
}
