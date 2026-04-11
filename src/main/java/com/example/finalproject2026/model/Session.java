package com.example.finalproject2026.model;

import java.time.LocalDateTime;
import java.time.Duration;

public class Session {

    private String username;
    private int sessionId;
    private int computerId;
    private int userID;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean isActive;
    private double hourlyRate;

    // 🔹 Constructor for NEW session
    public Session(int sessionId, int computerId, int username, double hourlyRate) {
        this.sessionId = sessionId;
        this.computerId = computerId;
        this.userID = username;
        this.hourlyRate = hourlyRate;
        this.startTime = LocalDateTime.now();
        this.isActive = true;
    }

    // 🔹 Constructor for DB loading
    public Session(int sessionId, int computerId, int userID,
                   LocalDateTime startTime, LocalDateTime endTime,
                   double hourlyRate, boolean isActive) {

        this.sessionId = sessionId;
        this.computerId = computerId;
        this.userID = userID;
        this.startTime = startTime;
        this.endTime = endTime;
        this.hourlyRate = hourlyRate;
        this.isActive = isActive;
    }

    public void endSession() {
        if (!isActive) return;
        this.endTime = LocalDateTime.now();
        this.isActive = false;
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
    public int getUserID() { return userID; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public boolean isActive() { return isActive; }
    public double getHourlyRate() { return hourlyRate; }

    public void setUsername(String username){
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    // 🔹 Setter (needed for DB auto ID)
    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }
}