package com.dormitory.service;

import com.dormitory.mapper.*;
import com.dormitory.model.*;
import com.dormitory.utils.GenderMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class RelocationService {

    private final RelocationApplicationMapper relocationAppMapper;
    private final StudentMapper studentMapper;
    private final DormBatchMapper dormBatchMapper;
    private final RoomMapper roomMapper;
    private final BedMapper bedMapper;
    private final BuildingMapper buildingMapper;
    private final AllocationResultMapper allocationResultMapper;
    private final OperationLogMapper operationLogMapper;
    private final UserMapper userMapper;
    private final RoommateGroupMapper roommateGroupMapper;
    private final NotificationService notificationService;

    public RelocationService(RelocationApplicationMapper relocationAppMapper,
                             StudentMapper studentMapper,
                             DormBatchMapper dormBatchMapper,
                             RoomMapper roomMapper,
                             BedMapper bedMapper,
                             BuildingMapper buildingMapper,
                             AllocationResultMapper allocationResultMapper,
                             OperationLogMapper operationLogMapper,
                             UserMapper userMapper,
                             RoommateGroupMapper roommateGroupMapper,
                             NotificationService notificationService) {
        this.relocationAppMapper = relocationAppMapper;
        this.studentMapper = studentMapper;
        this.dormBatchMapper = dormBatchMapper;
        this.roomMapper = roomMapper;
        this.bedMapper = bedMapper;
        this.buildingMapper = buildingMapper;
        this.allocationResultMapper = allocationResultMapper;
        this.operationLogMapper = operationLogMapper;
        this.userMapper = userMapper;
        this.roommateGroupMapper = roommateGroupMapper;
        this.notificationService = notificationService;
    }

    /** 学生提交调换申请 */
    @Transactional
    public RelocationApplication apply(Long studentId, String reason, Long preferredBuildingId) {
        Student student = studentMapper.findById(studentId);
        if (student == null) throw new RuntimeException("学生不存在");
        if (student.getStatus() != 1) throw new RuntimeException("只有已入住的学生才能申请调换");
        if (student.getDormBatchId() == null) throw new RuntimeException("你未参与选宿批次");

        DormBatch batch = dormBatchMapper.findById(student.getDormBatchId());
        if (batch == null) throw new RuntimeException("批次不存在");

        // 检查是否有进行中的申请
        int activeCount = relocationAppMapper.countPendingOrApproved(studentId);
        if (activeCount > 0) throw new RuntimeException("你已有进行中的调换申请，请等待处理完成");

        // 检查一年一次限制
        int thisYear = LocalDateTime.now().getYear();
        int yearlyCount = relocationAppMapper.countApprovedOrExecutedInYear(studentId, thisYear);
        if (yearlyCount >= 1) throw new RuntimeException("本年度调换次数已用完（每年限1次）");

        RelocationApplication app = new RelocationApplication();
        app.setStudentId(studentId);
        app.setBatchId(student.getDormBatchId());
        app.setCurrentRoomId(student.getRoomId());
        // find current bed id
        if (student.getRoomId() != null && student.getBedNumber() != null) {
            List<Bed> beds = bedMapper.findByRoomId(student.getRoomId());
            for (Bed b : beds) {
                if (student.getBedNumber().equals(b.getBedNumber())) {
                    app.setCurrentBedId(b.getId());
                    break;
                }
            }
        }
        app.setReason(reason);
        app.setPreferredBuildingId(preferredBuildingId);
        app.setStatus("pending");
        relocationAppMapper.insert(app);

        writeLog(studentId, "student", student.getStudentNo(),
                "申请调换", "{\"reason\":\"" + reason + "\"}");

        return relocationAppMapper.findById(app.getId());
    }

    /** 管理员审批通过 */
    @Transactional
    public RelocationApplication approve(Long applicationId, Long adminUserId, String comment) {
        RelocationApplication app = relocationAppMapper.findById(applicationId);
        if (app == null) throw new RuntimeException("申请不存在");
        if (!"pending".equals(app.getStatus())) throw new RuntimeException("只有待审核的申请才能审批");

        app.setStatus("approved");
        app.setReviewedBy(adminUserId);
        app.setReviewComment(comment);
        relocationAppMapper.update(app);

        writeLog(app.getStudentId(), "admin", String.valueOf(adminUserId),
                "审批通过调换申请", "{\"applicationId\":" + applicationId + "}");

        notificationService.createNotification(app.getStudentId(), app.getBatchId(),
                "relocation_approved", "你的调换申请已审批通过，等待执行");

        return relocationAppMapper.findById(applicationId);
    }

    /** 管理员拒绝 */
    @Transactional
    public RelocationApplication reject(Long applicationId, Long adminUserId, String comment) {
        RelocationApplication app = relocationAppMapper.findById(applicationId);
        if (app == null) throw new RuntimeException("申请不存在");
        if (!"pending".equals(app.getStatus()) && !"approved".equals(app.getStatus())) {
            throw new RuntimeException("只有待审核或已通过的申请才能拒绝");
        }

        app.setStatus("rejected");
        app.setReviewedBy(adminUserId);
        app.setReviewComment(comment);
        relocationAppMapper.update(app);

        writeLog(app.getStudentId(), "admin", String.valueOf(adminUserId),
                "拒绝调换申请", "{\"applicationId\":" + applicationId + "}");

        notificationService.createNotification(app.getStudentId(), app.getBatchId(),
                "relocation_rejected", "你的调换申请已被驳回" + (comment != null ? "：" + comment : ""));

        return relocationAppMapper.findById(applicationId);
    }

    /** 管理员执行调换 */
    @Transactional
    public RelocationApplication execute(Long applicationId, Long adminUserId, Long newRoomId, Long newBedId) {
        RelocationApplication app = relocationAppMapper.findById(applicationId);
        if (app == null) throw new RuntimeException("申请不存在");
        if (!"approved".equals(app.getStatus())) throw new RuntimeException("只有已审批通过的申请才能执行");

        Student student = studentMapper.findById(app.getStudentId());
        if (student == null) throw new RuntimeException("学生不存在");
        if (student.getStatus() == null || student.getStatus() != 1) {
            throw new RuntimeException("学生已退宿，无法执行调宿");
        }

        Room newRoom = roomMapper.findById(newRoomId);
        if (newRoom == null) throw new RuntimeException("目标房间不存在");
        if (newRoom.getIsActive() != 1) throw new RuntimeException("目标房间已停用");
        boolean sameRoom = student.getRoomId() != null && student.getRoomId().equals(newRoomId);
        int live = studentMapper.countByRoomId(newRoomId);
        if (!sameRoom && live >= newRoom.getCapacity()) {
            throw new RuntimeException("目标房间已满");
        }

        Building building = buildingMapper.findById(newRoom.getBuildingId());
        if (!GenderMatcher.isCompatible(student.getGender(), building.getGenderType()))
            throw new RuntimeException("学生性别与目标楼栋类型不匹配");

        Bed newBed = bedMapper.findById(newBedId);
        if (newBed == null || !newBed.getRoomId().equals(newRoomId))
            throw new RuntimeException("床位不存在或不属于目标房间");
        if (newBed.getIsOccupied() == 1)
            throw new RuntimeException("该床位已被占用");

        // 释放旧资源（按学生当前床位，而不是申请快照，避免期间已换床）
        if (sameRoom) {
            if (student.getBedNumber() != null) {
                List<Bed> beds = bedMapper.findByRoomId(student.getRoomId());
                for (Bed b : beds) {
                    if (student.getBedNumber().equals(b.getBedNumber()) && !b.getId().equals(newBedId)) {
                        bedMapper.updateOccupied(b.getId(), 0);
                        break;
                    }
                }
            }
        } else {
            releaseOldResources(student);
        }

        // 占用新资源：先条件占床，防止并发下重复占用同一床位
        int occupied = bedMapper.tryOccupy(newBedId);
        if (occupied == 0) {
            throw new RuntimeException("该床位已被占用");
        }
        if (!sameRoom) {
            int inc = roomMapper.incrementCount(newRoomId);
            if (inc == 0) {
                throw new RuntimeException("目标房间已满，无法调宿");
            }
        }

        // 更新学生
        Long oldRoomId = student.getRoomId();
        student.setRoomId(newRoomId);
        student.setBedNumber(newBed.getBedNumber());
        studentMapper.update(student);
        roomMapper.setCurrentCount(newRoomId, studentMapper.countByRoomId(newRoomId));
        if (oldRoomId != null && !oldRoomId.equals(newRoomId)) {
            roomMapper.setCurrentCount(oldRoomId, studentMapper.countByRoomId(oldRoomId));
        }

        // 更新 AllocationResult：原地更新为 manual_assigned，避免违反 (student_id, batch_id) 唯一约束
        AllocationResult result = allocationResultMapper
                .findByStudentIdAndBatchId(student.getId(), app.getBatchId());
        if (result != null) {
            // 从旧室友组中移除
            if (result.getRoommateGroupId() != null) {
                RoommateGroup oldGroup = roommateGroupMapper.findById(result.getRoommateGroupId());
                if (oldGroup != null) {
                    List<Long> members = oldGroup.getMemberIdList();
                    members.remove(student.getId());
                    oldGroup.setMemberIdList(members);
                    roommateGroupMapper.update(oldGroup);
                }
            }

            // 加入新房间的室友组（或新建）
            List<RoommateGroup> newRoomGroups = roommateGroupMapper.findByRoomId(newRoomId);
            RoommateGroup targetGroup = null;
            for (RoommateGroup rg : newRoomGroups) {
                if (rg.getMemberIdList() != null && rg.getMemberIdList().size() < 4) {
                    targetGroup = rg;
                    break;
                }
            }
            if (targetGroup != null) {
                List<Long> members = targetGroup.getMemberIdList();
                members.add(student.getId());
                targetGroup.setMemberIdList(members);
                roommateGroupMapper.update(targetGroup);
            } else {
                targetGroup = new RoommateGroup();
                targetGroup.setBatchId(app.getBatchId());
                targetGroup.setRoomId(newRoomId);
                targetGroup.setMemberIdList(new ArrayList<>(List.of(student.getId())));
                roommateGroupMapper.insert(targetGroup);
            }

            // 原地更新分配结果并关联到新室友组
            result.setRoomId(newRoomId);
            result.setBedId(newBedId);
            result.setStatus("manual_assigned");
            result.setRoommateGroupId(targetGroup.getId());
            allocationResultMapper.update(result);
        }

        // 更新申请
        app.setStatus("executed");
        app.setExecutedBy(adminUserId);
        app.setNewRoomId(newRoomId);
        app.setNewBedId(newBedId);
        relocationAppMapper.update(app);

        writeLog(student.getId(), "admin", String.valueOf(adminUserId),
                "执行调换", "{\"applicationId\":" + applicationId
                        + ", \"oldRoomId\":" + (oldRoomId != null ? oldRoomId : "null")
                        + ", \"newRoomId\":" + newRoomId + "}");

        notificationService.createNotification(app.getStudentId(), app.getBatchId(),
                "relocation_executed", "你的调换已执行完成，请查看「我的宿舍」确认新房间信息");

        return relocationAppMapper.findById(applicationId);
    }

    public List<RelocationApplication> findAll() {
        return relocationAppMapper.findAll();
    }

    public List<RelocationApplication> findByStatus(String status) {
        return relocationAppMapper.findByStatus(status);
    }

    public List<RelocationApplication> findByStudentId(Long studentId) {
        return relocationAppMapper.findByStudentId(studentId);
    }

    public RelocationApplication findById(Long id) {
        return relocationAppMapper.findById(id);
    }

    public void cancelActiveByStudent(Long studentId) {
        if (studentId == null) {
            return;
        }
        for (RelocationApplication app : relocationAppMapper.findByStudentId(studentId)) {
            if ("pending".equals(app.getStatus()) || "approved".equals(app.getStatus())) {
                relocationAppMapper.updateStatus(app.getId(), "cancelled");
            }
        }
    }

    private void releaseOldResources(Student student) {
        Long currentBedId = null;
        if (student.getRoomId() != null && student.getBedNumber() != null) {
            List<Bed> beds = bedMapper.findByRoomId(student.getRoomId());
            for (Bed b : beds) {
                if (student.getBedNumber().equals(b.getBedNumber())) {
                    currentBedId = b.getId();
                    break;
                }
            }
        }
        if (student.getRoomId() != null) {
            int dec = roomMapper.decrementCount(student.getRoomId());
            if (dec == 0) {
                roomMapper.setCurrentCount(student.getRoomId(),
                        Math.max(0, studentMapper.countByRoomId(student.getRoomId()) - 1));
            }
        }
        if (currentBedId != null) {
            bedMapper.updateOccupied(currentBedId, 0);
        }
    }

    private void writeLog(Long studentId, String operatorType, String operatorId,
                          String action, String detail) {
        OperationLog log = new OperationLog();
        log.setStudentId(studentId);
        log.setOperatorType(operatorType);
        log.setOperatorId(operatorId);
        log.setAction(action);
        log.setDetail(detail);
        operationLogMapper.insert(log);
    }
}
