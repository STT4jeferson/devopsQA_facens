package com.lab4.calculator.messaging;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class AsyncTaskListenerTest {

    @AfterEach
    void clearInterruptFlag() {
        Thread.interrupted();
    }

    @Test
    void handleAsyncTaskShouldRespectInterruptWithoutSleeping() {
        AsyncTaskListener listener = new AsyncTaskListener();
        AsyncTaskMessage message = new AsyncTaskMessage("id-3", "REPORT", "payload");

        Thread.currentThread().interrupt();

        listener.handleAsyncTask(message);

        assertThat(Thread.currentThread().isInterrupted()).isTrue();
    }

    @Test
    void handleAsyncTaskShouldProcessNormally() {
        AsyncTaskListener listener = new AsyncTaskListener();
        AsyncTaskMessage message = new AsyncTaskMessage("id-4", "EMAIL", "body");

        listener.handleAsyncTask(message);

        assertThat(Thread.currentThread().isInterrupted()).isFalse();
    }
}
