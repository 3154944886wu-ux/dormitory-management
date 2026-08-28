package com.dormitory.controller;

import com.dormitory.mapper.*;
import com.dormitory.model.*;
import com.dormitory.service.ManagerScopeService;
import com.dormitory.utils.AuthRoles;
import com.dormitory.utils.DashboardFees;
import com.dormitory.utils.DashboardOverview;
import com.dormitory.utils.RepairCounts;
import com.dormitory.utils.RoomFill;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class DashboardController {

    private final BuildingMapper buildingMapper;
    private final RoomMapper roomMapper;
    private final StudentMapper studentMapper;
    private final UtilityFeeMapper utilityFeeMapper;
    private final RepairMapper repairMapper;
    private final VisitorMapper visitorMapper;
    private final DormBatchMapper dormBatchMapper;
    private final AllocationResultMapper allocationResultMapper;
    private final ManagerScopeService managerScopeService;
    private final UserMapper userMapper;

    public DashboardController(BuildingMapper buildingMapper,
                              RoomMapper roomMapper,
                              StudentMapper studentMapper,
                              UtilityFeeMapper utilityFeeMapper,
                              RepairMapper repairMapper,
                              VisitorMapper visitorMapper,
                              DormBatchMapper dormBatchMapper,
                              AllocationResultMapper allocationResultMapper,
                              ManagerScopeService managerScopeService,
                              UserMapper userMapper) {
        this.buildingMapper = buildingMapper;
        this.roomMapper = roomMapper;
        this.studentMapper = studentMapper;
        this.utilityFeeMapper = utilityFeeMapper;
        this.repairMapper = repairMapper;
        this.visitorMapper = visitorMapper;
        this.dormBatchMapper = dormBatchMapper;
        this.allocationResultMapper = allocationResultMapper;
        this.managerScopeService = managerScopeService;
        this.userMapper = userMapper;
    }

    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getOverview(Authentication auth) {
        ScopedSnapshot snap = snapshot(auth);
        Map<Long, Integer> occupancy = RoomFill.occupancyByRoom(snap.allResidents);
        Map<String, Object> rooms = RoomFill.summarize(snap.rooms, occupancy);
        int activeVisitors = 0;
        for (Visitor visitor : snap.visitors) {
            if (visitor != null && visitor.getStatus() != null && visitor.getStatus() == 1) {
                activeVisitors++;
            }
        }
        return ok(DashboardOverview.overview(
                snap.buildings.size(),
                rooms,
                RoomFill.residing(snap.students),
                RepairCounts.overview(snap.repairs),
                activeVisitors));
    }

    @GetMapping("/accommodation")
    public ResponseEntity<Map<String, Object>> getAccommodationStats(Authentication auth) {
        ScopedSnapshot snap = snapshot(auth);
        Map<String, Object> rooms = RoomFill.summarize(snap.rooms, RoomFill.occupancyByRoom(snap.allResidents));
        return ok(DashboardOverview.accommodation(snap.buildings.size(), rooms, RoomFill.residing(snap.students)));
    }

    @GetMapping("/repair")
    public ResponseEntity<Map<String, Object>> getRepairStats(Authentication auth) {
        ScopedSnapshot snap = snapshot(auth);
        Map<String, Object> data = RepairCounts.panel(snap.repairs);
        List<Repair> recent = snap.repairs.size() > 5 ? snap.repairs.subList(0, 5) : snap.repairs;
        data.put("recentRepairs", recent);
        return ok(data);
    }

    @GetMapping("/utility")
    public ResponseEntity<Map<String, Object>> getUtilityStats(Authentication auth) {
        ScopedSnapshot snap = snapshot(auth);
        List<UtilityFee> fees = snap.fees;
        Map<String, Object> data = new HashMap<>();
        data.put("total", fees.size());
        long unpaid = fees.stream().filter(f -> f.getStatus() != null && f.getStatus() == 0).count();
        long paid = fees.stream().filter(f -> f.getStatus() != null && f.getStatus() == 1).count();
        data.put("unpaid", unpaid);
        data.put("paid", paid);
        java.math.BigDecimal totalAmount = fees.stream()
                .map(f -> f.getTotalFee() != null ? f.getTotalFee() : java.math.BigDecimal.ZERO)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        java.math.BigDecimal paidAmount = fees.stream()
                .filter(f -> f.getStatus() != null && f.getStatus() == 1)
                .map(f -> f.getTotalFee() != null ? f.getTotalFee() : java.math.BigDecimal.ZERO)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        data.put("totalAmount", totalAmount);
        data.put("paidAmount", paidAmount);
        data.put("unpaidAmount", totalAmount.subtract(paidAmount));
        data.putAll(DashboardFees.summarize(fees));
        data.put("recentFees", fees.size() > 5 ? fees.subList(0, 5) : fees);
        return ok(data);
    }

    @GetMapping("/dorm-stats")
    public ResponseEntity<Map<String, Object>> getDormStats(Authentication auth) {
        ScopedSnapshot snap = snapshot(auth);
        return ok(DashboardOverview.dormStats(snap.allocations, dormBatchMapper.countActive()));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats(Authentication auth) {
        return getOverview(auth);
    }

    private ScopedSnapshot snapshot(Authentication auth) {
        Long managerId = managerUserId(auth);
        List<Building> buildings = buildingMapper.findAll();
        List<Room> rooms = roomMapper.findAll();
        List<Student> students = studentMapper.findAll();
        List<Repair> repairs = repairMapper.findAll();
        List<Visitor> visitors = visitorMapper.findAll();
        List<UtilityFee> fees = utilityFeeMapper.findAll();
        List<AllocationResult> allocations = allocationResultMapper.findAll();
        if (managerId == null) {
            return new ScopedSnapshot(buildings, rooms, students, students, repairs, visitors, fees, allocations);
        }
        if (!managerScopeService.hasScope(managerId)) {
            return ScopedSnapshot.empty();
        }
        buildings = managerScopeService.filterVisibleByBuilding(managerId, buildings, Building::getId);
        rooms = managerScopeService.filterVisibleByBuilding(managerId, rooms, Room::getBuildingId);
        List<Student> visibleStudents = managerScopeService.filterVisible(
                managerId, students, Student::getBuildingId, Student::getClassName);
        repairs = managerScopeService.filterVisible(managerId, repairs, Repair::getBuildingId, Repair::getClassName);
        visitors = managerScopeService.filterVisible(managerId, visitors, Visitor::getBuildingId, v -> null);
        fees = managerScopeService.filterVisibleByBuilding(managerId, fees, UtilityFee::getBuildingId);
        allocations = managerScopeService.filterVisible(
                managerId, allocations, AllocationResult::getBuildingId, AllocationResult::getClassName);
        java.util.Set<Long> visibleRoomIds = rooms.stream()
                .map(Room::getId)
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toSet());
        List<Student> residentsInVisibleRooms = students.stream()
                .filter(s -> s.getRoomId() != null && visibleRoomIds.contains(s.getRoomId()))
                .toList();
        return new ScopedSnapshot(buildings, rooms, visibleStudents, residentsInVisibleRooms,
                repairs, visitors, fees, allocations);
    }

    private Long managerUserId(Authentication auth) {
        if (!AuthRoles.isManagerOnly(auth)) {
            return null;
        }
        User user = userMapper.findByUsername(auth.getName());
        return user == null ? null : user.getId();
    }

    private ResponseEntity<Map<String, Object>> ok(Map<String, Object> data) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", data);
        return ResponseEntity.ok(result);
    }

    private record ScopedSnapshot(List<Building> buildings,
                                  List<Room> rooms,
                                  List<Student> students,
                                  List<Student> allResidents,
                                  List<Repair> repairs,
                                  List<Visitor> visitors,
                                  List<UtilityFee> fees,
                                  List<AllocationResult> allocations) {
        static ScopedSnapshot empty() {
            return new ScopedSnapshot(List.of(), List.of(), List.of(), List.of(),
                    List.of(), List.of(), List.of(), List.of());
        }
    }
}
