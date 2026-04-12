package com.example.finalproject2026.model;

public class WorkStation {

    private int computerId;
    private String specs;
    private boolean isAvailable;
    private boolean isBroken;

    // 🔹 ADD THESE FIELDS
    private String name;        // For display: "PC1", "PC2", etc.
    private String status;      // "AVAILABLE", "IN_USE", "BROKEN", "MAINTENANCE"
    private double hourlyRate;  // For billing calculations

    // 🔹 UPDATED Constructor
    public WorkStation(int computerId, String specs, boolean isAvailable, boolean isBroken) {
        this.computerId = computerId;
        this.specs = specs;
        this.isAvailable = isAvailable;
        this.isBroken = isBroken;

        // 🔹 AUTO-SET THESE
        this.name = "PC" + computerId;
        this.hourlyRate = 2.00; // default rate

        // Determine status from availability/broken
        updateStatus();
    }

    // 🔹 ADD DEFAULT CONSTRUCTOR (needed for DAO)
    public WorkStation() {
        this.hourlyRate = 2.00;
    }

    // 🔹 ADD THIS METHOD
    public void updateStatus() {
        if (isBroken) {
            this.status = "BROKEN";
        } else if (!isAvailable) {
            this.status = "IN_USE";
        } else {
            this.status = "AVAILABLE";
        }
    }

    // 🔹 EXISTING GETTERS
    public int getComputerId() { return computerId; }
    public String getSpecs() { return specs; }
    public boolean isAvailable() { return isAvailable; }
    public boolean isBroken() { return isBroken; }

    // 🔹 ADD THESE GETTERS
    public String getName() { return name; }
    public String getStatus() {
        updateStatus(); // Auto-update before returning
        return status;
    }
    public double getHourlyRate() { return hourlyRate; }

    // 🔹 EXISTING SETTERS
    public void setSpecs(String specs) { this.specs = specs; }
    public void setAvailable(boolean available) {
        isAvailable = available;
        updateStatus();
    }
    public void setBroken(boolean broken) {
        isBroken = broken;
        updateStatus();
    }

    // 🔹 ADD THESE SETTERS
    public void setComputerId(int computerId) {
        this.computerId = computerId;
        this.name = "PC" + computerId; // Auto-update name
    }
    public void setName(String name) { this.name = name; }
    public void setStatus(String status) { this.status = status; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }
}