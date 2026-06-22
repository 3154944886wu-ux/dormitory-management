package com.dormitory.service;

import com.dormitory.mapper.NotificationMapper;
import com.dormitory.mapper.StudentMapper;
import com.dormitory.mapper.UserMapper;
import com.dormitory.model.Notification;
import com.dormitory.model.Student;
import com.dormitory.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationMapper notificationMapper;
    private final UserMapper userMapper;
    private final StudentMapper studentMapper;

    public NotificationService(NotificationMapper notificationMapper,
                               UserMapper userMapper,
                               StudentMapper studentMapper) {
        this.notificationMapper = notificationMapper;
        this.userMapper = userMapper;
        this.studentMapper = studentMapper;
    }

    public List<Notification> getMyNotifications(String username) {
        User user = userMapper.findByUsername(username);
        if (user == null) throw new RuntimeException("用户不存在");
        Student student = studentMapper.findByUserId(user.getId());
        if (student == null) throw new RuntimeException("学生信息不存在");
        return notificationMapper.findByRecipientId(student.getId());
    }

    public void createNotification(Long recipientId, Long batchId, String type, String content) {
        Notification notification = new Notification();
        notification.setRecipientId(recipientId);
        notification.setBatchId(batchId);
        notification.setType(type);
        notification.setContent(content);
        notification.setChannel("inner");
        notification.setStatus("sent");
        notificationMapper.insert(notification);
    }
}
