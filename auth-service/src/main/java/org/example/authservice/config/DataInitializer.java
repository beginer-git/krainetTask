package org.example.authservice.config;

import org.example.authservice.entity.Role;
import org.example.authservice.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;

    public DataInitializer(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userService.count() == 0) { // только если таблица пустая

            // Ждём немного, чтобы RabbitMQ + Notification-service успели подняться
            Thread.sleep(5000);

            userService.createAndPublish(
                    "maria",
                    "password123",
                    "maria.smith@example.com",
                    "Maria",
                    "Smith",
                    Role.USER
            );

            userService.createAndPublish(
                    "takashi",
                    "password123",
                    "takashi.tanaka@example.com",
                    "Takashi",
                    "Tanaka",
                    Role.USER
            );

            userService.createAndPublish(
                    "sophie",
                    "admin123",
                    "sophie.martin@example.com",
                    "Sophie",
                    "Martin",
                    Role.ADMIN
            );

            System.out.println("✅ Тестовые пользователи инициализированы, события отправлены");
        }
    }
}
