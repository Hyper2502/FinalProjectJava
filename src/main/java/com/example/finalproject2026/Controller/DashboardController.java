package com.example.finalproject2026.Controller;

import com.example.finalproject2026.database.UserDAO;
import com.example.finalproject2026.database.WorkstationDAO;
import com.example.finalproject2026.manager.*;
import com.example.finalproject2026.model.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;

import com.example.finalproject2026.model.WorkStation;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

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
    @FXML private Button deleteUserBtn;
    @FXML private Button deleteDeviceBtn;
    @FXML private Button createUserBtn;
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
        deleteUserBtn.setOnAction(e -> handleDeleteUser());
        deleteDeviceBtn.setOnAction(e -> handleDeleteDevice());
        createUserBtn.setOnAction(e -> handleCreateUser());
        createDeviceBtn.setOnAction(e -> handleCreateDevice());
    }
    // ==================== DELETE USER DIALOG ====================

    @FXML
    private void handleDeleteUser() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(activeCountLabel.getScene().getWindow());
        dialog.setTitle("Delete User");

        Label usernameLabel = new Label("Username *");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username to delete");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");
        errorLabel.setVisible(false);

        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");
        deleteBtn.setDefaultButton(true);
        Button cancelBtn = new Button("Cancel");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        grid.add(usernameLabel, 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(errorLabel, 0, 1, 2, 1);

        HBox buttonBox = new HBox(10, cancelBtn, deleteBtn);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        grid.add(buttonBox, 1, 2);

        deleteBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();

            if (username.isEmpty()) {
                errorLabel.setText("Username is required");
                errorLabel.setVisible(true);
                return;
            }

            // Confirm deletion
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Deletion");
            confirm.setHeaderText("Delete User");
            confirm.setContentText("Are you sure you want to delete user '" + username + "'?");

            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    boolean deleted = UserDAO.delete(username);

                    Alert result = new Alert(deleted ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
                    result.setTitle(deleted ? "Success" : "Error");
                    result.setHeaderText(null);
                    result.setContentText(deleted
                            ? "User '" + username + "' deleted successfully!"
                            : "User '" + username + "' not found or could not be deleted.");
                    result.showAndWait();

                    if (deleted) dialog.close();
                }
            });
        });

        cancelBtn.setOnAction(e -> dialog.close());

        Scene scene = new Scene(grid);
        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.showAndWait();
    }

// ==================== DELETE DEVICE DIALOG ====================

    @FXML
    private void handleDeleteDevice() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(activeCountLabel.getScene().getWindow());
        dialog.setTitle("Delete Device");

        Label idLabel = new Label("Computer ID *");
        TextField idField = new TextField();
        idField.setPromptText("Enter computer ID to delete");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");
        errorLabel.setVisible(false);

        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");
        deleteBtn.setDefaultButton(true);
        Button cancelBtn = new Button("Cancel");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        grid.add(idLabel, 0, 0);
        grid.add(idField, 1, 0);
        grid.add(errorLabel, 0, 1, 2, 1);

        HBox buttonBox = new HBox(10, cancelBtn, deleteBtn);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        grid.add(buttonBox, 1, 2);

        
        deleteBtn.setOnAction(e -> {
            String input = idField.getText().trim();

            if (input.isEmpty()) {
                errorLabel.setText("Computer ID is required");
                errorLabel.setVisible(true);
                return;
            }

            int computerId;
            try {
                computerId = Integer.parseInt(input);
            }catch (NumberFormatException ex){
                errorLabel.setText("Computer ID must be an integer");
                errorLabel.setVisible(true);
                return;
            }

            // Confirm deletion
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Deletion");
            confirm.setHeaderText("Delete Device");
            confirm.setContentText("Are you sure you want to delete device '" + computerId + "'?");

            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    boolean deleted = WorkstationDAO.delete(computerId);

                    Alert result = new Alert(deleted ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
                    result.setTitle(deleted ? "Success" : "Error");
                    result.setHeaderText(null);
                    result.setContentText(deleted
                            ? "Device '" + computerId + "' deleted successfully!"
                            : "Device '" + computerId + "' not found or could not be deleted.");
                    result.showAndWait();

                    if (deleted) {
                        dialog.close();
                        updateDashboard(); // Refresh stats
                    }
                }
            });
        });

        cancelBtn.setOnAction(e -> dialog.close());

        Scene scene = new Scene(grid);
        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.showAndWait();
    }

    @FXML
    private void handleCreateUser() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(activeCountLabel.getScene().getWindow());
        dialog.setTitle("Create User");

        Label nameLabel = new Label("Name *");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter full name");

        Label emailLabel = new Label("Email");
        TextField emailField = new TextField();
        emailField.setPromptText("optional@example.com");

        Label passwordLabel = new Label("Password *");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Required password");

        Label adminLabel = new Label("Admin");
        CheckBox adminCheckBox = new CheckBox("Grant admin privileges");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");
        errorLabel.setVisible(false);

        Button createBtn = new Button("Create");
        createBtn.setDefaultButton(true);
        Button cancelBtn = new Button("Cancel");


        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(emailLabel, 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(passwordLabel, 0, 2);
        grid.add(passwordField, 1, 2);
        grid.add(adminLabel, 0, 3);
        grid.add(adminCheckBox, 1, 3);
        grid.add(errorLabel, 0, 4, 2, 1);

        HBox buttonBox = new HBox(10, cancelBtn, createBtn);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        grid.add(buttonBox, 1, 5);

        cancelBtn.setOnAction(e -> dialog.close());

        createBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText();

            // Validation
            if (name.isEmpty()) {
                errorLabel.setText("Name is required");
                errorLabel.setVisible(true);
                return;
            }
            if (password.isEmpty()) {
                errorLabel.setText("Password is required");
                errorLabel.setVisible(true);
                return;
            }

            User newUser = new User(name, password, email.isEmpty() ? null: email, adminCheckBox.isSelected());

            UserDAO.save(newUser.getUsername(), newUser.getEmail(), newUser.getPassword(), newUser.getAdmin());

            Alert success = new Alert(Alert.AlertType.INFORMATION);

            success.setTitle("Success");
            success.setHeaderText(null);
            success.setContentText("User '" + name + "' created successfully!");
            success.showAndWait();

            dialog.close();




        });

        Scene scene = new Scene(grid);
        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.showAndWait();

    }

    @FXML
    private void handleCreateDevice() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(activeCountLabel.getScene().getWindow());
        dialog.setTitle("Create Device");

        Label description = new Label("Description");
        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Enter Specs for the Device");




        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");
        errorLabel.setVisible(false);

        Button createBtn = new Button("Create");
        createBtn.setDefaultButton(true);
        Button cancelBtn = new Button("Cancel");


        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));


        grid.add(description, 0, 1);
        grid.add(descriptionField, 1, 1);
        grid.add(errorLabel, 0, 4, 2, 1);

        HBox buttonBox = new HBox(10, cancelBtn, createBtn);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        grid.add(buttonBox, 1, 5);

        cancelBtn.setOnAction(e -> dialog.close());

        createBtn.setOnAction(e -> {

            String specs = descriptionField.getText().trim();

            // Validation
            if (specs.isEmpty()) {
                errorLabel.setText("Password is required");
                errorLabel.setVisible(true);
                return;
            }

            WorkstationDAO.save(specs);



            Alert success = new Alert(Alert.AlertType.INFORMATION);

            success.setTitle("Success");
            success.setHeaderText(null);
            success.setContentText("Device created successfully!");
            success.showAndWait();

            dialog.close();




        });

        Scene scene = new Scene(grid);
        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.showAndWait();
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
    private void handleReportsMenu(ActionEvent event) {
        SceneManager.switchToReports(event);
    }

    @FXML
    private void handleBillingMenu(ActionEvent event) {
        SceneManager.switchToReports(event);
    }
}







