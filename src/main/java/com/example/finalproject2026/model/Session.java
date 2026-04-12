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

    // Constructor for NEW session FIXED
    public Session(int sessionId, int computerId, int userId, double hourlyRate) {
        this.sessionId = sessionId;
        this.computerId = computerId;
        this.userId = userId;
        this.hourlyRate = hourlyRate;
        this.startTime = LocalDateTime.now();
        this.isActive = true;
        this.totalCost = 0.0;
    }

   // Constructor for DB Loading FIXED
public Session(int sessionId, int computerId, int userId, String username,
               LocalDateTime startTime, LocalDateTime endTime, double hourlyRate, boolean isActive, double totalCost) {

        this.sessionId = sessionId;
        this.computerId = computerId;
        this.userId = userId;
        this.username = username;
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

    // 🔹 Getters
    public int getSessionId() { return sessionId; }
    public int getComputerId() { return computerId; }
    public int getUserId() { return userId; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public boolean isActive() { return isActive; }
    public double gethourlyRate() { return hourlyRate; }
    public double gettotalCost() { return isActive? calculateCost(): totalCost;}


    //Setters FIXED
    public void setsessionId(int sessionId) {
        this.sessionId = sessionId;
    }
    public void setusername(String username) {
        this.username = username;
    }

    @Override
public String toString() {
    return String.format("Session[id=%d, user=%s, computer=%d, active=%s, cost=$%.2f]",
            sessionId, username, computerId, isActive, totalCost);
    }
}