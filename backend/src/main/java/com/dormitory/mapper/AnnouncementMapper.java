package com.dormitory.mapper;

import com.dormitory.model.Announcement;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AnnouncementMapper {
    
    @Select("SELECT a.*, u.username as publisherName FROM announcements a " +
            "LEFT JOIN users u ON a.publisher_id = u.id " +
            "WHERE a.status = 1 " +
            "ORDER BY a.is_top DESC, a.publish_time DESC")
    List<Announcement> findAllPublished();
    
    @Select("SELECT a.*, u.username as publisherName FROM announcements a " +
            "LEFT JOIN users u ON a.publisher_id = u.id " +
            "ORDER BY a.is_top DESC, a.create_time DESC")
    List<Announcement> findAll();
    
    @Select("SELECT a.*, u.username as publisherName FROM announcements a " +
            "LEFT JOIN users u ON a.publisher_id = u.id " +
            "WHERE a.id = #{id}")
    Announcement findById(@Param("id") Long id);
    
    @Insert("INSERT INTO announcements (title, content, type, status, publisher_id, publish_time, view_count, is_top, create_time, update_time) " +
            "VALUES (#{title}, #{content}, #{type}, #{status}, #{publisherId}, #{publishTime}, 0, #{isTop}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Announcement announcement);
    
    @Update("UPDATE announcements SET " +
            "title = #{title}, " +
            "content = #{content}, " +
            "type = #{type}, " +
            "status = #{status}, " +
            "publish_time = #{publishTime}, " +
            "is_top = #{isTop}, " +
            "update_time = NOW() " +
            "WHERE id = #{id}")
    int update(Announcement announcement);
    
    @Update("UPDATE announcements SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(@Param("id") Long id);
    
    @Delete("DELETE FROM announcements WHERE id = #{id}")
    int delete(@Param("id") Long id);
    
    @Update("UPDATE announcements SET status = 2, update_time = NOW() WHERE id = #{id}")
    int offline(@Param("id") Long id);
    
    @Update("UPDATE announcements SET status = 1, publish_time = NOW(), update_time = NOW() WHERE id = #{id}")
    int publish(@Param("id") Long id);
    
    @Select("SELECT COUNT(*) FROM announcements WHERE status = 1")
    int countPublished();
    
    @Select("SELECT COUNT(*) FROM announcements WHERE type = #{type}")
    int countByType(@Param("type") Integer type);
}