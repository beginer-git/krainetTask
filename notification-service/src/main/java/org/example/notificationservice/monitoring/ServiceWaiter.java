package org.example.notificationservice.monitoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class ServiceWaiter {

    private static final Logger log = LoggerFactory.getLogger(ServiceWaiter.class);

    public static boolean waitFor(String host, int port, int maxAttempts, long delayMillis) {
        int attempt = 1;
        while (attempt <= maxAttempts) {
            try (Socket socket = new Socket(host, port)) {
                log.info("✅ Service {}:{} is UP!", host, port);
                return true;
            } catch (Exception e) {
                log.warn("⏳ Waiting for service {}:{} (attempt {}/{})", host, port, attempt, maxAttempts);
                try {
                    TimeUnit.MILLISECONDS.sleep(delayMillis);
                } catch (InterruptedException ignored) {}
                attempt++;
            }
        }
        log.error("❌ Service {}:{} did not respond after {} attempts", host, port, maxAttempts);
        return false;
    }
}
