package com.dormitory.mapper;

import com.dormitory.model.InspectionItem;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface InspectionItemMapper {

    @Select("SELECT * FROM inspection_items WHERE id = #{id}")
    InspectionItem findById(Long id);

    @Select("SELECT * FROM inspection_items WHERE status = 1 ORDER BY category, sort_order")
    List<InspectionItem> findAllActive();

    @Select("SELECT * FROM inspection_items WHERE category = #{category} AND status = 1 ORDER BY sort_order")
    List<InspectionItem> findByCategory(String category);

    @Select("SELECT * FROM inspection_items ORDER BY category, sort_order")
    List<InspectionItem> findAll();

    @Select("SELECT * FROM inspection_items ORDER BY category, sort_order " +
            "LIMIT #{offset}, #{limit}")
    List<InspectionItem> findAllPaginated(@Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM inspection_items")
    int count();

    @Insert("INSERT INTO inspection_items (name, category, standard, max_score, status, sort_order, create_time, update_time) " +
            "VALUES (#{name}, #{category}, #{standard}, #{maxScore}, #{status}, #{sortOrder}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(InspectionItem item);

    @Update("UPDATE inspection_items SET name = #{name}, category = #{category}, " +
            "standard = #{standard}, max_score = #{maxScore}, sort_order = #{sortOrder}, " +
            "status = #{status}, update_time = NOW() WHERE id = #{id}")
    int update(InspectionItem item);

    @Delete("DELETE FROM inspection_items WHERE id = #{id}")
    int delete(Long id);
}
