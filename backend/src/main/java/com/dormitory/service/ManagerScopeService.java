package com.dormitory.service;

import com.dormitory.mapper.ManagerScopeMapper;
import com.dormitory.model.ManagerScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ManagerScopeService {

    private final ManagerScopeMapper managerScopeMapper;

    public ManagerScopeService(ManagerScopeMapper managerScopeMapper) {
        this.managerScopeMapper = managerScopeMapper;
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
}
