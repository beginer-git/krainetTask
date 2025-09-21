package org.example.authservice.config;

import org.example.authservice.entity.User;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class UserUpdateInterceptor extends EmptyInterceptor {

    private static final Logger log = LoggerFactory.getLogger(UserUpdateInterceptor.class);

    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState,
                                Object[] previousState, String[] propertyNames, Type[] types) {

        // 🔒 Проверяем, что это именно наша сущность User
        if (entity instanceof User user) {
            log.info("🔍 [DB CHANGE DETECTED] User ID={} (username={})", user.getId(), user.getUsername());

            for (int i = 0; i < propertyNames.length; i++) {
                Object oldValue = previousState[i];
                Object newValue = currentState[i];

                if ((oldValue == null && newValue != null) ||
                        (oldValue != null && !oldValue.equals(newValue))) {
                    log.info("   ✏️ Field '{}' изменено: {} → {}",
                            propertyNames[i], oldValue, newValue);
                }
            }
        }

        return false; // ⚠️ важно: изменений сам Interceptor не вносит
    }
}
