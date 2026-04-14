package com.example.finalproject2026.Controller;

import com.example.finalproject2026.manager.*;
import com.example.finalproject2026.model.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class BillingController implements Initializable {


    @FXML private TableView<Session> completedSessionsTable;
    @FXML private TableColumn<Session, Integer> sessionIdCol;
    @FXML private TableColumn<Session, Integer> computerCol;
    @FXML private TableColumn<Session, String> userCol;
    @FXML private TableColumn<Session, String> durationCol;
    @FXML private TableColumn<Session, Double> totalCostCol;


    @FXML private TextField sessionIdField;
    @FXML private TextArea receiptArea;


    @FXML private Label todayRevenueLabel;
    @FXML private Label monthRevenueLabel;
    @FXML private Label activeWorkstationsLabel;
    @FXML private Label totalWorkstationsLabel;

    private SessionManager sessionManager;
    private BillingManager billingManager;
    private WorkstationManager workstationManager;
    private ReportManager reportManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        sessionManager = new SessionManager();
        billingManager = new BillingManager();
        workstationManager = ServiceLocator.getWorkstationManager();
        reportManager = ServiceLocator.getReportManager();


        todayRevenueLabel.setText("$0.00");
        monthRevenueLabel.setText("$0.00");
        activeWorkstationsLabel.setText("0");
        totalWorkstationsLabel.setText("0");


        setupTableColumns();

        loadCompletedSessions();
        updateRevenueLabels();
        updateWorkstationStats();
    }

    private void setupTableColumns() {
        sessionIdCol.setCellValueFactory(new PropertyValueFactory<>("sessionId"));
        computerCol.setCellValueFactory(new PropertyValueFactory<>("computerId"));
        userCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        durationCol.setCellValueFactory(new PropertyValueFactory<>("durationFormatted"));
        totalCostCol.setCellValueFactory(new PropertyValueFactory<>("totalCost"));
    }

    @FXML
    private void handleGenerateReceipt() {
        try {
            int sessionId = Integer.parseInt(sessionIdField.getText().trim());
            Session session = findCompletedSession(sessionId);

            if (session != null) {  // FIXED: was checking == null before
                String receipt = billingManager.generateReceipt(session);
                int computerId = session.getComputerId();
                String workstationInfo = getWorkstationInfo(computerId);

                receiptArea.setText(receipt + "\n" + workstationInfo);
            } else {
                receiptArea.setText("Session not found or still active!");
            }
        } catch (NumberFormatException e) {
            receiptArea.setText("Please enter a valid session ID!");
        }
    }

    @FXML
    private void handleEndAndBill() {
        try {
            int sessionId = Integer.parseInt(sessionIdField.getText().trim());
            boolean ended = sessionManager.endSession(sessionId);

            if (ended) {
                Session session = findSession(sessionId);
                String receipt = billingManager.generateReceipt(session);

                List<Session> allSessions = sessionManager.getAllSessions();
                String revenueReport = reportManager.generateRevenueReport(allSessions);

                receiptArea.setText("SESSION ENDED SUCCESSFULLY!\n\n" +
                        receipt + "\n\n" +
                        "--- Revenue Impact ---\n" +
                        revenueReport);


                loadCompletedSessions();
                updateRevenueLabels();
                updateWorkstationStats();


                workstationManager.updateStatus(session.getComputerId(), "AVAILABLE");

            } else {
                receiptArea.setText("Failed to end session! It may not exist or already ended.");
            }
        } catch (NumberFormatException e) {
            receiptArea.setText("Please enter a valid session ID");
        }
    }

    @FXML
    private void handleGenerateRevenueReport() {
        List<Session> allSessions = sessionManager.getAllSessions();
        String revenueReport = reportManager.generateRevenueReport(allSessions);
        String usageReport = reportManager.generateUsageReport(allSessions);

        receiptArea.setText("=== FULL REVENUE REPORT ===\n\n" +
                revenueReport + "\n\n" +
                "=== WORKSTATION USAGE ===\n\n" +
                usageReport);
    }


    private Session findCompletedSession(int sessionId) {
        return sessionManager.getAllSessions().stream()
                .filter(s -> s.getSessionId() == sessionId && !s.isActive())
                .findFirst()
                .orElse(null);
    }

    private Session findSession(int sessionId) {
        return sessionManager.getAllSessions().stream()
                .filter(s -> s.getSessionId() == sessionId)
                .findFirst()
                .orElse(null);
    }

    private String getWorkstationInfo(int computerId) {
        var workstation = workstationManager.getAllWorkstations().stream()
                .filter(w -> w.getComputerId() == computerId)
                .findFirst()
                .orElse(null);

        if (workstation != null) {  // FIXED: was checking == null before
            return "--- Workstation Info ---\nComputer #" + computerId +
                    "\nStatus: " + workstation.getStatus() +
                    "\nSpecs: " + workstation.getSpecs();
        }
        return "";
    }

    // FIXED: Filter for COMPLETED sessions (not active)
    private void loadCompletedSessions() {
        List<Session> completed = sessionManager.getAllSessions().stream()
                .filter(s -> !s.isActive())
                .collect(Collectors.toList());

        completedSessionsTable.getItems().setAll(completed);
    }

    private void updateRevenueLabels() {
        double todayRevenue = calculateTodayRevenue();
        double monthRevenue = calculateMonthRevenue();

        todayRevenueLabel.setText(String.format("$%.2f", todayRevenue));
        monthRevenueLabel.setText(String.format("$%.2f", monthRevenue));
    }

    private void updateWorkstationStats() {
        int total = workstationManager.getAllWorkstations().size();
        int available = workstationManager.getAvailableCount();
        int inUse = total - available - workstationManager.getMaintenanceCount()
                - workstationManager.getBrokenCount();

        totalWorkstationsLabel.setText(String.valueOf(total));
        activeWorkstationsLabel.setText(String.valueOf(inUse));
    }

    private double calculateTodayRevenue() {
        return sessionManager.getAllSessions().stream()
                .filter(s -> !s.isActive())
                .mapToDouble(Session::getTotalCost)
                .sum();
    }

    private double calculateMonthRevenue() {
        return sessionManager.getAllSessions().stream()
                .filter(s -> !s.isActive())
                .mapToDouble(Session::getTotalCost)
                .sum();
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

    }

    @FXML
    private void handleReportsMenu(ActionEvent event) {
        SceneManager.switchToReports(event);  // FIXED: was switchToBilling
    }
}