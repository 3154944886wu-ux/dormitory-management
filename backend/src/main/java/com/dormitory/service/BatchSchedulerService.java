package com.dormitory.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class BatchSchedulerService {

    private final DormBatchService batchService;

    public BatchSchedulerService(DormBatchService batchService) {
        this.batchService = batchService;
    }

    @Scheduled(fixedDelay = 30_000)
    public void autoTransitionBatches() {
        batchService.autoTransitionRunningToConfirming();
        batchService.autoTransitionConfirmingToFinished();
    }
}
