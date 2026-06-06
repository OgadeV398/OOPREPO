package com.santediagnostics.models;

import java.time.LocalDateTime;

public class Result {

    private int id;
    private int requestId;
    private String filePath;
    private String resultType;
    private String resultText;
    private boolean isVerified;
    private boolean isVisibleToCustomer;
    private LocalDateTime uploadedAt;
    private LocalDateTime verifiedAt;

    public Result() {}

    public Result(int id, int requestId, String filePath, String resultType,
                  String resultText, boolean isVerified, boolean isVisibleToCustomer,
                  LocalDateTime uploadedAt, LocalDateTime verifiedAt) {
        this.id = id;
        this.requestId = requestId;
        this.filePath = filePath;
        this.resultType = resultType;
        this.resultText = resultText;
        this.isVerified = isVerified;
        this.isVisibleToCustomer = isVisibleToCustomer;
        this.uploadedAt = uploadedAt;
        this.verifiedAt = verifiedAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getResultType() { return resultType; }
    public void setResultType(String resultType) { this.resultType = resultType; }

    public String getResultText() { return resultText; }
    public void setResultText(String resultText) { this.resultText = resultText; }

    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean isVerified) { this.isVerified = isVerified; }

    public boolean isVisibleToCustomer() { return isVisibleToCustomer; }
    public void setVisibleToCustomer(boolean isVisibleToCustomer) { this.isVisibleToCustomer = isVisibleToCustomer; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }

    public LocalDateTime getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; }

    @Override
    public String toString() {
        return "Result{id=" + id + ", requestId=" + requestId + ", isVerified=" + isVerified + "}";
    }
}