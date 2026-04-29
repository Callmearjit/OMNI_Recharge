
package com.notification_service.notification_service.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @RabbitListener(queues = "recharge-queue")
    public void consume(Long rechargeId) {

        System.out.println("Notification: Recharge successful for ID: " + rechargeId);

        // later:
        // send email / SMS
    }
}