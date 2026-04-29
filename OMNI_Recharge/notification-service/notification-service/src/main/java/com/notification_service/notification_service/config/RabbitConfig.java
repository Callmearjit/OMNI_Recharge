
package com.notification_service.notification_service.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class RabbitConfig {

    @Bean
    public Queue queue() {
        return new Queue("recharge-queue", true);
    }
}