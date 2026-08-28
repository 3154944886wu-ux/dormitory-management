package com.dormitory.service;

import com.dormitory.mapper.BedMapper;
import com.dormitory.mapper.BuildingMapper;
import com.dormitory.mapper.RoomMapper;
import com.dormitory.model.Bed;
import com.dormitory.model.Building;
import com.dormitory.model.Room;
import com.dormitory.utils.BedLayout;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoomService {
    
    private final RoomMapper roomMapper;
    private final BuildingMapper buildingMapper;
    private final BedMapper bedMapper;
    
    public RoomService(RoomMapper roomMapper, BuildingMapper buildingMapper, BedMapper bedMapper) {
        this.roomMapper = roomMapper;
        this.buildingMapper = buildingMapper;
        this.bedMapper = bedMapper;
    }

    /** 按房间配置生成对应床位记录，供智能选宿匹配使用 */
    private void seedBeds(Room room) {
        for (Bed bed : BedLayout.forRoom(room)) {
            bed.setRoomId(room.getId());
            bedMapper.insert(bed);
        }
    }
    
    public List<Room> findAll() {
        return roomMapper.findAll();
    }
    
    public List<Room> findByBuildingId(Long buildingId) {
        return roomMapper.findByBuildingId(buildingId);
    }

    public List<Room> findAllWithPagination(int offset, int size) {
        return roomMapper.findAllWithPagination(offset, size);
    }

    public List<Room> findByBuildingIdWithPagination(Long buildingId, int offset, int size) {
        return roomMapper.findByBuildingIdWithPagination(buildingId, offset, size);
    }

    public long countAll() {
        return roomMapper.count();
    }

    public long countByBuildingId(Long buildingId) {
        return roomMapper.countByBuildingId(buildingId);
    }

    public Room findById(Long id) {
        return roomMapper.findById(id);
    }
    
    @Transactional
    public Long create(Room room) {
        // 检查楼栋是否存在
        Building building = buildingMapper.findById(room.getBuildingId());
        if (building == null) {
            throw new RuntimeException("楼栋不存在");
        }
        
        // 检查房间号是否重复
        Room existing = roomMapper.findByBuildingAndNumber(
            room.getBuildingId(), room.getRoomNumber());
        if (existing != null) {
            throw new RuntimeException("该楼栋已存在相同房间号");
        }
        
        // 设置默认值
        if (room.getCurrentCount() == null) {
            room.setCurrentCount(0);
        }
        if (room.getStatus() == null) {
            room.setStatus(1);
        }
        
        roomMapper.insert(room);
        seedBeds(room);
        return room.getId();
    }
    
    @Transactional
    public void update(Room room) {
        Room existing = roomMapper.findById(room.getId());
        if (existing == null) {
            throw new RuntimeException("房间不存在");
        }
        
        // 检查房间号是否与其他房间重复
        Room duplicate = roomMapper.findByBuildingAndNumber(
            room.getBuildingId(), room.getRoomNumber());
        if (duplicate != null && !duplicate.getId().equals(room.getId())) {
            throw new RuntimeException("该楼栋已存在相同房间号");
        }
        
        roomMapper.update(room);
    }
    
    @Transactional
    public void delete(Long id) {
        Room room = roomMapper.findById(id);
        if (room == null) {
            throw new RuntimeException("房间不存在");
        }
        int occupancy = room.getOccupancy() != null
                ? room.getOccupancy()
                : (room.getCurrentCount() == null ? 0 : room.getCurrentCount());
        if (occupancy > 0) {
            throw new RuntimeException("房间内有学生入住，无法删除");
        }
        roomMapper.deleteById(id);
    }
    
    public void updateStatus(Long id, Integer status) {
        Room room = roomMapper.findById(id);
        if (room == null) {
            throw new RuntimeException("房间不存在");
        }
        room.setStatus(status);
        room.setIsActive(status);
        roomMapper.update(room);
    }
    
    /**
     * 批量创建房间（根据楼栋配置）
     */
    @Transactional
    public int batchCreate(Long buildingId) {
        Building building = buildingMapper.findById(buildingId);
        if (building == null) {
            throw new RuntimeException("楼栋不存在");
        }
        
        int floors = building.getFloors();
        int roomsPerFloor = building.getRoomsPerFloor();
        int created = 0;
        
        for (int floor = 1; floor <= floors; floor++) {
            for (int room = 1; room <= roomsPerFloor; room++) {
                Room existing = roomMapper.findByBuildingAndNumber(
                    buildingId, String.format("%d%02d", floor, room));
                if (existing == null) {
                    Room newRoom = new Room();
                    newRoom.setBuildingId(buildingId);
                    newRoom.setRoomNumber(String.format("%d%02d", floor, room));
                    newRoom.setFloor(floor);
                    newRoom.setCapacity(4); // 默认4人间
                    newRoom.setCurrentCount(0);
                    newRoom.setStatus(1);
                    roomMapper.insert(newRoom);
                    seedBeds(newRoom);
                    created++;
                }
            }
        }
        
        return created;
    }
}