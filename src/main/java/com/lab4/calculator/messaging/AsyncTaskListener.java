package com.lab4.calculator.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class AsyncTaskListener {
    private static final Logger LOG = LoggerFactory.getLogger(AsyncTaskListener.class);

    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void handleAsyncTask(AsyncTaskMessage message) {
        LOG.info("Received async task id={} type={} payload={}", message.getId(), message.getTaskType(), message.getPayload());

        // Simulate processing (e.g., generating certificate, sending email)
        try {
            LOG.info("Processing task {}...", message.getId());
            Thread.sleep(2000); // simulate work
            LOG.info("Completed task {}", message.getId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.error("Task interrupted", e);
        }
    }
}
