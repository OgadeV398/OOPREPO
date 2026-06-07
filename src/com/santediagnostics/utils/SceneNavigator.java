package com.santediagnostics.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class SceneNavigator {

    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static void navigateTo(String fxmlPath, String title) {

        try {

            URL resource = SceneNavigator.class.getResource(fxmlPath);

            System.out.println("Attempting to load: " + fxmlPath);
            System.out.println("Resource found: " + resource);

            if (resource == null) {
                throw new IOException("FXML file not found: " + fxmlPath);
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            Scene scene = new Scene(root);

            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {

            System.out.println("Navigation Error");
            System.out.println("FXML Path: " + fxmlPath);
            e.printStackTrace();

        }
    }

    public static FXMLLoader getLoader(String fxmlPath) {

        URL resource = SceneNavigator.class.getResource(fxmlPath);

        if (resource == null) {
            throw new RuntimeException(
                "FXML file not found: " + fxmlPath
            );
        }

        return new FXMLLoader(resource);
    }
}