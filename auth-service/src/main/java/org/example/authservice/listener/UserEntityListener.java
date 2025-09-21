package org.example.authservice.listener;

import jakarta.persistence.*;
import org.example.authservice.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserEntityListener {

    private static final Logger log = LoggerFactory.getLogger(UserEntityListener.class);

    @PrePersist
    public void prePersist(User user) {
        log.info("🟢 [DB INSERT] Создание пользователя: username={}, email={}, role={}",
                user.getUsername(), user.getEmail(), user.getRole());
    }

    @PostPersist
    public void postPersist(User user) {
        log.info("✅ [DB INSERT COMPLETE] Пользователь создан с ID={}", user.getId());
    }

    @PreUpdate
    public void preUpdate(User user) {
        log.info("🟡 [DB UPDATE] Пользователь готовится к обновлению: ID={}, username={}",
                user.getId(), user.getUsername());
    }

    @PostUpdate
    public void postUpdate(User user) {
        log.info("✅ [DB UPDATE COMPLETE] Пользователь обновлён: ID={}", user.getId());
    }

    @PreRemove
    public void preRemove(User user) {
        log.info("🔴 [DB DELETE] Удаление пользователя: ID={}, username={}, email={}",
                user.getId(), user.getUsername(), user.getEmail());
    }

    @PostRemove
    public void postRemove(User user) {
        log.info("❌ [DB DELETE COMPLETE] Пользователь удалён: ID={}", user.getId());
    }
}
