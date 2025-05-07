package com.example.flowermanagementsystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class FlowerManagementSystemApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(FlowerManagementSystemApp.class.getResource("FlowerSignup.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 546, 670);
        stage.setTitle("🪷 Flower Management System - Sign Up");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}