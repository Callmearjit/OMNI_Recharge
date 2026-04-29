package com.notification_service.notification_service.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.notification_service.notification_service.dto.TransactionEvent;
import com.notification_service.notification_service.service.NotificationService;

@Component
public class RabbitConsumer {

    private static final Logger log = LoggerFactory.getLogger(RabbitConsumer.class);

    @Autowired
    private NotificationService notificationService;

    // FIX: Receives TransactionEvent directly — no manual ObjectMapper needed.
    // This works because SimpleRabbitListenerContainerFactory now has
    // Jackson2JsonMessageConverter set (see RabbitConfig.java).
    // Previously the method took a raw String and parsed it manually,
    // which was fragile and bypassed the configured converter entirely.
    @RabbitListener(queues = "recharge-queue")
    public void receiveMessage(TransactionEvent event) {

        log.info("Notification received — rechargeId: {}, status: {}, userId: {}",
                event.getRechargeId(), event.getStatus(), event.getUserId());

        try {
            notificationService.processNotification(event);
            sendEmail(event);
            sendSms(event);
        } catch (Exception e) {
            log.error("Failed to dispatch notification for rechargeId: {}",
                    event.getRechargeId(), e);
            // Re-throw so RabbitMQ routes the message to the DLQ (recharge-dlq)
            throw new RuntimeException("Notification dispatch failed — routing to DLQ", e);
        }
    }

    private void sendEmail(TransactionEvent event) {
        log.info("[EMAIL] Recharge {} | User: {} | Status: {} | Amount: Rs.{} | Ref: {}",
                event.getRechargeId(),
                event.getUserId(),
                event.getStatus(),
                event.getAmount(),
                event.getTransactionRef());
    }

    private void sendSms(TransactionEvent event) {
        log.info("[SMS] Recharge {} | User: {} | Status: {} | Amount: Rs.{}",
                event.getRechargeId(),
                event.getUserId(),
                event.getStatus(),
                event.getAmount());
    }
}