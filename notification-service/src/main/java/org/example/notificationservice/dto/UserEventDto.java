package org.example.notificationservice.dto;

import org.example.notificationservice.model.UserEventType;

import java.io.Serializable;


public class UserEventDto implements Serializable {
    private String username;
    private String password; // может быть null для DELETED
    private String email;
    private UserEventType action; // CREATED, UPDATED, DELETED
    private String role; // USER или ADMIN (опционально)

    public UserEventDto() {}

    public UserEventDto(String username, String password, String email, UserEventType action, String role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.action = action;
        this.role = role;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public UserEventType getAction() { return action; }
    public void setAction(UserEventType action) { this.action = action; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
