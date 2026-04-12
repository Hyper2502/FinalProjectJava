package com.example.finalproject2026.model;

import java.time.LocalDateTime;
import java.time.Duration;

public class Session {
    private int sessionId;
    private final int computerId;
    private final int userId;
    private String username;
    private final LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean isActive;
    private final double hourlyRate;
    private double totalCost;

    // 1️⃣ Constructor for NEW session (4 params) - USED BY SessionManager
    public Session(int sessionId, int computerId, int userId, double hourlyRate) {
        this.sessionId = sessionId;
        this.computerId = computerId;
        this.userId = userId;
        this.hourlyRate = hourlyRate;
        this.startTime = LocalDateTime.now();
        this.isActive = true;
        this.totalCost = 0.0;
    }

    // 2️⃣ Constructor for DB Loading (7 params)
    public Session(int sessionId, int computerId, int userId,
                   LocalDateTime startTime, LocalDateTime endTime,
                   double hourlyRate, boolean isActive) {
        this.sessionId = sessionId;
        this.computerId = computerId;
        this.userId = userId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.hourlyRate = hourlyRate;
        this.isActive = isActive;
        this.totalCost = 0.0;
    }

    // 3️⃣ Constructor for DB Loading (8 params)
    public Session(int sessionId, int computerId, int userId,
                   LocalDateTime startTime, LocalDateTime endTime,
                   double hourlyRate, boolean isActive, double totalCost) {
        this.sessionId = sessionId;
        this.computerId = computerId;
        this.userId = userId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.hourlyRate = hourlyRate;
        this.isActive = isActive;
        this.totalCost = totalCost;
    }

    public void endSession() {
        if(!isActive) {
            throw new IllegalStateException("Session has been ended");
        }
        this.endTime = LocalDateTime.now();
        this.isActive = false;
        this.totalCost = calculateCost();
    }

    public long getDurationMinutes() {
        LocalDateTime end = (endTime != null) ? endTime : LocalDateTime.now();
        return Duration.between(startTime, end).toMinutes();
    }

    public double calculateCost() {
        return (getDurationMinutes() / 60.0) * hourlyRate;
    }

    // Getters
    public int getSessionId() { return sessionId; }
    public int getComputerId() { return computerId; }
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public boolean isActive() { return isActive; }
    public double getHourlyRate() { return hourlyRate; }
    public double getTotalCost() { return isActive ? calculateCost() : totalCost; }

    // Setters
    public void setSessionId(int sessionId) { this.sessionId = sessionId; }
    public void setUsername(String username) { this.username = username; }

    @Override
    public String toString() {
        return String.format("Session[id=%d, user=%s, computer=%d, active=%s, cost=$%.2f]",
                sessionId, username, computerId, isActive, totalCost);
    }
}