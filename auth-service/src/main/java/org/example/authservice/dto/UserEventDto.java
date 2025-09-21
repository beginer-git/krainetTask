package org.example.authservice.dto;

import org.example.authservice.entity.Role;
import org.example.authservice.entity.UserEventType;

import java.io.Serializable;

public class UserEventDto implements Serializable {

    private String username;
    private String password;
    private String email;
    private UserEventType action; // CREATED, UPDATED, DELETED
    private Role role;

    public UserEventDto() {
    }

    public UserEventDto(
            String username,
            String password,
            String email,
            Role role,
            UserEventType action

    ) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.action = action;

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserEventType getAction() {
        return action;
    }

    public void setAction(UserEventType action) {
        this.action = action;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "UserEventDto{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", action=" + action +
                '}';
    }
}
