package org.example.notificationservice.controller;

import org.example.notificationservice.dto.NotificationRequest;
import org.example.notificationservice.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final EmailService emailService;

    public NotificationController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestBody NotificationRequest request) {
        emailService.sendAdminNotification(
                request.getSubject(),
                request.getText(),
                request.getRecipients().toArray(new String[0])
        );
        return ResponseEntity.ok("Email sent to: " + String.join(", ", request.getRecipients()));
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Notification service is up and running ✅");
    }
}
