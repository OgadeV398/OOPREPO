package com.santediagnostics.models;

import java.time.LocalDateTime;

public class TestRequest {

    private int id;
    private int customerId;
    private int testTypeId;
    private String status;
    private String paymentStatus;
    private LocalDateTime requestedAt;
    private LocalDateTime readyAt;

    // Extra fields for display purposes
    private String customerName;
    private String testTypeName;

    public TestRequest() {}

    public TestRequest(int id, int customerId, int testTypeId, String status,
                       String paymentStatus, LocalDateTime requestedAt, LocalDateTime readyAt) {
        this.id = id;
        this.customerId = customerId;
        this.testTypeId = testTypeId;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.requestedAt = requestedAt;
        this.readyAt = readyAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public int getTestTypeId() { return testTypeId; }
    public void setTestTypeId(int testTypeId) { this.testTypeId = testTypeId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }

    public LocalDateTime getReadyAt() { return readyAt; }
    public void setReadyAt(LocalDateTime readyAt) { this.readyAt = readyAt; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getTestTypeName() { return testTypeName; }
    public void setTestTypeName(String testTypeName) { this.testTypeName = testTypeName; }

    @Override
    public String toString() {
        return "TestRequest{id=" + id + ", status=" + status + ", paymentStatus=" + paymentStatus + "}";
    }
}