package com.dormitory.service;

import com.dormitory.mapper.BedMapper;
import com.dormitory.model.Bed;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BedService {

    private final BedMapper bedMapper;

    public BedService(BedMapper bedMapper) {
        this.bedMapper = bedMapper;
    }

    public List<Bed> findAvailableByRoomId(Long roomId) {
        return bedMapper.findAvailableByRoomId(roomId);
    }
}
