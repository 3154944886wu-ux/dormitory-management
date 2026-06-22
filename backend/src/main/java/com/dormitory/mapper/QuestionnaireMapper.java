package com.dormitory.mapper;

import com.dormitory.model.Questionnaire;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface QuestionnaireMapper {

    @Select("SELECT * FROM questionnaire ORDER BY id")
    List<Questionnaire> findAll();

    @Select("SELECT * FROM questionnaire WHERE id = #{id}")
    Questionnaire findById(Long id);

    @Select("SELECT * FROM questionnaire WHERE is_active = #{isActive} ORDER BY id")
    List<Questionnaire> findByIsActive(Integer isActive);

    @Select("SELECT * FROM questionnaire WHERE question_type = #{questionType} ORDER BY id")
    List<Questionnaire> findByQuestionType(String questionType);

    @Insert("INSERT INTO questionnaire(question_text, question_type, is_required, weight, is_active) " +
            "VALUES(#{questionText}, #{questionType}, #{isRequired}, #{weight}, #{isActive})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Questionnaire questionnaire);

    @Update("UPDATE questionnaire SET question_text = #{questionText}, question_type = #{questionType}, " +
            "is_required = #{isRequired}, weight = #{weight}, is_active = #{isActive} WHERE id = #{id}")
    int update(Questionnaire questionnaire);

    @Delete("DELETE FROM questionnaire WHERE id = #{id}")
    int deleteById(Long id);
}
