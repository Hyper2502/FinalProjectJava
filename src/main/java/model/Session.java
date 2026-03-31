package model;

import java.time.LocalDateTime;
import java.time.Duration;

public class Session {

    private int sessionId;
    private int computerId;
    private String username;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean isActive;
    private double HourlyRate;

    public Session(int sessionsId, int computerId, String
            username, double hourlyRate) {

        this.sessionId = sessionsId;
        this.computerId = computerId;
        this.username = username;
        this.HourlyRate = hourlyRate;
        this.startTime = LocalDateTime.now();
        this.isActive = true;
    }
    Session(int sessionsId, int computerId, String username,
            double HourlyRate,
            LocalDateTime StartTime, LocalDateTime EndTime,
            boolean isActive) {

        this.sessionId = sessionsId;
        this.computerId = computerId;
        this.username = username;
        this.HourlyRate = HourlyRate;
        this.startTime = StartTime;
        this.endTime = EndTime;
        this.isActive = isActive;
    }
    public void endSession() {
        this.endTime = LocalDateTime.now();
        this.isActive = false;
    }

    public long getDurationMinutes() {
        LocalDateTime end = (endTime != null) ? endTime : LocalDateTime.now();
        return Duration.between(startTime, end).toMinutes();
    }

    public double calculateCost() {
        return (getDurationMinutes() / 60.0) * HourlyRate;
    }
    public int getSessionId() {
        return sessionId;
    }
    public int getComputerId() {
        return computerId;
    }
    public String getUsername() {
        return username;
    }
    public LocalDateTime getStartTime() {
        return startTime;
    }
    public LocalDateTime getEndTime() {
        return endTime;
    }
    public boolean isActive() {
        return isActive;
    }
    public double getHourlyRate() {
        return HourlyRate;
    }
}
