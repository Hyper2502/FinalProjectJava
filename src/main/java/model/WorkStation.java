package model;

public class WorkStation {

    private int computerId;
    private String specs;
    private boolean isAvailable;
    private boolean isBroken;

    // 🔹 Constructor
    public WorkStation(int computerId, String specs, boolean isAvailable, boolean isBroken) {
        this.computerId = computerId;
        this.specs = specs;
        this.isAvailable = isAvailable;
        this.isBroken = isBroken;
    }

    // 🔹 Getters
    public int getComputerId() {
        return computerId;
    }

    public String getSpecs() {
        return specs;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public boolean isBroken() {
        return isBroken;
    }

    // 🔹 Setters (NO DATABASE CALLS HERE)
    public void setSpecs(String specs) {
        this.specs = specs;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public void setBroken(boolean broken) {
        isBroken = broken;
    }
}