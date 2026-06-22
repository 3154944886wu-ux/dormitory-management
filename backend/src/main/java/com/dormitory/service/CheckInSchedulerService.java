package com.dormitory.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

@Service
public class CheckInSchedulerService {

    private final CheckInService checkInService;
    private final OperationLogService operationLogService;

    public CheckInSchedulerService(CheckInService checkInService, OperationLogService operationLogService) {
        this.checkInService = checkInService;
        this.operationLogService = operationLogService;
    }

    /**
     * 每 5 分钟扫描一次，超过未归截止后自动记未归。
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void generateAbsentAfterDeadlineJob() {
        int count = checkInService.generateAbsentAfterDeadline();
        if (count > 0) {
            operationLogService.log(null, "system", "scheduler", "checkin.generate_absent", Map.of(
                    "date", LocalDate.now().toString(),
                    "count", count
            ));
        }
    }

    /**
     * 每日零点后扫描前一天未归学生，重复执行不会重复生成同类异常。
     */
    @Scheduled(cron = "0 5 0 * * ?")
    public void generateDailyMissingExceptions() {
        LocalDate targetDate = LocalDate.now().minusDays(1);
        int count = checkInService.generateMissingCheckIns(targetDate);
        operationLogService.log(null, "system", "scheduler", "checkin.generate_missing", Map.of(
                "date", targetDate.toString(),
                "count", count
        ));
    }
}
