package com.dormitory.service;

import com.dormitory.mapper.InspectionRecordMapper;
import com.dormitory.mapper.InspectionPlanMapper;
import com.dormitory.model.InspectionRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class InspectionRecordService {

    @Autowired
    private InspectionRecordMapper recordMapper;

    @Autowired
    private InspectionPlanMapper planMapper;

    public InspectionRecord findById(Long id) {
        return recordMapper.findById(id);
    }

    public List<InspectionRecord> findAll() {
        return recordMapper.findAll();
    }

    public List<InspectionRecord> findAll(int page, int size) {
        return recordMapper.findAllPaginated(com.dormitory.utils.Pagination.offset(page, size),
                com.dormitory.utils.Pagination.size(size));
    }

    public int count() {
        return recordMapper.count();
    }

    public List<InspectionRecord> findByPlanId(Long planId) {
        return recordMapper.findByPlanId(planId);
    }

    public List<InspectionRecord> findByRoomId(Long roomId) {
        return recordMapper.findByRoomId(roomId);
    }

    public List<InspectionRecord> findByRectificationStatus(String status) {
        return recordMapper.findByRectificationStatus(status);
    }

    public List<InspectionRecord> findByResult(String result) {
        return recordMapper.findByResult(result);
    }

    public List<InspectionRecord> search(Long planId, Long buildingId, String result,
                                         String rectificationStatus, LocalDate startDate, LocalDate endDate) {
        return recordMapper.search(planId, buildingId, result, rectificationStatus, startDate, endDate);
    }

    @Transactional
    public InspectionRecord create(InspectionRecord record) {
        record.setInspectionTime(LocalDateTime.now());
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());

        // 根据检查结果自动设置整改相关字段
        if ("FAIL".equals(record.getResult())) {
            record.setNeedRectification(true);
            if (record.getRectificationStatus() == null) {
                record.setRectificationStatus("PENDING");
            }
        } else {
            record.setNeedRectification(false);
            record.setRectificationStatus("NONE");
        }

        recordMapper.insert(record);

        // 如果有关联计划，递增已完成房间数
        if (record.getPlanId() != null) {
            planMapper.incrementCompletedRooms(record.getPlanId());
        }

        return record;
    }

    @Transactional
    public InspectionRecord update(InspectionRecord record) {
        record.setUpdateTime(LocalDateTime.now());
        recordMapper.update(record);
        return recordMapper.findById(record.getId());
    }

    @Transactional
    public InspectionRecord submitRectify(Long id, String rectificationPhotos, String rectifyRemark) {
        InspectionRecord record = recordMapper.findById(id);
        if (record == null) {
            throw new RuntimeException("检查记录不存在");
        }
        if (!"PENDING".equals(record.getRectificationStatus())) {
            throw new RuntimeException("只有待整改状态的记录才能提交整改，当前状态: " + record.getRectificationStatus());
        }
        record.setRectificationStatus("COMPLETED");
        record.setRectificationTime(LocalDateTime.now());
        record.setRectificationPhotos(rectificationPhotos);
        record.setRectifyRemark(rectifyRemark);
        record.setUpdateTime(LocalDateTime.now());
        recordMapper.updateRectify(record);
        return recordMapper.findById(id);
    }

    @Transactional
    public InspectionRecord approveRectify(Long id, String verifiedBy) {
        InspectionRecord record = recordMapper.findById(id);
        if (record == null) {
            throw new RuntimeException("检查记录不存在");
        }
        if (!"COMPLETED".equals(record.getRectificationStatus())) {
            throw new RuntimeException("只有已整改状态的记录才能审核，当前状态: " + record.getRectificationStatus());
        }
        recordMapper.approveRectify(id, "VERIFIED", verifiedBy);
        return recordMapper.findById(id);
    }

    @Transactional
    public void delete(Long id) {
        InspectionRecord existing = recordMapper.findById(id);
        recordMapper.delete(id);
        if (existing != null && existing.getPlanId() != null) {
            planMapper.decrementCompletedRooms(existing.getPlanId());
        }
    }
}
