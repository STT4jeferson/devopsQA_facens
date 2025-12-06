package com.lab4.calculator.messaging;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@ExtendWith(MockitoExtension.class)
class MessageProducerTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Test
    void shouldSendMessageToConfiguredExchangeAndRoutingKey() {
        MessageProducer producer = new MessageProducer(rabbitTemplate);
        AsyncTaskMessage message = new AsyncTaskMessage("id-123", "REPORT", "{\"payload\":true}");

        producer.sendAsyncTask(message);

        verify(rabbitTemplate).convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.ROUTING_KEY, message);
    }
}
