
package com.notification_service.notification_service.service;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

//    @RabbitListener(queues = "recharge-queue")
//    public void consume(Long rechargeId) {
//
//        System.out.println("Notification: Recharge successful for ID: " + rechargeId);
//
//        // later:
//        // send email / SMS
//    }
	
	 private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
	 
	    public void logNotification(String message) {
	        log.info("Notification processed: {}", message);
	    }
}