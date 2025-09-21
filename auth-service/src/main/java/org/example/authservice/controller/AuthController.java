package org.example.authservice.controller;

import org.example.authservice.dto.LoginDto;
import org.example.authservice.dto.RegisterDto;
import org.example.authservice.dto.UserDto;
import org.example.authservice.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /** 🔹 Регистрация нового пользователя (USER → событие CREATED) */
    @PostMapping("/register")
    public UserDto register(@RequestBody RegisterDto dto) {
        return authService.register(dto);
    }

    /** 🔹 Логин пользователя, возвращает JWT */
    @PostMapping("/login")
    public String login(@RequestBody LoginDto dto) {
        return authService.login(dto);
    }
}
