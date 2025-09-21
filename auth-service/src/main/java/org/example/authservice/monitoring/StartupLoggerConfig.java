package org.example.authservice.monitoring;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StartupLoggerConfig {

    private static final Logger log = LoggerFactory.getLogger(StartupLoggerConfig.class);

    @PostConstruct
    public void logDependencies() {
        log.info("🔍 [auth-service] Checking dependencies...");

        if (ServiceWaiter.waitFor("rabbitmq", 5672, 15, 2000)) {
            log.info("✅ RabbitMQ is ready");
        } else {
            log.error("❌ RabbitMQ is not responding!");
        }

        if (ServiceWaiter.waitFor("auth-db", 3306, 15, 2000)) {
            log.info("✅ MySQL (auth-db) is ready");
        } else {
            log.error("❌ MySQL (auth-db) is not responding!");
        }

        log.info("🚀 [auth-service] Dependency check completed!");
    }
}
