package com.example.finalproject2026.manager;

public class ServiceLocator {
    private static final SessionManager sessionManager = new SessionManager();
    private static final BillingManager billingManager = new BillingManager();
    private static final ReportManager reportManager = new ReportManager();
    private static final WorkstationManager workstationManager = new WorkstationManager();

    public static SessionManager getSessionManager() {
        return sessionManager;
    }
    public static BillingManager getBillingManager() {
        return billingManager;
    }
    public static ReportManager getReportManager() {
        return reportManager;
    }
    public static WorkstationManager getWorkstationManager() {
        return workstationManager;
    }
}
