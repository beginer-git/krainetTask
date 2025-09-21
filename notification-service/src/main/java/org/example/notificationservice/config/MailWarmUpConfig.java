package org.example.notificationservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailWarmUpConfig {

    private static final Logger log = LoggerFactory.getLogger(MailWarmUpConfig.class);

    @Bean
    public CommandLineRunner warmUpMail(JavaMailSender mailSender) {
        return args -> {
            if (mailSender instanceof JavaMailSenderImpl impl) {
                boolean connected = false;
                int attempts = 0;

                while (!connected && attempts < 10) {
                    try {
                        impl.testConnection();
                        connected = true;
                        log.info("✅ Mail server is ready!");
                    } catch (Exception e) {
                        attempts++;
                        log.warn("⏳ Mail server not ready, retrying... ({}). Error: {}", attempts, e.getMessage());
                        Thread.sleep(3000);
                    }
                }

                if (!connected) {
                    log.error("❌ Mail server is not available after {} attempts", attempts);
                    throw new IllegalStateException("Mail server is not available after retries");
                }
            } else {
                log.warn("⚠ MailSender is not JavaMailSenderImpl, skipping warm-up");
            }
        };
    }
}
