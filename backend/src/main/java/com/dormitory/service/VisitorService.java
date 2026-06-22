package com.dormitory.service;

import com.dormitory.mapper.RoomMapper;
import com.dormitory.mapper.VisitorMapper;
import com.dormitory.model.Room;
import com.dormitory.model.Visitor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VisitorService {
    
    private final VisitorMapper visitorMapper;
    private final RoomMapper roomMapper;
    
    public VisitorService(VisitorMapper visitorMapper, RoomMapper roomMapper) {
        this.visitorMapper = visitorMapper;
        this.roomMapper = roomMapper;
    }
    
    public List<Visitor> findAll() {
        return visitorMapper.findAll();
    }
    
    public Visitor findById(Long id) {
        return visitorMapper.findById(id);
    }
    
    public List<Visitor> findByRoomId(Long roomId) {
        return visitorMapper.findByRoomId(roomId);
    }
    
    public List<Visitor> findByStatus(Integer status) {
        return visitorMapper.findByStatus(status);
    }
    
    public List<Visitor> searchByName(String name) {
        return visitorMapper.findByName(name);
    }
    
    public List<Visitor> findByDate(LocalDateTime date) {
        return visitorMapper.findByDate(date);
    }
    
    public int getActiveCount() {
        return visitorMapper.countActive();
    }
    
    @Transactional
    public Long register(Visitor visitor) {
        // 验证房间存在
        Room room = roomMapper.findById(visitor.getRoomId());
        if (room == null) {
            throw new RuntimeException("房间不存在");
        }
        
        if (visitor.getVisitTime() == null) {
            visitor.setVisitTime(LocalDateTime.now());
        }
        visitor.setStatus(1); // 在访
        
        visitorMapper.insert(visitor);
        return visitor.getId();
    }
    
    @Transactional
    public void leave(Long id) {
        Visitor visitor = visitorMapper.findById(id);
        if (visitor == null) {
            throw new RuntimeException("访客记录不存在");
        }
        if (visitor.getStatus() == 0) {
            throw new RuntimeException("该访客已离开");
        }
        
        visitor.setStatus(0);
        visitor.setLeaveTime(LocalDateTime.now());
        visitorMapper.update(visitor);
    }
    
    @Transactional
    public void update(Visitor visitor) {
        Visitor existing = visitorMapper.findById(visitor.getId());
        if (existing == null) {
            throw new RuntimeException("访客记录不存在");
        }
        
        visitorMapper.update(visitor);
    }
    
    @Transactional
    public void delete(Long id) {
        Visitor visitor = visitorMapper.findById(id);
        if (visitor == null) {
            throw new RuntimeException("访客记录不存在");
        }
        visitorMapper.deleteById(id);
    }
}