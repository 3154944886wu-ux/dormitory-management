package com.dormitory.mapper;

import com.dormitory.model.QuestionOption;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface QuestionOptionMapper {

    @Select("SELECT * FROM question_option WHERE id = #{id}")
    QuestionOption findById(Long id);

    @Select("SELECT * FROM question_option WHERE q_id = #{qId} ORDER BY id")
    List<QuestionOption> findByQId(Long qId);

    @Insert("INSERT INTO question_option(q_id, option_text, option_value) " +
            "VALUES(#{qId}, #{optionText}, #{optionValue})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(QuestionOption option);

    @Update("UPDATE question_option SET q_id = #{qId}, option_text = #{optionText}, " +
            "option_value = #{optionValue} WHERE id = #{id}")
    int update(QuestionOption option);

    @Delete("DELETE FROM question_option WHERE id = #{id}")
    int deleteById(Long id);

    @Delete("DELETE FROM question_option WHERE q_id = #{qId}")
    int deleteByQId(Long qId);
}
