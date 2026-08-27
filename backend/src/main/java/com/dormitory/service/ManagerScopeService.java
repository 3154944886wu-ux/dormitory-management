package com.dormitory.service;

import com.dormitory.mapper.ManagerScopeMapper;
import com.dormitory.model.ManagerScope;
import com.dormitory.utils.ManagerScopeJson;
import com.dormitory.utils.ManagerScopeMatcher;
import com.dormitory.utils.ScopeLists;
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

    public boolean hasScope(Long userId) {
        return !findActiveByUserId(userId).isEmpty();
    }
}
