package com.dormitory.service;

import com.dormitory.mapper.RoomMapper;
import com.dormitory.mapper.UtilityFeeMapper;
import com.dormitory.model.Room;
import com.dormitory.model.UtilityFee;
import com.dormitory.utils.MeterReading;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UtilityFeeService {
    
    private final UtilityFeeMapper feeMapper;
    private final RoomMapper roomMapper;
    
    // 费率配置（可后续改为数据库配置）
    private static final BigDecimal ELECTRICITY_RATE = new BigDecimal("0.6"); // 每度电0.6元
    private static final BigDecimal WATER_RATE = new BigDecimal("5.0");        // 每吨水5元
    
    public UtilityFeeService(UtilityFeeMapper feeMapper, RoomMapper roomMapper) {
        this.feeMapper = feeMapper;
        this.roomMapper = roomMapper;
    }
    
    public List<UtilityFee> findAll() {
        return feeMapper.findAll();
    }
    
    public List<UtilityFee> findByRoomId(Long roomId) {
        return feeMapper.findByRoomId(roomId);
    }
    
    public List<UtilityFee> findByStatus(Integer status) {
        return feeMapper.findByStatus(status);
    }
    
    public UtilityFee findById(Long id) {
        return feeMapper.findById(id);
    }
    
    public List<UtilityFee> findUnpaid() {
        return feeMapper.findByStatus(0);
    }
    
    @Transactional
    public Long create(UtilityFee fee) {
        // 验证房间存在
        Room room = roomMapper.findById(fee.getRoomId());
        if (room == null) {
            throw new RuntimeException("房间不存在");
        }

        // 检查是否已存在该月份的费用记录
        UtilityFee existing = feeMapper.findByRoomAndMonth(
            fee.getRoomId(), fee.getYear(), fee.getMonth());
        if (existing != null) {
            throw new RuntimeException("该房间当月费用记录已存在");
        }

        // 计算用量
        calculateUsage(fee);

        // 计算费用
        calculateTotalFee(fee);

        fee.setStatus(0); // 未缴费
        feeMapper.insert(fee);
        return fee.getId();
    }

    /**
     * 直接创建费用记录（使用前端传入的金额，不通过读数计算）
     */
    @Transactional
    public Long createDirect(UtilityFee fee) {
        // 验证房间存在
        Room room = roomMapper.findById(fee.getRoomId());
        if (room == null) {
            throw new RuntimeException("房间不存在");
        }

        // 检查是否已存在该月份的费用记录
        UtilityFee existing = feeMapper.findByRoomAndMonth(
            fee.getRoomId(), fee.getYear(), fee.getMonth());
        if (existing != null) {
            throw new RuntimeException("该房间当月费用记录已存在");
        }

        fee.setStatus(fee.getStatus() != null ? fee.getStatus() : 0);
        feeMapper.insert(fee);
        return fee.getId();
    }
    
    @Transactional
    public void update(UtilityFee fee) {
        UtilityFee existing = feeMapper.findById(fee.getId());
        if (existing == null) {
            throw new RuntimeException("费用记录不存在");
        }

        // 重新计算用量和费用
        calculateUsage(fee);
        calculateTotalFee(fee);

        feeMapper.update(fee);
    }

    /**
     * 直接更新费用记录（使用前端传入的金额）
     */
    @Transactional
    public void updateDirect(UtilityFee fee) {
        UtilityFee existing = feeMapper.findById(fee.getId());
        if (existing == null) {
            throw new RuntimeException("费用记录不存在");
        }

        feeMapper.update(fee);
    }
    
    @Transactional
    public void pay(Long id) {
        UtilityFee fee = feeMapper.findById(id);
        if (fee == null) {
            throw new RuntimeException("费用记录不存在");
        }
        if (fee.getStatus() == 1) {
            throw new RuntimeException("该费用已缴纳");
        }
        
        fee.setStatus(1);
        fee.setPayTime(LocalDateTime.now());
        feeMapper.update(fee);
    }
    
    @Transactional
    public void delete(Long id) {
        UtilityFee fee = feeMapper.findById(id);
        if (fee == null) {
            throw new RuntimeException("费用记录不存在");
        }
        if (fee.getStatus() == 1) {
            throw new RuntimeException("已缴费记录无法删除");
        }
        feeMapper.deleteById(id);
    }
    
    /**
     * 批量生成某月所有房间的费用记录
     */
    @Transactional
    public int batchGenerate(Integer year, Integer month) {
        List<Room> rooms = roomMapper.findAll();
        int created = 0;
        
        for (Room room : rooms) {
            // 跳过已存在的记录
            UtilityFee existing = feeMapper.findByRoomAndMonth(room.getId(), year, month);
            if (existing != null) {
                continue;
            }
            
            // 获取上月读数作为起始读数
            UtilityFee lastMonth = feeMapper.findByRoomAndMonth(
                room.getId(), 
                month == 1 ? year - 1 : year, 
                month == 1 ? 12 : month - 1);
            
            UtilityFee fee = new UtilityFee();
            fee.setRoomId(room.getId());
            fee.setYear(year);
            fee.setMonth(month);
            
            // 设置起始读数
            if (lastMonth != null) {
                fee.setElectricityStart(lastMonth.getElectricityEnd());
                fee.setWaterStart(lastMonth.getWaterEnd());
            } else {
                fee.setElectricityStart(BigDecimal.ZERO);
                fee.setWaterStart(BigDecimal.ZERO);
            }
            
            // 默认结束读数为起始读数（需要后续手动填写）
            fee.setElectricityEnd(fee.getElectricityStart());
            fee.setWaterEnd(fee.getWaterStart());
            
            fee.setStatus(0);
            calculateUsage(fee);
            calculateTotalFee(fee);
            
            feeMapper.insert(fee);
            created++;
        }
        
        return created;
    }
    
    private void calculateUsage(UtilityFee fee) {
        fee.setElectricityUsage(MeterReading.usage(fee.getElectricityStart(), fee.getElectricityEnd()));
        fee.setWaterUsage(MeterReading.usage(fee.getWaterStart(), fee.getWaterEnd()));
    }
    
    private void calculateTotalFee(UtilityFee fee) {
        // 计算电费
        BigDecimal electricityFee = BigDecimal.ZERO;
        if (fee.getElectricityUsage() != null) {
            electricityFee = fee.getElectricityUsage().multiply(ELECTRICITY_RATE);
        }
        fee.setElectricityFee(electricityFee);
        
        // 计算水费
        BigDecimal waterFee = BigDecimal.ZERO;
        if (fee.getWaterUsage() != null) {
            waterFee = fee.getWaterUsage().multiply(WATER_RATE);
        }
        fee.setWaterFee(waterFee);
        
        // 计算总费用
        fee.setTotalFee(electricityFee.add(waterFee));
    }
}