package com.dormitory.service;

import com.dormitory.mapper.ManagerScopeMapper;
import com.dormitory.mapper.RoomMapper;
import com.dormitory.mapper.StudentMapper;
import com.dormitory.model.ManagerScope;
import com.dormitory.model.Room;
import com.dormitory.model.Student;
import com.dormitory.utils.ManagerScopeJson;
import com.dormitory.utils.ManagerScopeMatcher;
import com.dormitory.utils.ScopeLists;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ManagerScopeService {

    private final ManagerScopeMapper managerScopeMapper;
    private final StudentMapper studentMapper;
    private final RoomMapper roomMapper;

    public ManagerScopeService(ManagerScopeMapper managerScopeMapper,
                               StudentMapper studentMapper,
                               RoomMapper roomMapper) {
        this.managerScopeMapper = managerScopeMapper;
        this.studentMapper = studentMapper;
        this.roomMapper = roomMapper;
    }

    public List<ManagerScope> findAllActive() {
        return managerScopeMapper.findAllActive();
    }

    public List<ManagerScope> findActiveByUserId(Long userId) {
        return managerScopeMapper.findActiveByUserId(userId);
    }

    @Transactional
    public ManagerScope save(ManagerScope scope) {
        if (scope.getStatus() == null) {
            scope.setStatus(1);
        }
        if (scope.getId() == null) {
            managerScopeMapper.insert(scope);
        } else {
            managerScopeMapper.update(scope);
        }
        return managerScopeMapper.findById(scope.getId());
    }

    @Transactional
    public void disable(Long id) {
        managerScopeMapper.disable(id);
    }

    public String buildingIdsCsv(Long userId) {
        String csv = findActiveByUserId(userId).stream()
                .map(ManagerScope::getBuildingId)
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .distinct()
                .collect(Collectors.joining(","));
        return csv.isBlank() ? null : csv;
    }

    public String classNamesJson(Long userId) {
        String json = ScopeLists.toJsonArray(findActiveByUserId(userId).stream()
                .map(ManagerScope::getClassName)
                .filter(v -> v != null && !v.isBlank())
                .distinct()
                .toList());
        return json;
    }

    public String scopesJson(Long userId) {
        return ManagerScopeJson.encode(findActiveByUserId(userId));
    }

    public boolean canSee(Long userId, Long buildingId, String className) {
        return ManagerScopeMatcher.isVisible(findActiveByUserId(userId), buildingId, className);
    }

    public boolean canSeeBuilding(Long userId, Long buildingId) {
        return ManagerScopeMatcher.isBuildingVisible(findActiveByUserId(userId), buildingId);
    }

    public List<String> occupantClassNames(Long roomId) {
        if (roomId == null) {
            return List.of();
        }
        List<String> classes = new ArrayList<>();
        for (Student student : studentMapper.findByRoomId(roomId)) {
            if (student.getStatus() != null && student.getStatus() == 1) {
                classes.add(student.getClassName());
            }
        }
        return classes;
    }

    public boolean canSeeRoom(Long userId, Long buildingId, Long roomId) {
        return ManagerScopeMatcher.isRoomVisible(
                findActiveByUserId(userId), buildingId, occupantClassNames(roomId));
    }

    public <T> java.util.List<T> filterVisibleByRoom(Long userId, java.util.List<T> items,
                                                     java.util.function.Function<T, Long> buildingId,
                                                     java.util.function.Function<T, Long> roomId) {
        if (items == null || items.isEmpty()) {
            return java.util.List.of();
        }
        java.util.List<ManagerScope> scopes = findActiveByUserId(userId);
        java.util.List<T> visible = new java.util.ArrayList<>();
        for (T item : items) {
            if (ManagerScopeMatcher.isRoomVisible(
                    scopes, buildingId.apply(item), occupantClassNames(roomId.apply(item)))) {
                visible.add(item);
            }
        }
        return visible;
    }

    public <T> java.util.List<T> filterVisible(Long userId, java.util.List<T> items,
                                               java.util.function.Function<T, Long> buildingId,
                                               java.util.function.Function<T, String> className) {
        if (items == null || items.isEmpty()) {
            return java.util.List.of();
        }
        java.util.List<ManagerScope> scopes = findActiveByUserId(userId);
        java.util.List<T> visible = new java.util.ArrayList<>();
        for (T item : items) {
            if (ManagerScopeMatcher.isVisible(scopes, buildingId.apply(item), className.apply(item))) {
                visible.add(item);
            }
        }
        return visible;
    }

    public <T> java.util.List<T> filterVisibleByBuilding(Long userId, java.util.List<T> items,
                                                         java.util.function.Function<T, Long> buildingId) {
        if (items == null || items.isEmpty()) {
            return java.util.List.of();
        }
        java.util.List<ManagerScope> scopes = findActiveByUserId(userId);
        java.util.List<T> visible = new java.util.ArrayList<>();
        for (T item : items) {
            if (ManagerScopeMatcher.isBuildingVisible(scopes, buildingId.apply(item))) {
                visible.add(item);
            }
        }
        return visible;
    }

    public boolean hasScope(Long userId) {
        return !findActiveByUserId(userId).isEmpty();
    }

    public boolean matches(List<ManagerScope> scopes, Long buildingId, String className) {
        if (scopes == null || scopes.isEmpty()) {
            return false;
        }
        for (ManagerScope scope : scopes) {
            if (matchesRow(scope, buildingId, className)) {
                return true;
            }
        }
        return false;
    }

    public void assertStudentInScope(String role, Long userId, Long studentId) {
        if (isAdmin(role)) {
            return;
        }
        if (!isManager(role) || userId == null || studentId == null) {
            throw new AccessDeniedException("无权访问该学生数据");
        }
        Student student = studentMapper.findById(studentId);
        if (student == null) {
            throw new AccessDeniedException("无权访问该学生数据");
        }
        Long buildingId = student.getBuildingId();
        if (buildingId == null && student.getRoomId() != null) {
            Room room = roomMapper.findById(student.getRoomId());
            if (room != null) {
                buildingId = room.getBuildingId();
            }
        }
        if (!matches(findActiveByUserId(userId), buildingId, student.getClassName())) {
            throw new AccessDeniedException("无权访问该学生数据");
        }
    }

    private boolean matchesRow(ManagerScope scope, Long buildingId, String className) {
        if (scope == null) {
            return false;
        }
        if (scope.getStatus() != null && scope.getStatus() != 1) {
            return false;
        }
        Long scopeBuilding = scope.getBuildingId();
        boolean buildingOk = scopeBuilding == null || scopeBuilding.equals(buildingId);
        String scopeClass = scope.getClassName();
        boolean classOk = scopeClass == null || scopeClass.isBlank()
                || scopeClass.equals(className);
        return buildingOk && classOk;
    }

    private boolean isAdmin(String role) {
        return "ADMIN".equalsIgnoreCase(normalizeRole(role));
    }

    private boolean isManager(String role) {
        return "MANAGER".equalsIgnoreCase(normalizeRole(role));
    }

    private String normalizeRole(String role) {
        if (role == null || role.isBlank()) {
            return "";
        }
        String normalized = role.trim().toUpperCase();
        if (normalized.startsWith("ROLE_")) {
            normalized = normalized.substring(5);
        }
        return normalized;
    }
}
