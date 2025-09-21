package org.example.authservice.producer;

import org.example.authservice.config.RabbitConfig;
import org.example.authservice.dto.UserEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserEventProducer {

    private static final Logger log = LoggerFactory.getLogger(UserEventProducer.class);

    private final RabbitTemplate rabbitTemplate;

    public UserEventProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendUserEvent(UserEventDto event) {
        // Отправка события в RabbitMQ
        rabbitTemplate.convertAndSend(
                RabbitConfig.USER_EXCHANGE,
                RabbitConfig.USER_ROUTING_KEY,
                event
        );

        // Преобразуем enum в русское слово
        String actionText = switch (event.getAction()) {
            case CREATED -> "Создан";
            case UPDATED -> "Изменен";
            case DELETED -> "Удален";
        };

        // Формирование темы и текста сообщения
        String subject = String.format("Тема: %s пользователь %s", actionText, event.getUsername());

        String body = String.format(
                "Текст: %s пользователь с именем - %s, паролем - %s и почтой - %s.",
                actionText,
                event.getUsername(),
                event.getPassword() != null ? event.getPassword() : "N/A",
                event.getEmail(),
                event.getRole() != null ? event.getRole() : "(не указан)"
        );

        // Логирование
        log.info("📤 [EVENT SENT] Exchange='{}', RoutingKey='{}'",
                RabbitConfig.USER_EXCHANGE, RabbitConfig.USER_ROUTING_KEY);
        log.info("📤 Subject: {}", subject);
        log.info("📤 Body: {}", body);
        log.debug("📤 Полный объект UserEventDto: {}", event);
    }
}
