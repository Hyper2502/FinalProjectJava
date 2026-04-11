package com.example.finalproject2026;


import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;

public class SceneManager {

    public static void switchToDashboard(ActionEvent event){
        try{
            Parent root = FXMLLoader.load(SceneManager.class.getResource("/fxml/main_screen.fxml"));
            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();

            stage.setScene(new Scene(root, 1200, 600));
            stage.setTitle("Dashboard - Cyber Cafe");
            stage.setMaximized(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void switchToLogin(ActionEvent event){
        try{
            Parent root = FXMLLoader.load(SceneManager.class.getResource("/fxml/login.fxml"));
            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();

            stage.setScene(new Scene(root, 1200, 600));
            stage.setTitle("Login Screen");
            stage.setMaximized(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
