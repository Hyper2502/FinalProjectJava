package com.example.finalproject2026.Controller;

import com.example.finalproject2026.manager.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.example.finalproject2026.manager.ServiceLocator;
import com.example.finalproject2026.manager.ReportManager;
import com.example.finalproject2026.manager.SessionManager;
import com.example.finalproject2026.model.Session;

import java.util.List;
import javafx.event.ActionEvent;

public class ReportsController {

    @FXML private TextArea revenueReportArea;
    @FXML private TextArea usageReportArea;
    @FXML private DatePicker datePicker;
    @FXML private Label totalSessionsLabel;
    @FXML private Label totalRevenueLabel;

    private ReportManager reportManager;
    private SessionManager sessionManager;

    @FXML
    public void initialize() {
        reportManager = ServiceLocator.getReportManager();
        sessionManager = ServiceLocator.getSessionManager();
    }

    @FXML
    private void handleGenerateReports() {
        List<Session> allSessions = sessionManager.getAllSessions();

        // Revenue Report
        String revenueReport = reportManager.generateRevenueReport(allSessions);
        revenueReportArea.setText(revenueReport);

        // Usage Report
        String usageReport = reportManager.generateUsageReport(allSessions);
        usageReportArea.setText(usageReport);

        // Update summary labels
        updateSummaryLabels(allSessions);
    }

    @FXML
    private void handleDailySummary() {
        if (datePicker.getValue() == null) {
            revenueReportArea.setText("Please select a date first!");
            return;
        }

        String date = datePicker.getValue().toString();
        List<Session> allSessions = sessionManager.getAllSessions();

        String summary = reportManager.generateDailySummary(allSessions, date);
        revenueReportArea.setText(summary);
    }

    private void updateSummaryLabels(List<Session> sessions) {
        int total = sessions.size();
        double revenue = sessions.stream()
                .filter(s -> !s.isActive())
                .mapToDouble(Session::getTotalCost)
                .sum();

        totalSessionsLabel.setText(String.valueOf(total));
        totalRevenueLabel.setText(String.format("$%.2f", revenue));
    }

    @FXML
    private void handleDashboardMenu(ActionEvent event) {
        SceneManager.switchToDashboard(event);
    }

    @FXML
    private void handleDevicesMenu(ActionEvent event) {
        SceneManager.switchToDevices(event);
    }

    @FXML
    private void handleSessionsMenu(ActionEvent event) {
        SceneManager.switchToSessions(event);
    }

    @FXML
    private void handleBillingMenu(ActionEvent event) {
        SceneManager.switchToReports(event);
    }

    @FXML
    private void handleReportsMenu(ActionEvent event) {
    }
}