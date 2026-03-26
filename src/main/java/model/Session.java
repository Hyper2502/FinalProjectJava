package model;

import java.time.LocalDateTime;
import java.time.Duration;

public class Session {

    private int sessionsId;
    private int computerId;
    private String username;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean isActive;
    private double HourlyRate;

    public Session(int sessionsId, int computerId, String
            username,double hourlyRate) {

        this.sessionsId = sessionsId;
        this.computerId = computerId;
        this.username = username;
        this.HourlyRate = hourlyRate;
        this.startTime = LocalDateTime.now();

        this.isActive = true;
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
    public int getSessionsId() {
        return sessionsId;
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
}
