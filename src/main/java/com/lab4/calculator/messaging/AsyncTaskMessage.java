package com.lab4.calculator.messaging;

import java.time.Instant;

public class AsyncTaskMessage {
    private String id;
    private String taskType;
    private String payload;
    private Instant createdAt;

    public AsyncTaskMessage() {}

    public AsyncTaskMessage(String id, String taskType, String payload) {
        this.id = id;
        this.taskType = taskType;
        this.payload = payload;
        this.createdAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
