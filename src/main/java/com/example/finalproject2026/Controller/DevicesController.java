package com.example.finalproject2026.Controller;

import com.example.finalproject2026.database.WorkstationDAO;
import com.example.finalproject2026.manager.SceneManager;
import com.example.finalproject2026.model.WorkStation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DevicesController implements Initializable {

    @FXML
    private Label computerNameLabel;
    @FXML
    private TextField specsField;  // Changed from computerNameField
    @FXML
    private CheckBox isBrokenCheckBox;
    @FXML
    private CheckBox isAvailableCheckBox;  // Added for visibility
    @FXML
    private VBox computerListVBox;
    @FXML
    private Label statusLabel;  // Added to show status

    private List<WorkStation> workstations;
    private WorkStation selectedWorkstation;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadWorkstations();
    }

    private void loadWorkstations() {
        computerListVBox.getChildren().clear();
        workstations = WorkstationDAO.getAll();

        for (WorkStation ws : workstations) {
            Button wsButton = createWorkstationButton(ws);
            computerListVBox.getChildren().add(wsButton);
        }
    }

    private Button createWorkstationButton(WorkStation ws) {
        Button btn = new Button(ws.getName() + " - " + ws.getStatus());
        btn.setPrefWidth(300);
        btn.setPrefHeight(50);

        // Style based on status
        String color;
        switch (ws.getStatus()) {
            case "BROKEN":
                color = "#ff6b6b";  // Red
                break;
            case "IN_USE":
                color = "#ffd93d";  // Yellow
                break;
            case "AVAILABLE":
                color = "#6bcf7f";  // Green
                break;
            default:
                color = "#cccccc";  // Gray
        }

        btn.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + (ws.getStatus().equals("IN_USE") ? "black" : "white") + ";"
        );

        btn.setOnAction(e -> selectWorkstation(ws));
        return btn;
    }

    private void selectWorkstation(WorkStation ws) {
        selectedWorkstation = ws;
        computerNameLabel.setText(ws.getName());
        specsField.setText(ws.getSpecs());
        isBrokenCheckBox.setSelected(ws.isBroken());
        isAvailableCheckBox.setSelected(ws.isAvailable());

        // Show status with color
        statusLabel.setText("Status: " + ws.getStatus());
        statusLabel.setTextFill(getStatusColor(ws.getStatus()));
    }

    private Color getStatusColor(String status) {
        switch (status) {
            case "BROKEN":
                return Color.RED;
            case "IN_USE":
                return Color.ORANGE;
            case "AVAILABLE":
                return Color.GREEN;
            default:
                return Color.GRAY;
        }
    }

    @FXML
    private void handleSaveComputer() {
        if (selectedWorkstation == null) return;

        // Update specs
        selectedWorkstation.setSpecs(specsField.getText());
        WorkstationDAO.updateSpecifications(selectedWorkstation.getComputerId(), specsField.getText());

        // Update broken status
        boolean wasBroken = selectedWorkstation.isBroken();
        selectedWorkstation.setBroken(isBrokenCheckBox.isSelected());
        WorkstationDAO.updateBroken(selectedWorkstation.getComputerId(), isBrokenCheckBox.isSelected());

        // Refresh list to show updated colors
        loadWorkstations();

        // Show confirmation
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Saved");
        alert.setHeaderText(null);
        alert.setContentText(" Computer updated successfully!");
        alert.showAndWait();
    }

    @FXML
    private void handleAddWorkstation() {
        // Create new workstation with default specs
        WorkStation newWs = WorkstationDAO.save("Standard PC - Intel i5, 16GB RAM, GTX 1660");
        loadWorkstations();

        // Select the new one
        selectWorkstation(newWs);
    }

    @FXML
    private void handleDeleteWorkstation() {
        if (selectedWorkstation == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete " + selectedWorkstation.getName() + "?");
        confirm.setContentText("This action cannot be undone.");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            WorkstationDAO.delete(selectedWorkstation.getComputerId());
            selectedWorkstation = null;
            computerNameLabel.setText("Select a Computer");
            specsField.clear();
            isBrokenCheckBox.setSelected(false);
            loadWorkstations();
        }
    }

    @FXML
    private void handleDashboardMenu() {
        // Already on dashboard, just refresh
        SceneManager.switchToDashboard(new ActionEvent());
    }

    @FXML
    private void handleDevicesMenu() {
        // Need to get the event - use a workaround
        SceneManager.switchToDevices(new ActionEvent());
    }

    @FXML
    private void handleSessionsMenu() {
        SceneManager.switchToDevices(new ActionEvent());
    }


    @FXML
    private void handleBillingMenu(ActionEvent event) {
        SceneManager.switchToReports(event);
    }
}