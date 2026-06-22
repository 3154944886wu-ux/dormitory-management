package com.dormitory.config;

import com.dormitory.service.DemoCheckInSeedService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "app.seed-demo.enabled", havingValue = "true")
public class DemoCheckInSeedRunner implements CommandLineRunner {

    private final DemoCheckInSeedService demoCheckInSeedService;

    public DemoCheckInSeedRunner(DemoCheckInSeedService demoCheckInSeedService) {
        this.demoCheckInSeedService = demoCheckInSeedService;
    }

    @Override
    public void run(String... args) {
        demoCheckInSeedService.ensureSchema();
        demoCheckInSeedService.seed();
    }
}
