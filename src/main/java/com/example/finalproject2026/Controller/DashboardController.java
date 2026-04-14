package com.example.finalproject2026.Controller;

import com.example.finalproject2026.manager.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import com.example.finalproject2026.model.WorkStation;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.finalproject2026.manager.ServiceLocator.getReportManager;

public class DashboardController implements Initializable {

    // FXML Injections - Stats
    @FXML private Label activeCountLabel;
    @FXML private Label maintenanceCountLabel;
    @FXML private Label brokenCountLabel;

    // FXML Injections - Billing
    @FXML private Label todayRevenueLabel;
    @FXML private Label monthRevenueLabel;

    // FXML Injections - Buttons
    @FXML private Button startSessionBtn;
    @FXML private Button endSessionBtn;
    @FXML private Button reportIssueBtn;
    @FXML private Button createDeviceBtn;

    // Managers
    private WorkstationManager workstationManager;
    private BillingManager billingManager;
    private Timer refreshTimer;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        workstationManager = new WorkstationManager();
        billingManager = new BillingManager();

        updateDashboard();
        setupButtonHandlers();
        startAutoRefresh();
    }

    // ==================== DATA UPDATE ====================

    private void updateDashboard() {
        updateStats();
        updateBilling();
    }

    private void updateStats() {
        Platform.runLater(() -> {
            List<WorkStation> all = workstationManager.getAllWorkstations();

            long active = all.stream()
                    .filter(w -> w.isAvailable() && !w.isBroken())
                    .count();

            long maintenance = all.stream()
                    .filter(w -> !w.isAvailable() && !w.isBroken())
                    .count();

            long broken = all.stream()
                    .filter(w -> w.isBroken())
                    .count();

            activeCountLabel.setText(String.valueOf(active));
            maintenanceCountLabel.setText(String.valueOf(maintenance));
            brokenCountLabel.setText(String.valueOf(broken));
        });
    }

    private void updateBilling() {
        Platform.runLater(() -> {
            double todayRevenue = calculateMonthRevenue();
            double monthlyRevenue = calculateMonthRevenue();

            todayRevenueLabel.setText(String.format("$%.2f", todayRevenue));
            monthRevenueLabel.setText(String.format("$%.2f", monthlyRevenue));
        });
    }

    private double calculateTodayRevenue() {
        var sessionManager = ServiceLocator.getSessionManager();
        return sessionManager.getAllSessions().stream()
                .filter(s -> !s.isActive())
                .mapToDouble(s -> s.calculateCost())
                .sum();
    }

    private double calculateMonthRevenue() {
        var sessionManager = ServiceLocator.getSessionManager();
        return sessionManager.getAllSessions().stream()
                .filter(s -> !s.isActive())
                .mapToDouble(s -> s.calculateCost())
                .sum();
    }

    // ==================== BUTTON HANDLERS ====================

    private void setupButtonHandlers() {
        startSessionBtn.setOnAction(e -> handleStartSession());
        endSessionBtn.setOnAction(e -> handleEndSession());
        reportIssueBtn.setOnAction(e -> handleReportIssue());
        createDeviceBtn.setOnAction(e -> handleCreateDevice());
    }

    @FXML
    private void handleStartSession() {
        System.out.println("Start Session clicked");
        showNotImplemented("Start Session");
    }

    @FXML
    private void handleEndSession() {
        System.out.println("End Session clicked");
        showNotImplemented("End Session");
    }

    @FXML
    private void handleReportIssue() {
        System.out.println("Report Issue clicked");
        showNotImplemented("Report Issue");
    }

    @FXML
    private void handleCreateDevice() {
        System.out.println("Create Device clicked");
        showNotImplemented("Create Device");
    }

    // ==================== MENU BAR HANDLERS ====================

    @FXML
    private void handleMenuRefresh() {
        System.out.println("Menu: Refresh");
        updateDashboard();
    }

    @FXML
    private void handleMenuExit() {
        System.out.println("Menu: Exit");
        Platform.exit();
    }

    @FXML
    private void handleMenuDevices() {
        System.out.println("Menu: View All Devices");
        showNotImplemented("View All Devices");
    }

    @FXML
    private void handleMenuAddDevice() {
        System.out.println("Menu: Add New Device");

    }

    @FXML
    private void handleMenuActiveSessions() {
        System.out.println("Menu: Active Sessions");

    }

    @FXML
    private void handleMenuSessionHistory() {
        System.out.println("Menu: Session History");

    }

    @FXML
    private void handleMenuTodayRevenue() {
        System.out.println("Menu: Today's Revenue");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Today's Revenue");
        alert.setHeaderText(null);
        alert.setContentText("Today's revenue: " + todayRevenueLabel.getText());
        alert.showAndWait();
    }

    @FXML
    private void handleMenuMonthlyReport() {
        System.out.println("Menu: Monthly Report");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Monthly Revenue");
        alert.setHeaderText(null);
        alert.setContentText("Monthly revenue: " + monthRevenueLabel.getText());
        alert.showAndWait();
    }

    @FXML
    private void handleMenuGenerateReport() {
        System.out.println("Menu: Generate Report");
        showNotImplemented("Generate Report");
    }

    // ==================== UTILITIES ====================

    private void showNotImplemented(String feature) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Feature Coming Soon");
        alert.setHeaderText(null);
        alert.setContentText(feature + " will be implemented in the next update!");
        alert.showAndWait();
    }

    private void startAutoRefresh() {
        refreshTimer = new Timer(true);
        refreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateDashboard();
            }
        }, 30000, 30000);
    }

    public void stop() {
        if (refreshTimer != null) {
            refreshTimer.cancel();
        }
    }
    @FXML
    private void handleDashboardMenu() {
        // Already on dashboard, just refresh
        updateDashboard();
    }

    @FXML
    private void handleDevicesMenu() {
        // Need to get the event - use a workaround
        SceneManager.switchToDevices(new ActionEvent(activeCountLabel, null));
    }

    @FXML
    private void handleSessionsMenu() {
        SceneManager.switchToSessions
                (new ActionEvent(activeCountLabel, null));
    }

    @FXML
    private void handleBillingMenu(ActionEvent event) {
        SceneManager.switchToReports(event);
    }
}






