package model;

public class WorkStation {
    private int computerId;
    private String specs;
    private boolean isAvailable;
    private boolean isBroken;

    public WorkStation(int computerId, String specs) {
        this.computerId = computerId;
        this.specs = specs;
        this.isAvailable = true;
        this.isBroken = false;
    }

    public void setInUse(Boolean inUse){
        this.isAvailable = !inUse;
    }

    public int getComputerId() {
        return computerId;
    }
    public boolean isAvailable() {
        return isAvailable && !isBroken;
    }
}
