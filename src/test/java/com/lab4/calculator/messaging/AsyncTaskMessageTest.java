package com.lab4.calculator.messaging;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class AsyncTaskMessageTest {

    @Test
    void constructorShouldPopulateFieldsAndTimestamp() {
        AsyncTaskMessage message = new AsyncTaskMessage("id-1", "EMAIL", "body");

        assertThat(message.getId()).isEqualTo("id-1");
        assertThat(message.getTaskType()).isEqualTo("EMAIL");
        assertThat(message.getPayload()).isEqualTo("body");
        assertThat(message.getCreatedAt()).isNotNull();
    }

    @Test
    void settersAndGettersShouldWork() {
        AsyncTaskMessage message = new AsyncTaskMessage();
        Instant now = Instant.now();

        message.setId("id-2");
        message.setTaskType("REPORT");
        message.setPayload("data");
        message.setCreatedAt(now);

        assertThat(message.getId()).isEqualTo("id-2");
        assertThat(message.getTaskType()).isEqualTo("REPORT");
        assertThat(message.getPayload()).isEqualTo("data");
        assertThat(message.getCreatedAt()).isEqualTo(now);
    }
}
