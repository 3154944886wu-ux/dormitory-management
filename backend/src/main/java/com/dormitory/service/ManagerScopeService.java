package com.dormitory.service;

import com.dormitory.mapper.ManagerScopeMapper;
import com.dormitory.mapper.RoomMapper;
import com.dormitory.mapper.StudentMapper;
import com.dormitory.model.ManagerScope;
import com.dormitory.model.Room;
import com.dormitory.model.Student;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public String classNamesCsv(Long userId) {
        String csv = findActiveByUserId(userId).stream()
                .map(ManagerScope::getClassName)
                .filter(v -> v != null && !v.isBlank())
                .distinct()
                .collect(Collectors.joining(","));
        return csv.isBlank() ? null : csv;
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
        Long buildingId = null;
        if (student.getRoomId() != null) {
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
