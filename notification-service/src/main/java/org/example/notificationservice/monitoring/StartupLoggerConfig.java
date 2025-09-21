package org.example.notificationservice.monitoring;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StartupLoggerConfig {

    private static final Logger log = LoggerFactory.getLogger(StartupLoggerConfig.class);

    @PostConstruct
    public void logDependencies() {
        log.info("🔍 [notification-service] Checking dependencies...");

        if (ServiceWaiter.waitFor("rabbitmq", 5672, 15, 2000)) {
            log.info("✅ RabbitMQ is ready");
        } else {
            log.error("❌ RabbitMQ is not responding!");
        }

        if (ServiceWaiter.waitFor("mailhog", 1025, 15, 2000)) {
            log.info("✅ Mailhog is ready");
        } else {
            log.error("❌ Mailhog is not responding!");
        }

        log.info("🚀 [notification-service] Dependency check completed!");
    }
}
