package com.santediagnostics.models;

import java.time.LocalDateTime;

public class TestType {

    private int id;
    private String name;
    private double price;
    private int turnaroundTime;
    private String resultFormat;
    private LocalDateTime createdAt;

    public TestType() {}

    public TestType(int id, String name, double price, int turnaroundTime,
                    String resultFormat, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.turnaroundTime = turnaroundTime;
        this.resultFormat = resultFormat;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getTurnaroundTime() { return turnaroundTime; }
    public void setTurnaroundTime(int turnaroundTime) { this.turnaroundTime = turnaroundTime; }

    public String getResultFormat() { return resultFormat; }
    public void setResultFormat(String resultFormat) { this.resultFormat = resultFormat; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "TestType{id=" + id + ", name=" + name + ", price=" + price + "}";
    }
}