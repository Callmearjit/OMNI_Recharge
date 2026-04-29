package com.notification_service.notification_service.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.notification_service.notification_service.dto.TransactionEvent;
import com.notification_service.notification_service.entity.NotificationLog;
import com.notification_service.notification_service.repository.NotificationLogRepository;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    // FIX: Repository was missing — processNotification only logged to console.
    // Without saving to the DB, GET /notifications/history always returned [].
    @Autowired
    private NotificationLogRepository notificationLogRepository;

    public void processNotification(TransactionEvent event) {

        log.info("Processing notification — rechargeId: {}, userId: {}, status: {}, amount: Rs.{}, ref: {}",
                event.getRechargeId(),
                event.getUserId(),
                event.getStatus(),
                event.getAmount(),
                event.getTransactionRef() != null ? event.getTransactionRef() : "N/A");

        // FIX: Persist the notification so /notifications/history works
        NotificationLog notificationLog = new NotificationLog();
        notificationLog.setUserId(event.getUserId());
        notificationLog.setRechargeId(event.getRechargeId());
        notificationLog.setAmount(event.getAmount());
        notificationLog.setStatus(event.getStatus());
        notificationLog.setTransactionRef(event.getTransactionRef());
        notificationLog.setCreatedAt(
                event.getCreatedAt() != null ? event.getCreatedAt() : LocalDateTime.now());

        notificationLogRepository.save(notificationLog);

        log.info("NotificationLog saved for rechargeId: {}", event.getRechargeId());
    }
}