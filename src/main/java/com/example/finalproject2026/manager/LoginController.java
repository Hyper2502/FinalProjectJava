package com.example.finalproject2026.manager;

import com.example.finalproject2026.database.UserDAO;
import com.example.finalproject2026.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import org.mindrot.jbcrypt.BCrypt;

public class LoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        User user = UserDAO.lookByUsername(username);

        // Fix: Added missing opening brace and corrected method calls
        if (user != null && BCrypt.checkpw(password, user.getPassword()) && user.getAdmin()) {
            System.out.println("Login Successfully!");
            SceneManager.switchToDashboard(event);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Failed");
            alert.setHeaderText("Invalid Credentials");
            alert.setContentText("Username or password is incorrect");
            alert.showAndWait();
        }
    }
}