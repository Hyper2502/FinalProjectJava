package com.example.finalproject2026;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            // Print error to console so we can see it
            System.err.println("ERROR loading FXML: " + e.getMessage());
            e.printStackTrace();
            // Don't exit - let us see the error
        }
    }

    public static void main(String[] args){
        launch(args);
    }
}