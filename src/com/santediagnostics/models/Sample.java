package com.santediagnostics.models;

import java.time.LocalDateTime;

public class Sample {

    private int id;
    private int requestId;
    private String status;
    private LocalDateTime collectedAt;
    private LocalDateTime updatedAt;

    public Sample() {}

    public Sample(int id, int requestId, String status,
                  LocalDateTime collectedAt, LocalDateTime updatedAt) {
        this.id = id;
        this.requestId = requestId;
        this.status = status;
        this.collectedAt = collectedAt;
        this.updatedAt = updatedAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCollectedAt() { return collectedAt; }
    public void setCollectedAt(LocalDateTime collectedAt) { this.collectedAt = collectedAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Sample{id=" + id + ", requestId=" + requestId + ", status=" + status + "}";
    }
}