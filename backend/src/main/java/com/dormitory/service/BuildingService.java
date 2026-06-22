package com.dormitory.service;

import com.dormitory.mapper.BuildingMapper;
import com.dormitory.model.Building;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BuildingService {
    
    private final BuildingMapper buildingMapper;
    
    public BuildingService(BuildingMapper buildingMapper) {
        this.buildingMapper = buildingMapper;
    }
    
    public List<Building> findAll() {
        return buildingMapper.findAll();
    }
    
    public Building findById(Long id) {
        return buildingMapper.findById(id);
    }
    
    @Transactional
    public Long create(Building building) {
        // 检查名称是否已存在
        Building existing = buildingMapper.findByName(building.getName());
        if (existing != null) {
            throw new RuntimeException("楼栋名称已存在");
        }

        building.setStatus(1); // 默认启用
        try {
            buildingMapper.insert(building);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("楼栋名称已存在");
        }
        return building.getId();
    }
    
    @Transactional
    public void update(Building building) {
        // 检查名称是否被其他楼栋使用
        int count = buildingMapper.countByNameExclude(building.getName(), building.getId());
        if (count > 0) {
            throw new RuntimeException("楼栋名称已存在");
        }
        
        buildingMapper.update(building);
    }
    
    @Transactional
    public void delete(Long id) {
        buildingMapper.deleteById(id);
    }
    
    public void updateStatus(Long id, Integer status) {
        Building building = buildingMapper.findById(id);
        if (building == null) {
            throw new RuntimeException("楼栋不存在");
        }
        building.setStatus(status);
        buildingMapper.update(building);
    }
}