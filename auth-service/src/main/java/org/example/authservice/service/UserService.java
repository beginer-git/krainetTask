package org.example.authservice.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.authservice.dto.RegisterDto;
import org.example.authservice.dto.UserDto;
import org.example.authservice.dto.UserEventDto;
import org.example.authservice.entity.Role;
import org.example.authservice.entity.User;
import org.example.authservice.entity.UserEventType;
import org.example.authservice.producer.UserEventProducer;
import org.example.authservice.repository.UserRepository;
import org.example.authservice.validation.UserValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserEventProducer eventProducer;
    private final PasswordEncoder passwordEncoder;
    private final UserValidator userValidator;

    public UserService(UserRepository userRepository,
                       UserEventProducer eventProducer,
                       PasswordEncoder passwordEncoder,
                       UserValidator userValidator) {
        this.userRepository = userRepository;
        this.eventProducer = eventProducer;
        this.passwordEncoder = passwordEncoder;
        this.userValidator = userValidator;
    }

    /** 🔹 Получить всех пользователей */
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    /** 🔹 Получить пользователя по id */
    public UserDto getById(Long id) {
        return userRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    /** 🔹 Создание пользователя с передачей роли и сырого пароля (для DataInitializer) */
    public UserDto createAndPublish(String username, String rawPassword, String email,
                                    String firstName, String lastName, Role role) {

        userValidator.validate(
                username,
                rawPassword,
                email
        );


        User user = new User(
                null,
                username,
                passwordEncoder.encode(rawPassword),
                email,
                firstName,
                lastName,
                role
        );

        User saved = userRepository.save(user);

        // событие CREATE (отправляем сырой пароль)
        sendEvent(saved, rawPassword, UserEventType.CREATED);

        return toDto(saved);
    }

    /** 🔹 Обновить пользователя → событие UPDATED */
    public UserDto updateAndPublish(Long id, RegisterDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        String rawPassword = null;
        rawPassword = dto.getPassword();

        userValidator.validate(rawPassword, dto.getEmail());

        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());

        User updated = userRepository.save(user);

        // событие UPDATE
        if (user.getRole() == Role.USER) {
            sendEvent(updated, rawPassword, UserEventType.UPDATED);
        }

        return toDto(updated);
    }

    /** 🔹 Удалить пользователя → событие DELETED */
    public void deleteAndPublish(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        userRepository.delete(user);

        // событие DELETE (пароль не нужен)
        if (user.getRole() == Role.USER) {
            sendEvent(user, null, UserEventType.DELETED);
        }

    }

    // ================= 🔹 Вспомогательные методы =================
    private void sendEvent(User user, String rawPassword, UserEventType action) {
        UserEventDto event = new UserEventDto(
                user.getUsername(),
                rawPassword,
                user.getEmail(),
                user.getRole(),
                action
        );
        eventProducer.sendUserEvent(event);
    }

    private UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole()
        );
    }

    /** 🔹 Подсчёт всех пользователей (для DataInitializer) */
    public long count() {
        return userRepository.count();
    }
}
