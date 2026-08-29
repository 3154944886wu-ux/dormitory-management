package com.dormitory.service;

import com.dormitory.mapper.InspectionItemMapper;
import com.dormitory.model.InspectionItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InspectionItemService {

    @Autowired
    private InspectionItemMapper itemMapper;

    public InspectionItem findById(Long id) {
        return itemMapper.findById(id);
    }

    public List<InspectionItem> findAll() {
        return itemMapper.findAll();
    }

    public List<InspectionItem> findAll(int page, int size) {
        return itemMapper.findAllPaginated(com.dormitory.utils.Pagination.offset(page, size),
                com.dormitory.utils.Pagination.size(size));
    }

    public int count() {
        return itemMapper.count();
    }

    public List<InspectionItem> findAllActive() {
        return itemMapper.findAllActive();
    }

    public List<InspectionItem> findByCategory(String category) {
        return itemMapper.findByCategory(category);
    }

    @Transactional
    public InspectionItem create(InspectionItem item) {
        if (item.getStatus() == null) {
            item.setStatus(1); // 默认启用
        }
        itemMapper.insert(item);
        return item;
    }

    @Transactional
    public InspectionItem update(InspectionItem item) {
        itemMapper.update(item);
        return itemMapper.findById(item.getId());
    }

    @Transactional
    public void delete(Long id) {
        itemMapper.delete(id);
    }
}
