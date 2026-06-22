package com.dormitory.service;

import com.dormitory.mapper.CheckRuleMapper;
import com.dormitory.model.CheckRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
public class CheckRuleService {

    @Autowired
    private CheckRuleMapper checkRuleMapper;

    public CheckRule findById(Long id) {
        return checkRuleMapper.findById(id);
    }

    public List<CheckRule> findAll() {
        return checkRuleMapper.findAll();
    }

    public List<CheckRule> findActive() {
        return checkRuleMapper.findActive();
    }

    public CheckRule findByBuildingId(Long buildingId) {
        return checkRuleMapper.findByBuildingId(buildingId);
    }

    public CheckRule findDefault() {
        return checkRuleMapper.findDefault();
    }

    @Transactional
    public CheckRule create(CheckRule rule) {
        // 如果设置为默认规则，先清除其他默认
        if (rule.getIsDefault() != null && rule.getIsDefault() == 1) {
            checkRuleMapper.clearDefault();
        }
        
        fillDefaults(rule);
        rule.setLateThreshold(null);
        validateTimeOrder(rule);
        
        checkRuleMapper.insert(rule);
        return checkRuleMapper.findById(rule.getId());
    }

    @Transactional
    public CheckRule update(CheckRule rule) {
        CheckRule existing = checkRuleMapper.findById(rule.getId());
        if (existing == null) {
            throw new RuntimeException("规则不存在");
        }
        
        // 如果设置为默认规则，先清除其他默认
        if (rule.getIsDefault() != null && rule.getIsDefault() == 1) {
            checkRuleMapper.clearDefault();
        }
        
        fillDefaults(rule);
        rule.setLateThreshold(null);
        validateTimeOrder(rule);
        checkRuleMapper.update(rule);
        return checkRuleMapper.findById(rule.getId());
    }

    @Transactional
    public void delete(Long id) {
        checkRuleMapper.delete(id);
    }

    @Transactional
    public CheckRule setDefault(Long id) {
        CheckRule rule = checkRuleMapper.findById(id);
        if (rule == null) {
            throw new RuntimeException("规则不存在");
        }
        
        checkRuleMapper.clearDefault();
        rule.setIsDefault(1);
        checkRuleMapper.update(rule);
        
        return checkRuleMapper.findById(id);
    }

    private void fillDefaults(CheckRule rule) {
        if (rule.getStatus() == null) {
            rule.setStatus(1);
        }
        if (rule.getIsDefault() == null) {
            rule.setIsDefault(0);
        }
        if (rule.getApplyDays() == null || rule.getApplyDays().isBlank()) {
            rule.setApplyDays("1,2,3,4,5");
        }
        if (rule.getAllowedRadius() == null) {
            rule.setAllowedRadius(500);
        }
        if (rule.getRequireLocation() == null) {
            rule.setRequireLocation(1);
        }
        if (rule.getMaxLocationAccuracy() == null) {
            rule.setMaxLocationAccuracy(200);
        }
        if (rule.getExceptionThreshold() == null) {
            rule.setExceptionThreshold(3);
        }
        if (rule.getAbsentDeadline() == null) {
            if (rule.getCheckEndTime() != null) {
                rule.setAbsentDeadline(rule.getCheckEndTime().plusMinutes(30));
            } else {
                rule.setAbsentDeadline(LocalTime.MIDNIGHT);
            }
        }
    }

    @Transactional
    public CheckRule updateStatus(Long id, Integer status) {
        CheckRule rule = checkRuleMapper.findById(id);
        if (rule == null) {
            throw new RuntimeException("规则不存在");
        }
        if (status == null || (status != 0 && status != 1)) {
            throw new RuntimeException("无效的状态值");
        }
        rule.setStatus(status);
        checkRuleMapper.update(rule);
        return checkRuleMapper.findById(id);
    }

    /**
     * 校验时间链：归寝开始 < 归寝结束 < 未归截止（支持未归截止跨午夜，如 00:00）
     */
    private void validateTimeOrder(CheckRule rule) {
        LocalTime start = rule.getCheckStartTime();
        LocalTime end = rule.getCheckEndTime();
        LocalTime absent = rule.getAbsentDeadline();

        if (start == null || end == null || absent == null) {
            throw new RuntimeException("请完整填写归寝开始、归寝结束和未归截止时间");
        }

        int startMin = toWindowMinutes(start, start);
        int endMin = toWindowMinutes(end, start);
        int absentMin = toWindowMinutes(absent, start);

        if (!(startMin < endMin && endMin < absentMin)) {
            throw new RuntimeException("时间规则必须满足：归寝开始 < 归寝结束 < 未归截止");
        }
    }

    private int toWindowMinutes(LocalTime time, LocalTime windowStart) {
        int minutes = time.toSecondOfDay() / 60;
        int anchor = windowStart.toSecondOfDay() / 60;
        if (minutes <= anchor && !time.equals(windowStart)) {
            minutes += 24 * 60;
        }
        return minutes;
    }
}