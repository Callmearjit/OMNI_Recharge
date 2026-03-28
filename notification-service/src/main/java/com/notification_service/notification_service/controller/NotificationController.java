package com.notification_service.notification_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    // ✅ Health check endpoint so Swagger has something to show
    @GetMapping("/health")
    public String health() {
        return "Notification service is running";
    }
}