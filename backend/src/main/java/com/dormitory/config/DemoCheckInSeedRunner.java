package com.dormitory.config;

import com.dormitory.service.DemoCheckInSeedService;
import com.dormitory.utils.SeedDemoGuard;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@ConditionalOnProperty(value = "app.seed-demo.enabled", havingValue = "true")
public class DemoCheckInSeedRunner implements CommandLineRunner {

    private final DemoCheckInSeedService demoCheckInSeedService;
    private final Environment environment;

    @Value("${app.seed-demo.allow-data-wipe:false}")
    private boolean allowDataWipe;

    public DemoCheckInSeedRunner(DemoCheckInSeedService demoCheckInSeedService, Environment environment) {
        this.demoCheckInSeedService = demoCheckInSeedService;
        this.environment = environment;
    }

    @Override
    public void run(String... args) {
        if (!SeedDemoGuard.allow(true, allowDataWipe, Arrays.asList(environment.getActiveProfiles()))) {
            throw new IllegalStateException("演示灌数会清空打卡数据，请仅在非生产环境同时设置 app.seed-demo.enabled 与 app.seed-demo.allow-data-wipe=true");
        }
        demoCheckInSeedService.ensureSchema();
        demoCheckInSeedService.seed();
    }
}
