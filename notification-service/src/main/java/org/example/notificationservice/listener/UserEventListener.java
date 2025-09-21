package org.example.notificationservice.listener;

import org.example.notificationservice.dto.UserEventDto;
import org.example.notificationservice.model.Role;
import org.example.notificationservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserEventListener {

    private static final Logger log = LoggerFactory.getLogger(UserEventListener.class);

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;

    public UserEventListener(JavaMailSender mailSender, UserRepository userRepository) {
        this.mailSender = mailSender;
        this.userRepository = userRepository;
    }

    @RabbitListener(queues = "user.queue")
    public void handleUserEvent(UserEventDto event) {
        log.info("📩 [EVENT RECEIVED] Queue='user.queue'");
        log.info("📩 Событие: {} | Username: {} | Email: {} | Role: {}",
                event.getAction(), event.getUsername(), event.getEmail(), event.getRole());
        log.debug("📩 Полный объект UserEventDto: {}", event);

        String actionText = switch (event.getAction()) {
            case CREATED -> "Создан";
            case UPDATED -> "Изменён";
            case DELETED -> "Удалён";
        };

        String subject = String.format("Событие: %s пользователь %s", actionText, event.getUsername());
        String body = String.format(
                "Событие: %s%nИмя пользователя: %s%nПароль: %s%nПочта: %s%nРоль: %s",
                actionText,
                event.getUsername(),
                event.getPassword() != null ? event.getPassword() : "(не указан)",
                event.getEmail(),
                event.getRole() != null ? event.getRole() : "(не указан)"
        );

        // Получаем всех администраторов
        List<String> adminEmails = userRepository.findByRole(Role.ADMIN)
                .stream()
                .map(user -> user.getEmail())
                .toList();

        for (String adminEmail : adminEmails) {
            boolean sent = sendEmailWithRetry(adminEmail, subject, body, 3, 2000, event.getUsername());

            if (sent) {
                log.info("✅ Письмо отправлено администратору: {}", adminEmail);
            } else {
                log.error("❌ Письмо НЕ удалось отправить администратору: {}", adminEmail);
            }
        }

        log.info("📬 Отправка завершена. Администраторы, которым пытались отправить письмо: {}",
                String.join(", ", adminEmails));
    }

    private boolean sendEmailWithRetry(String to, String subject, String text,
                                       int maxAttempts, long delayMs, String username) {
        int attempts = 0;

        while (attempts < maxAttempts) {
            try {
                attempts++;
                sendEmail(to, subject, text);
                log.info("📧 Письмо успешно отправлено (попытка {} из {}) администратору {}", attempts, maxAttempts, to);
                return true;
            } catch (MailException ex) {
                log.error("❌ Ошибка отправки письма (попытка {} из {}) администратору {} ({})",
                        attempts, maxAttempts, to, ex.getMessage());
            } catch (Exception ex) {
                log.error("❌ Непредвиденная ошибка при отправке письма (попытка {} из {}) администратору {} ({})",
                        attempts, maxAttempts, to, ex.getMessage());
            }

            if (attempts < maxAttempts) {
                try {
                    Thread.sleep(delayMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("⚠️ Retry прерван для администратора {} ({})", to, username);
                    return false;
                }
            }
        }

        log.error("🚨 Не удалось отправить письмо администратору {} после {} попыток", to, maxAttempts);
        return false;
    }

    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom("no-reply@krainet.local");
        mailSender.send(message);
    }
}
