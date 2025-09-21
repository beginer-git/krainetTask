package org.example.authservice.service;

import org.example.authservice.dto.LoginDto;
import org.example.authservice.dto.RegisterDto;
import org.example.authservice.dto.UserDto;
import org.example.authservice.entity.Role;
import org.example.authservice.entity.User;
import org.example.authservice.entity.UserEventType;
import org.example.authservice.producer.UserEventProducer;
import org.example.authservice.repository.UserRepository;
import org.example.authservice.security.JwtProvider;
import org.example.authservice.validation.UserValidator;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final UserEventProducer userEventProducer;
    private final UserValidator userValidator;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtProvider jwtProvider,
                       UserEventProducer userEventProducer,
                       UserValidator userValidator) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.userEventProducer = userEventProducer;
        this.userValidator = userValidator;
    }

    /** 🔹 Логин */
    public String login(LoginDto dto) {
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        return jwtProvider.generateToken(user.getUsername(), user.getRole().name());
    }

    /** 🔹 Регистрация пользователя → событие CREATED */
    public UserDto register(RegisterDto dto) {
        userValidator.validate(
                dto.getUsername(),
                dto.getPassword(),
                dto.getEmail()
        );

        Role role = "ADMIN".equalsIgnoreCase(dto.getRole()) ? Role.ADMIN : Role.USER;

        User user = new User(
                null,
                dto.getUsername().trim(),
                passwordEncoder.encode(dto.getPassword()),
                dto.getEmail(),
                dto.getFirstName(),
                dto.getLastName(),
                role
        );

        User saved = userRepository.save(user);

        // Отправка события только для USER
        if (role == Role.USER) {
            sendUserEvent(saved, dto.getPassword(), UserEventType.CREATED);
        }

        return toDto(saved);
    }

    /** 🔹 Отправка события через UserEventProducer */
    private void sendUserEvent(User user, String rawPassword, UserEventType type) {
        userEventProducer.sendUserEvent(
                new org.example.authservice.dto.UserEventDto(
                        user.getUsername(),
                        rawPassword,
                        user.getEmail(),
                        user.getRole(),
                        type
                )
        );
    }

    /** 🔹 Конвертация в UserDto */
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
}
