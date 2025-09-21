package org.example.authservice.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.example.authservice.dto.RegisterDto;
import org.example.authservice.dto.UserDto;
import org.example.authservice.security.CustomUserDetails;
import org.example.authservice.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /** 🔹 Получить всех пользователей (только ADMIN) */
    @GetMapping
    public List<UserDto> getAll(@AuthenticationPrincipal CustomUserDetails currentUser) {
        ensureAdmin(currentUser);
        return userService.getAll();
    }

    /** 🔹 Получить пользователя по id (ADMIN или владелец) */
    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id,
                           @AuthenticationPrincipal CustomUserDetails currentUser) {
        ensureOwnerOrAdmin(currentUser, id);
        return userService.getById(id);
    }

    /** 🔹 Обновить пользователя (ADMIN или владелец) → событие UPDATED */
    @PutMapping("/{id}")
    public UserDto update(@PathVariable Long id,
                          @RequestBody RegisterDto dto,
                          @AuthenticationPrincipal CustomUserDetails currentUser) {
        ensureOwnerOrAdmin(currentUser, id);
        return userService.updateAndPublish(id, dto);
    }

    /** 🔹 Удалить пользователя (ADMIN или владелец) → событие DELETED */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id,
                       @AuthenticationPrincipal CustomUserDetails currentUser) {
        ensureOwnerOrAdmin(currentUser, id);
        userService.deleteAndPublish(id);
    }

    // ================= 🔹 Вспомогательные проверки =================
    private void ensureAdmin(CustomUserDetails currentUser) {
        if (!currentUser.hasRole("ADMIN")) {
            throw new SecurityException("Access denied: ADMIN only");
        }
    }

    private void ensureOwnerOrAdmin(CustomUserDetails currentUser, Long targetUserId) {
        if (!currentUser.hasRole("ADMIN") && !currentUser.getId().equals(targetUserId)) {
            throw new SecurityException("Access denied: must be ADMIN or resource owner");
        }
    }
}
