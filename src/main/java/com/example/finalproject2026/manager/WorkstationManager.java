package com.example.finalproject2026.manager;

import com.example.finalproject2026.database.WorkstationDAO;
import com.example.finalproject2026.model.WorkStation;
import java.util.List;

public class WorkstationManager {

    // Remove this - DAO methods are static
    // private WorkstationDAO workstationDAO;

    public WorkstationManager() {
        // Remove this - no need to instantiate
        // this.workstationDAO = new WorkstationDAO();
    }

    public List<WorkStation> getAllWorkstations() {
        // Call static method directly on class
        return WorkstationDAO.getAll();
    }

    public int getAvailableCount() {
        return (int) getAllWorkstations().stream()
                .filter(w -> "AVAILABLE".equals(w.getStatus()))
                .count();
    }

    public int getMaintenanceCount() {
        return (int) getAllWorkstations().stream()
                .filter(w -> "MAINTENANCE".equals(w.getStatus()))
                .count();
    }

    public int getBrokenCount() {
        return (int) getAllWorkstations().stream()
                .filter(w -> "BROKEN".equals(w.getStatus()))
                .count();
    }

    public void createWorkstation(String specs) {
        // Your DAO save only takes specs, returns WorkStation
        WorkstationDAO.save(specs);
    }

    public void updateStatus(int computerId, String status) {
        WorkStation ws = WorkstationDAO.getById(computerId);
        if (ws != null) {
            // Update based on status string
            switch (status) {
                case "AVAILABLE":
                    WorkstationDAO.updateAvailability(computerId, true);
                    WorkstationDAO.updateBroken(computerId, false);
                    break;
                case "IN_USE":
                    WorkstationDAO.updateAvailability(computerId, false);
                    break;
                case "BROKEN":
                    WorkstationDAO.updateBroken(computerId, true);
                    break;
                case "MAINTENANCE":
                    WorkstationDAO.updateAvailability(computerId, false);
                    break;
            }
        }
    }
}