package org.example.authservice.validation;

import org.example.authservice.repository.UserRepository;
import org.example.authservice.exception.UsernameAlreadyExistsException;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {

    private final UserRepository userRepository;

    public UserValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validate(String username, String password, String email) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be blank");
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be blank");
        }

        if (userRepository.findByUsername(username.trim()).isPresent()) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        if (email == null || email.isBlank() || !isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email address");
        }
    }

    public void validate(String password, String email) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be blank");
        }

        if (email == null || email.isBlank() || !isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email address");
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }
}

