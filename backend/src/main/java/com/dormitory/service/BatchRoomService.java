package com.dormitory.service;

import com.dormitory.mapper.BatchRoomMapper;
import com.dormitory.mapper.DormBatchMapper;
import com.dormitory.mapper.RoomMapper;
import com.dormitory.model.BatchRoom;
import com.dormitory.model.DormBatch;
import com.dormitory.model.Room;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BatchRoomService {

    private final BatchRoomMapper batchRoomMapper;
    private final DormBatchMapper batchMapper;
    private final RoomMapper roomMapper;

    public BatchRoomService(BatchRoomMapper batchRoomMapper, DormBatchMapper batchMapper,
                            RoomMapper roomMapper) {
        this.batchRoomMapper = batchRoomMapper;
        this.batchMapper = batchMapper;
        this.roomMapper = roomMapper;
    }

    public List<BatchRoom> findByBatchId(Long batchId) {
        return batchRoomMapper.findByBatchId(batchId);
    }

    public List<BatchRoom> findAll() {
        return batchRoomMapper.findAll();
    }

    /**
     * 查询批次尚未划拨的可用房间
     */
    public List<Room> findAvailableRooms(Long batchId, Long buildingId, Integer floor) {
        DormBatch batch = batchMapper.findById(batchId);
        if (batch == null) {
            throw new RuntimeException("批次不存在");
        }

        Set<Long> assignedRoomIds = batchRoomMapper.findByBatchId(batchId).stream()
                .map(BatchRoom::getRoomId)
                .collect(Collectors.toSet());

        List<Room> candidates;
        if (buildingId != null) {
            candidates = roomMapper.findByBuildingId(buildingId);
        } else {
            candidates = roomMapper.findAll();
        }

        return candidates.stream()
                .filter(r -> r.getIsActive() == 1)
                .filter(r -> r.getStatus() == 1)
                .filter(r -> !assignedRoomIds.contains(r.getId()))
                .filter(r -> !r.isFull())
                .filter(r -> floor == null || r.getFloor().equals(floor))
                .collect(Collectors.toList());
    }

    /**
     * 批量添加房间到批次房源池，失败项单独记录
     */
    @Transactional
    public Map<String, Object> addRooms(Long batchId, List<Long> roomIds) {
        DormBatch batch = batchMapper.findById(batchId);
        if (batch == null) {
            throw new RuntimeException("批次不存在");
        }
        if (!"pending".equals(batch.getMatchStatus())) {
            throw new RuntimeException("只有pending状态的批次才能划拨房源，当前状态: " + batch.getMatchStatus());
        }

        Set<Long> assignedRoomIds = batchRoomMapper.findByBatchId(batchId).stream()
                .map(BatchRoom::getRoomId)
                .collect(Collectors.toSet());

        List<BatchRoom> successList = new ArrayList<>();
        Map<Long, String> failureMap = new LinkedHashMap<>();

        for (Long roomId : roomIds) {
            String failReason = validateSingleRoom(batchId, roomId, assignedRoomIds);
            if (failReason != null) {
                failureMap.put(roomId, failReason);
            } else {
                BatchRoom br = new BatchRoom();
                br.setBatchId(batchId);
                br.setRoomId(roomId);
                batchRoomMapper.insert(br);
                successList.add(br);
                assignedRoomIds.add(roomId);
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("added", successList.size());
        result.put("failed", failureMap.size());
        result.put("failureDetails", failureMap);
        return result;
    }

    /**
     * 批量移除房源
     */
    @Transactional
    public void removeRooms(Long batchId, List<Long> roomIds) {
        DormBatch batch = batchMapper.findById(batchId);
        if (batch == null) {
            throw new RuntimeException("批次不存在");
        }
        if (!"pending".equals(batch.getMatchStatus())) {
            throw new RuntimeException("只有pending状态的批次才能移除房源，当前状态: " + batch.getMatchStatus());
        }

        for (Long roomId : roomIds) {
            batchRoomMapper.deleteByBatchAndRoom(batchId, roomId);
        }
    }

    /**
     * 单房间添加
     */
    @Transactional
    public BatchRoom addRoom(Long batchId, Long roomId) {
        DormBatch batch = batchMapper.findById(batchId);
        if (batch == null) {
            throw new RuntimeException("批次不存在");
        }

        Set<Long> assignedRoomIds = batchRoomMapper.findByBatchId(batchId).stream()
                .map(BatchRoom::getRoomId)
                .collect(Collectors.toSet());

        String failReason = validateSingleRoom(batchId, roomId, assignedRoomIds);
        if (failReason != null) {
            throw new RuntimeException(failReason);
        }

        BatchRoom br = new BatchRoom();
        br.setBatchId(batchId);
        br.setRoomId(roomId);
        batchRoomMapper.insert(br);
        return br;
    }

    /**
     * 单条移除
     */
    @Transactional
    public void removeRoom(Long batchId, Long roomId) {
        batchRoomMapper.deleteByBatchAndRoom(batchId, roomId);
    }

    private String validateSingleRoom(Long batchId, Long roomId, Set<Long> assignedRoomIds) {
        Room room = roomMapper.findById(roomId);
        if (room == null) {
            return "房间 " + roomId + " 不存在";
        }
        if (room.getIsActive() != 1 || room.getStatus() != 1) {
            return "房间 " + room.getRoomNumber() + " 已停用";
        }
        if (assignedRoomIds.contains(roomId)) {
            return "房间 " + room.getRoomNumber() + " 已在该批次中";
        }
        if (room.isFull()) {
            return "房间 " + room.getRoomNumber() + " 已满员，不能划拨给批次";
        }
        // 检查是否在其他非终态批次中已划拨
        int conflictCount = batchRoomMapper.countByRoomIdAndActiveBatches(roomId);
        if (conflictCount > 0) {
            return "房间 " + room.getRoomNumber() + " 已被其他进行中的批次占用";
        }
        return null;
    }
}
