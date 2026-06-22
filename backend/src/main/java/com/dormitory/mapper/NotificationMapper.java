package com.dormitory.mapper;

import com.dormitory.model.Notification;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NotificationMapper {

    @Select("SELECT n.*, s.name as student_name, db.name as batch_name " +
            "FROM notification n " +
            "LEFT JOIN students s ON n.recipient_id = s.id " +
            "LEFT JOIN dorm_batch db ON n.batch_id = db.id " +
            "ORDER BY n.create_time DESC")
    List<Notification> findAll();

    @Select("SELECT n.*, s.name as student_name, db.name as batch_name " +
            "FROM notification n " +
            "LEFT JOIN students s ON n.recipient_id = s.id " +
            "LEFT JOIN dorm_batch db ON n.batch_id = db.id " +
            "WHERE n.id = #{id}")
    Notification findById(Long id);

    @Select("SELECT n.*, s.name as student_name, db.name as batch_name " +
            "FROM notification n " +
            "LEFT JOIN students s ON n.recipient_id = s.id " +
            "LEFT JOIN dorm_batch db ON n.batch_id = db.id " +
            "WHERE n.recipient_id = #{recipientId} " +
            "ORDER BY n.create_time DESC")
    List<Notification> findByRecipientId(Long recipientId);

    @Select("SELECT n.*, s.name as student_name, db.name as batch_name " +
            "FROM notification n " +
            "LEFT JOIN students s ON n.recipient_id = s.id " +
            "LEFT JOIN dorm_batch db ON n.batch_id = db.id " +
            "WHERE n.batch_id = #{batchId} " +
            "ORDER BY n.create_time DESC")
    List<Notification> findByBatchId(Long batchId);

    @Insert("INSERT INTO notification(recipient_id, batch_id, type, content, channel, status) " +
            "VALUES(#{recipientId}, #{batchId}, #{type}, #{content}, #{channel}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Notification notification);

    @Update("UPDATE notification SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    @Delete("DELETE FROM notification WHERE id = #{id}")
    int deleteById(Long id);

    @Delete("DELETE FROM notification WHERE batch_id = #{batchId}")
    int deleteByBatchId(Long batchId);
}
