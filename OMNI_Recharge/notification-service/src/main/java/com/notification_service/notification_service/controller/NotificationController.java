package com.notification_service.notification_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.notification_service.notification_service.entity.NotificationLog;
import com.notification_service.notification_service.repository.NotificationLogRepository;


// ─────────────────────────────────────────────────────────────────────────────
// FIX: Added GET /notifications/history so users can view their past
// recharge notifications. Previously only /notifications/health existed,
// making this service a black box — notifications were logged to console
// only, with no user-visible history.
//
// Requires:
//   NotificationLog entity   — see NotificationLog.java
//   NotificationLogRepository — see NotificationLogRepository.java
//   RabbitConsumer            — updated to save a NotificationLog on consume
//   notification-service pom  — add spring-boot-starter-data-jpa + postgresql
//   application.properties    — add datasource config, remove JPA exclusion
// ─────────────────────────────────────────────────────────────────────────────
@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationLogRepository notificationLogRepository;

    // Health check — kept as-is
    @GetMapping("/health")
    public String health() {
        return "Notification service is running";
    }

    // ── Get notification history for the logged-in user ──────────────
    @GetMapping("/history")
    public ResponseEntity<?> getMyNotifications(
            @RequestHeader("X-User-Id") String userId) {

        List<NotificationLog> logs = notificationLogRepository.findByUserId(userId);
        return ResponseEntity.ok(logs);
    }
}