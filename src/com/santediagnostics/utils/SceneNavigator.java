package com.santediagnostics.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class SceneNavigator {

    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static void navigateTo(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(
                SceneNavigator.class.getResource(fxmlPath)
            );
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            System.out.println("Navigation error: " + e.getMessage());
        }
    }

    public static FXMLLoader getLoader(String fxmlPath) {
        return new FXMLLoader(
            SceneNavigator.class.getResource(fxmlPath)
        );
    }
}