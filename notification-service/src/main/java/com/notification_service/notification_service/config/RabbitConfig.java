
package com.notification_service.notification_service.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;

@Configuration
public class RabbitConfig {

    //Same queue name as payment-service
    @Bean
    public Queue queue() {
        return new Queue("recharge-queue", true);
    }

    // Same exchange as payment-service
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange("recharge-exchange");
    }

    //Same routing key pattern as payment-service
    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("recharge.*");
    }

    //Needed to deserialize Transaction object from JSON
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}