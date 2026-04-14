package com.example.finalproject2026;

import com.example.finalproject2026.manager.SceneManager;
import com.example.finalproject2026.database.UserDAO;
import com.example.finalproject2026.database.WorkstationDAO;
import com.example.finalproject2026.manager.SessionManager;
import com.example.finalproject2026.model.Session;
import com.example.finalproject2026.model.WorkStation;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class SessionsController implements Initializable {

    @FXML private TableView<Session> activeSessionsTable;
    @FXML private TableColumn<Session, String> sessionIdCol;
    @FXML private TableColumn<Session, String> workstationCol;
    @FXML private TableColumn<Session, String> userCol;
    @FXML private TableColumn<Session, String> startTimeCol;
    @FXML private TableColumn<Session, String> durationCol;
    @FXML private TableColumn<Session, String> costCol;

    @FXML private ComboBox<String> userComboBox;
    @FXML private ComboBox<String> workstationComboBox;
    @FXML private TextField searchField;

    private SessionManager sessionManager;
    private ObservableList<Session> activeSessions;
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sessionManager = new SessionManager();
        activeSessions = FXCollections.observableArrayList();

        setupTableColumns();
        loadActiveSessions();
        loadUsers();
        loadAvailableWorkstations();

        // Auto-refresh every 30 seconds
        startAutoRefresh();
    }

    private void setupTableColumns() {
        sessionIdCol.setCellValueFactory(cell ->
                new SimpleStringProperty(String.valueOf(cell.getValue().getSessionId())));

        workstationCol.setCellValueFactory(cell ->
                new SimpleStringProperty("PC" + cell.getValue().getComputerId()));

        userCol.setCellValueFactory(cell -> {
            String username = cell.getValue().getUsername();
            return new SimpleStringProperty(username != null ? username : "User " + cell.getValue().getUserId());
        });

        startTimeCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getStartTime().format(timeFormatter)));

        durationCol.setCellValueFactory(cell -> {
            long minutes = cell.getValue().getDurationMinutes();
            return new SimpleStringProperty(String.format("%d min", minutes));
        });

        costCol.setCellValueFactory(cell ->
                new SimpleStringProperty(String.format("$%.2f", cell.getValue().calculateCost())));
    }

    private void loadActiveSessions() {
        activeSessions.clear();
        activeSessions.addAll(sessionManager.getSessions());
        activeSessionsTable.setItems(activeSessions);
    }

    private void loadUsers() {
        List<String> users = UserDAO.UsergetAll().stream()
                .map(u -> u.split(":")[1].trim().split(" ")[0]) // Extract username from "id: username - ..."
                .collect(Collectors.toList());
        userComboBox.setItems(FXCollections.observableArrayList(users));
    }

    private void loadAvailableWorkstations() {
        List<WorkStation> all = WorkstationDAO.getAll();
        List<String> available = all.stream()
                .filter(ws -> ws.isAvailable() && !ws.isBroken())
                .map(WorkStation::getName)
                .collect(Collectors.toList());
        workstationComboBox.setItems(FXCollections.observableArrayList(available));
    }

    private void startAutoRefresh() {
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(Duration.seconds(30), e -> {
                    loadActiveSessions();
                    loadAvailableWorkstations(); // Refresh available PCs
                })
        );
        timeline.setCycleCount(javafx.animation.Animation.INDEFINITE);
        timeline.play();
    }

    @FXML
    private void handleNewSession() {
        String selectedUser = userComboBox.getValue();
        String selectedWorkstation = workstationComboBox.getValue();

        if (selectedUser == null || selectedWorkstation == null) {
            showAlert(Alert.AlertType.WARNING, "Missing Selection",
                    "Please select both a user and a workstation.");
            return;
        }

        try {
            // Extract computer ID from "PC1", "PC2", etc.
            int computerId = Integer.parseInt(selectedWorkstation.replace("PC", ""));

            Session newSession = sessionManager.startSession(computerId, selectedUser);

            showAlert(Alert.AlertType.INFORMATION, "Session Started",
                    "Session " + newSession.getSessionId() + " started for " + selectedUser);

            loadActiveSessions();
            loadAvailableWorkstations(); // Refresh dropdown

        } catch (IllegalStateException e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid workstation selection.");
        }
    }

    @FXML
    private void handleEndSession() {
        Session selected = activeSessionsTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection",
                    "Please select a session to end.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("End Session");
        confirm.setHeaderText("End Session " + selected.getSessionId() + "?");
        confirm.setContentText(String.format("Current cost: $%.2f\n\nProceed?",
                selected.calculateCost()));

        if (confirm.showAndWait().get() == ButtonType.OK) {
            boolean ended = sessionManager.endSession(selected.getSessionId());

            if (ended) {
                showAlert(Alert.AlertType.INFORMATION, "Session Ended",
                        "Final cost: $" + String.format("%.2f", selected.calculateCost()));
                loadActiveSessions();
                loadAvailableWorkstations();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to end session.");
            }
        }
    }

    @FXML
    private void handleSearch() {
        String searchId = searchField.getText().trim();
        if (searchId.isEmpty()) {
            loadActiveSessions();
            return;
        }

        try {
            int id = Integer.parseInt(searchId);
            Session found = sessionManager.getAllSessions().stream()
                    .filter(s -> s.getSessionId() == id)
                    .findFirst()
                    .orElse(null);

            if (found != null) {
                activeSessions.clear();
                activeSessions.add(found);
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Not Found",
                        "No session found with ID: " + id);
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Invalid Input",
                    "Please enter a valid session ID number.");
        }
    }

    @FXML
    private void handleRefresh() {
        loadActiveSessions();
        loadAvailableWorkstations();
        loadUsers();
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
        // Already here
        handleRefresh();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}