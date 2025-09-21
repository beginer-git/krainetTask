package org.example.notificationservice.repository;

import org.example.notificationservice.model.Role;
import org.example.notificationservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByRole(Role role);
}
