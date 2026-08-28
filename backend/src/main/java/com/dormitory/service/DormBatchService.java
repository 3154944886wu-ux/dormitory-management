package com.dormitory.service;

import com.dormitory.mapper.*;
import com.dormitory.model.College;
import com.dormitory.model.DormBatch;
import com.dormitory.utils.BatchFinishPolicy;
import com.dormitory.utils.OccupancyRelease;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DormBatchService {

    private final DormBatchMapper batchMapper;
    private final CollegeMapper collegeMapper;
    private final StudentMapper studentMapper;
    private final AllocationResultMapper allocationResultMapper;
    private final StudentAnswerMapper studentAnswerMapper;
    private final BatchRoomMapper batchRoomMapper;
    private final BedMapper bedMapper;
    private final RoomMapper roomMapper;
    private final RoommateGroupMapper roommateGroupMapper;
    private final NotificationMapper notificationMapper;
    private final NotificationService notificationService;
    private final RelocationApplicationMapper relocationApplicationMapper;
    private final MatchingService matchingService;

    public DormBatchService(DormBatchMapper batchMapper, CollegeMapper collegeMapper,
                            StudentMapper studentMapper, AllocationResultMapper allocationResultMapper,
                            StudentAnswerMapper studentAnswerMapper, BatchRoomMapper batchRoomMapper,
                            BedMapper bedMapper, RoomMapper roomMapper,
                            RoommateGroupMapper roommateGroupMapper,
                            NotificationMapper notificationMapper,
                            NotificationService notificationService,
                            RelocationApplicationMapper relocationApplicationMapper,
                            MatchingService matchingService) {
        this.batchMapper = batchMapper;
        this.collegeMapper = collegeMapper;
        this.studentMapper = studentMapper;
        this.allocationResultMapper = allocationResultMapper;
        this.studentAnswerMapper = studentAnswerMapper;
        this.batchRoomMapper = batchRoomMapper;
        this.bedMapper = bedMapper;
        this.roomMapper = roomMapper;
        this.roommateGroupMapper = roommateGroupMapper;
        this.notificationMapper = notificationMapper;
        this.notificationService = notificationService;
        this.relocationApplicationMapper = relocationApplicationMapper;
        this.matchingService = matchingService;
    }

    public List<DormBatch> findAll() {
        List<DormBatch> batches = batchMapper.findAll();
        fillCounts(batches);
        return batches;
    }

    public DormBatch findById(Long id) {
        return batchMapper.findById(id);
    }

    public List<DormBatch> findByCollegeId(Long collegeId) {
        List<DormBatch> batches = batchMapper.findByCollegeId(collegeId);
        fillCounts(batches);
        return batches;
    }

    public List<DormBatch> findByMatchStatus(String matchStatus) {
        List<DormBatch> batches = batchMapper.findByMatchStatus(matchStatus);
        fillCounts(batches);
        return batches;
    }

    private void fillCounts(List<DormBatch> batches) {
        for (DormBatch batch : batches) {
            batch.setRoomCount(batchRoomMapper.countByBatchId(batch.getId()));
            batch.setStudentCount(studentMapper.countByDormBatchId(batch.getId()));
        }
    }

    @Transactional
    public DormBatch create(DormBatch batch) {
        College college = collegeMapper.findById(batch.getCollegeId());
        if (college == null) {
            throw new RuntimeException("学院不存在");
        }

        if (batch.getStartTime() == null || batch.getEndTime() == null) {
            throw new RuntimeException("开始时间和结束时间不能为空");
        }
        if (batch.getConfirmDeadline() == null) {
            throw new RuntimeException("确认截止时间不能为空");
        }
        if (!batch.getEndTime().isAfter(batch.getStartTime())) {
            throw new RuntimeException("结束时间必须在开始时间之后");
        }
        if (!batch.getConfirmDeadline().isAfter(batch.getEndTime())) {
            throw new RuntimeException("确认截止时间必须在结束时间之后");
        }

        if (batch.getMatchStatus() == null) {
            batch.setMatchStatus("pending");
        }
        if (batch.getMaxReallocation() == null) {
            batch.setMaxReallocation(1);
        }
        if (batch.getAllowMixMajor() == null) {
            batch.setAllowMixMajor(0);
        }
        if (batch.getMajorBonus() == null) {
            batch.setMajorBonus(10);
        }
        if (batch.getPreferSameFloor() == null) {
            batch.setPreferSameFloor(1);
        }

        batchMapper.insert(batch);
        return batchMapper.findById(batch.getId());
    }

    @Transactional
    public DormBatch update(Long id, DormBatch batch) {
        DormBatch existing = batchMapper.findById(id);
        if (existing == null) {
            throw new RuntimeException("批次不存在");
        }
        if (!"pending".equals(existing.getMatchStatus())) {
            throw new RuntimeException("只有pending状态的批次才能修改");
        }

        if (batch.getStartTime() != null && batch.getEndTime() != null) {
            if (!batch.getEndTime().isAfter(batch.getStartTime())) {
                throw new RuntimeException("结束时间必须在开始时间之后");
            }
        }

        // 前端只发送了修改的字段，用现有值填充 null 字段，防止覆盖数据库
        if (batch.getName() == null) batch.setName(existing.getName());
        if (batch.getCollegeId() == null) batch.setCollegeId(existing.getCollegeId());
        if (batch.getStartTime() == null) batch.setStartTime(existing.getStartTime());
        if (batch.getEndTime() == null) batch.setEndTime(existing.getEndTime());
        if (batch.getConfirmDeadline() == null) batch.setConfirmDeadline(existing.getConfirmDeadline());
        if (batch.getMaxReallocation() == null) batch.setMaxReallocation(existing.getMaxReallocation());
        if (batch.getAllowMixMajor() == null) batch.setAllowMixMajor(existing.getAllowMixMajor());
        if (batch.getMajorBonus() == null) batch.setMajorBonus(existing.getMajorBonus());
        if (batch.getPreferSameFloor() == null) batch.setPreferSameFloor(existing.getPreferSameFloor());
        if (batch.getMatchStatus() == null) batch.setMatchStatus(existing.getMatchStatus());

        batch.setId(id);
        batchMapper.update(batch);
        return batchMapper.findById(id);
    }

    @Transactional
    public DormBatch startBatch(Long id) {
        DormBatch batch = batchMapper.findById(id);
        if (batch == null) {
            throw new RuntimeException("批次不存在");
        }
        if (!"pending".equals(batch.getMatchStatus())) {
            throw new RuntimeException("只有pending状态的批次才能启动，当前状态: " + batch.getMatchStatus());
        }

        // 检查同学院是否已有运行中的批次
        List<DormBatch> runningBatches = batchMapper.findByCollegeId(batch.getCollegeId()).stream()
                .filter(b -> "running".equals(b.getMatchStatus()) || "confirming".equals(b.getMatchStatus())
                        || "matching".equals(b.getMatchStatus()))
                .toList();
        if (!runningBatches.isEmpty()) {
            throw new RuntimeException("该学院已有进行中的批次（" + runningBatches.get(0).getName() + "），请先结束或重置后再启动新批次");
        }

        // 检查房源池是否为空
        int roomCount = batchRoomMapper.countByBatchId(id);
        if (roomCount == 0) {
            throw new RuntimeException("该批次尚未划拨房源，请先到「房源划拨」页面分配房源后再启动");
        }

        batchMapper.updateStatus(id, "running");

        // 自动关联同学院的所有在住且未分配房间的学生
        studentMapper.updateDormBatchIdByCollege(
                batch.getCollegeId().intValue(),
                batch.getId()
        );

        return batchMapper.findById(id);
    }

    /** 管理员手动截止 → 批次作废 */
    @Transactional
    public DormBatch cancelBatch(Long id) {
        DormBatch batch = batchMapper.findById(id);
        if (batch == null) {
            throw new RuntimeException("批次不存在");
        }
        if (!"running".equals(batch.getMatchStatus())) {
            throw new RuntimeException("只有running状态的批次才能手动截止，当前状态: " + batch.getMatchStatus());
        }
        // 清空该批次学生的问卷答案
        List<com.dormitory.model.Student> batchStudents = studentMapper.findByDormBatchId(batch.getId());
        for (com.dormitory.model.Student s : batchStudents) {
            studentAnswerMapper.deleteByStudentId(s.getId());
        }
        // 清空该批次学生的 dorm_batch_id
        studentMapper.clearDormBatchIdByBatchId(batch.getId());
        batchMapper.updateStatus(id, "cancelled");
        return batchMapper.findById(id);
    }

    /** confirming → finished（手动或自动） */
    @Transactional
    public DormBatch advanceToFinished(Long id) {
        DormBatch batch = batchMapper.findById(id);
        if (batch == null) {
            throw new RuntimeException("批次不存在");
        }
        if (!"confirming".equals(batch.getMatchStatus())) {
            throw new RuntimeException("只有confirming状态的批次才能结束，当前状态: " + batch.getMatchStatus());
        }
        confirmAllRecommendations(batch.getId());
        int remaining = allocationResultMapper.findByBatchIdAndStatus(batch.getId(), "recommended").size();
        if (!BatchFinishPolicy.shouldMarkFinished(remaining)) {
            throw new RuntimeException("仍有 " + remaining + " 条未确认分配，无法结束批次");
        }
        batchMapper.updateStatus(id, "finished");
        return batchMapper.findById(id);
    }

    private void confirmAllRecommendations(Long batchId) {
        List<com.dormitory.model.AllocationResult> unconfirmed =
                allocationResultMapper.findByBatchIdAndStatus(batchId, "recommended");
        for (com.dormitory.model.AllocationResult ar : unconfirmed) {
            // 先增房间人数，再占床位（防止满员后床位幽灵占用）
            com.dormitory.model.Student student = studentMapper.findById(ar.getStudentId());
            if (student != null && OccupancyRelease.needsRoomIncrement(student.getRoomId(), ar.getRoomId())) {
                if (student.getRoomId() != null) {
                    releaseStudentOccupation(student);
                    student.setRoomId(null);
                    student.setBedNumber(null);
                }
            }
            if (OccupancyRelease.needsRoomIncrement(student == null ? null : student.getRoomId(), ar.getRoomId())
                    && ar.getRoomId() != null) {
                int inc = roomMapper.incrementCount(ar.getRoomId());
                if (inc == 0) {
                    System.err.println("警告: 自动确认时房间[" + ar.getRoomId() + "]人数更新失败，跳过学生[" + ar.getStudentId() + "]");
                    continue;
                }
            }
            if (ar.getBedId() != null && (student == null || student.getBedNumber() == null
                    || !OccupancyRelease.stillInAllocatedRoom(student.getRoomId(), ar.getRoomId()))) {
                int rows = bedMapper.tryOccupy(ar.getBedId());
                if (rows == 0) {
                    if (ar.getRoomId() != null && (student == null
                            || OccupancyRelease.needsRoomIncrement(student.getRoomId(), ar.getRoomId()))) {
                        roomMapper.decrementCount(ar.getRoomId());
                    }
                    System.err.println("警告: 自动确认时床位[" + ar.getBedId() + "]已被占用，跳过学生[" + ar.getStudentId() + "]");
                    continue;
                }
            }
            allocationResultMapper.updateStatus(ar.getId(), "auto_confirmed");
            notificationService.createNotification(ar.getStudentId(), batchId,
                    "auto_confirm", "你的选宿分配结果已自动确认，请查看「我的宿舍」");
            if (student != null && ar.getRoomId() != null) {
                student.setRoomId(ar.getRoomId());
                String bedNumber = ar.getBedNumber();
                if (bedNumber == null && ar.getBedId() != null) {
                    com.dormitory.model.Bed bed = bedMapper.findById(ar.getBedId());
                    if (bed != null) {
                        bedNumber = bed.getBedNumber();
                    }
                }
                student.setBedNumber(bedNumber);
                student.setStatus(1);
                student.setCheckInDate(java.time.LocalDateTime.now());
                studentMapper.update(student);
            }
        }
    }

    private void releaseConfirmedAllocation(com.dormitory.model.AllocationResult ar) {
        if (ar == null || "recommended".equals(ar.getStatus())) {
            return;
        }
        com.dormitory.model.Student student = studentMapper.findById(ar.getStudentId());
        if (student == null || !OccupancyRelease.stillInAllocatedRoom(student.getRoomId(), ar.getRoomId())) {
            return;
        }
        if (ar.getBedId() != null) {
            bedMapper.updateOccupied(ar.getBedId(), 0);
        }
        if (ar.getRoomId() != null) {
            roomMapper.decrementCount(ar.getRoomId());
        }
        student.setRoomId(null);
        student.setBedNumber(null);
        studentMapper.update(student);
    }

    private void releaseStudentOccupation(com.dormitory.model.Student student) {
        if (student.getRoomId() == null) {
            return;
        }
        roomMapper.decrementCount(student.getRoomId());
        if (student.getBedNumber() != null) {
            for (com.dormitory.model.Bed bed : bedMapper.findByRoomId(student.getRoomId())) {
                if (student.getBedNumber().equals(bed.getBedNumber())) {
                    bedMapper.updateOccupied(bed.getId(), 0);
                    break;
                }
            }
        }
    }

    @Transactional
    public DormBatch resetBatch(Long id) {
        DormBatch batch = batchMapper.findById(id);
        if (batch == null) {
            throw new RuntimeException("批次不存在");
        }
        if (!"pending".equals(batch.getMatchStatus())
                && !"finished".equals(batch.getMatchStatus())
                && !"cancelled".equals(batch.getMatchStatus())) {
            throw new RuntimeException("只有pending/finished/cancelled状态的批次才能重置，当前状态: " + batch.getMatchStatus());
        }

        // 释放该批次所有分配结果占用的物理资源（仅已确认的，recommended 无物理占用）
        List<com.dormitory.model.AllocationResult> results =
                allocationResultMapper.findByBatchId(batch.getId());
        for (com.dormitory.model.AllocationResult ar : results) {
            releaseConfirmedAllocation(ar);
        }

        // 清空该批次学生的 dorm_batch_id
        studentMapper.clearDormBatchIdByBatchId(batch.getId());
        // 清空该批次学生的问卷答案
        for (com.dormitory.model.AllocationResult ar : results) {
            studentAnswerMapper.deleteByStudentId(ar.getStudentId());
        }
        // 清空该批次的分配结果
        allocationResultMapper.deleteByBatchId(batch.getId());
        // 清空该批次的室友组
        roommateGroupMapper.deleteByBatchId(batch.getId());
        // 清空该批次的通知
        notificationMapper.deleteByBatchId(batch.getId());
        // 清空该批次的调换申请
        relocationApplicationMapper.deleteByBatchId(batch.getId());

        batchMapper.updateStatus(id, "pending");
        return batchMapper.findById(id);
    }

    @Transactional
    public DormBatch archiveBatch(Long id) {
        DormBatch batch = batchMapper.findById(id);
        if (batch == null) {
            throw new RuntimeException("批次不存在");
        }
        if (!"finished".equals(batch.getMatchStatus())) {
            throw new RuntimeException("只有已结束的批次才能归档，当前状态: " + batch.getMatchStatus());
        }
        batchMapper.updateStatus(id, "archived");
        return batchMapper.findById(id);
    }

    @Transactional
    public void delete(Long id) {
        DormBatch batch = batchMapper.findById(id);
        if (batch == null) {
            throw new RuntimeException("批次不存在");
        }
        if ("running".equals(batch.getMatchStatus()) || "confirming".equals(batch.getMatchStatus())
                || "matching".equals(batch.getMatchStatus())) {
            throw new RuntimeException("运行中、匹配中或确认中的批次不能删除");
        }

        // 释放该批次所有分配结果占用的物理资源（仅已确认的，recommended 无物理占用）
        List<com.dormitory.model.AllocationResult> results =
                allocationResultMapper.findByBatchId(batch.getId());
        for (com.dormitory.model.AllocationResult ar : results) {
            releaseConfirmedAllocation(ar);
        }

        // 清空该批次学生的 dorm_batch_id
        studentMapper.clearDormBatchIdByBatchId(batch.getId());
        // 清空学生问卷答案
        for (com.dormitory.model.AllocationResult ar : results) {
            studentAnswerMapper.deleteByStudentId(ar.getStudentId());
        }
        // 清空关联数据
        allocationResultMapper.deleteByBatchId(batch.getId());
        roommateGroupMapper.deleteByBatchId(batch.getId());
        notificationMapper.deleteByBatchId(batch.getId());
        relocationApplicationMapper.deleteByBatchId(batch.getId());
        batchRoomMapper.deleteByBatchId(batch.getId());
        // 删除批次本身
        batchMapper.deleteById(id);
    }

    // ==== 定时任务: 自动流转 ====

    /** 自动: running → 执行匹配 → confirming */
    public void autoTransitionRunningToConfirming() {
        List<DormBatch> batches = batchMapper.findRunningAndPastEndTime();
        for (DormBatch batch : batches) {
            try {
                matchingService.executeMatching(batch.getId());
            } catch (Exception e) {
                // 匹配失败则跳过，保留 running 状态待重试
                System.err.println("批次[" + batch.getId() + "]自动匹配失败: " + e.getMessage());
            }
        }
    }

    /** 自动: confirming → finished */
    @Transactional
    public void autoTransitionConfirmingToFinished() {
        List<DormBatch> batches = batchMapper.findConfirmingAndPastDeadline();
        for (DormBatch batch : batches) {
            confirmAllRecommendations(batch.getId());
            int remaining = allocationResultMapper.findByBatchIdAndStatus(batch.getId(), "recommended").size();
            if (BatchFinishPolicy.shouldMarkFinished(remaining)) {
                batchMapper.updateStatus(batch.getId(), "finished");
            }
        }
    }
}
