package model;

import database.WorkstationDAO;

public class WorkStation {
    private int computerId;
    private String specs;
    private boolean isAvailable;
    private boolean isBroken;

    public void SetBroken(int computerId,Boolean isBroken){

        this.computerId = computerId;
        this.isBroken = isBroken;

        WorkstationDAO.updateBroken(computerId, isBroken);
    }
    public void SetSpecs(int computerId,String Specs){

        this.computerId = computerId;
        this.specs = Specs;

        WorkstationDAO.updateSpecifications(computerId, Specs);
    }
    public void setInUse(int computerId,Boolean inUse){

        this.computerId = computerId;

        WorkstationDAO.updateAvailability(computerId, inUse);
    }

    public void GetWorkstations() {
        WorkstationDAO.getAllWorkstation();
    }
    public void isAvailable() {
        WorkstationDAO.CheckAvailability();
    }

}
