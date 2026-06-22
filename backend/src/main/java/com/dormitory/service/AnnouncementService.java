package com.dormitory.service;

import com.dormitory.mapper.AnnouncementMapper;
import com.dormitory.model.Announcement;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnnouncementService {
    
    private final AnnouncementMapper announcementMapper;
    
    public AnnouncementService(AnnouncementMapper announcementMapper) {
        this.announcementMapper = announcementMapper;
    }
    
    public List<Announcement> getAllAnnouncements() {
        return announcementMapper.findAll();
    }
    
    public List<Announcement> getPublishedAnnouncements() {
        return announcementMapper.findAllPublished();
    }
    
    public Announcement getAnnouncementById(Long id) {
        Announcement announcement = announcementMapper.findById(id);
        if (announcement != null) {
            announcementMapper.incrementViewCount(id);
        }
        return announcement;
    }
    
    @Transactional
    public Announcement createAnnouncement(Announcement announcement, Long publisherId) {
        announcement.setPublisherId(publisherId);
        announcement.setViewCount(0);
        announcement.setStatus(announcement.getStatus() != null ? announcement.getStatus() : 0);
        announcement.setIsTop(announcement.getIsTop() != null ? announcement.getIsTop() : 0);
        
        if (announcement.getStatus() == 1) {
            announcement.setPublishTime(LocalDateTime.now());
        }
        
        announcementMapper.insert(announcement);
        return announcementMapper.findById(announcement.getId());
    }
    
    @Transactional
    public Announcement updateAnnouncement(Long id, Announcement announcement) {
        Announcement existing = announcementMapper.findById(id);
        if (existing == null) {
            throw new RuntimeException("公告不存在");
        }
        
        announcement.setId(id);
        announcementMapper.update(announcement);
        return announcementMapper.findById(id);
    }
    
    @Transactional
    public void deleteAnnouncement(Long id) {
        announcementMapper.delete(id);
    }
    
    @Transactional
    public Announcement publishAnnouncement(Long id) {
        announcementMapper.publish(id);
        return announcementMapper.findById(id);
    }
    
    @Transactional
    public Announcement offlineAnnouncement(Long id) {
        announcementMapper.offline(id);
        return announcementMapper.findById(id);
    }
    
    @Transactional
    public Announcement toggleTop(Long id) {
        Announcement announcement = announcementMapper.findById(id);
        if (announcement == null) {
            throw new RuntimeException("公告不存在");
        }
        
        announcement.setIsTop(announcement.getIsTop() == 1 ? 0 : 1);
        announcementMapper.update(announcement);
        return announcementMapper.findById(id);
    }
}