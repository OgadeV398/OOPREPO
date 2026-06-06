package com.santediagnostics;

import com.santediagnostics.utils.SceneNavigator;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        SceneNavigator.setPrimaryStage(primaryStage);
        SceneNavigator.navigateTo("/fxml/login.fxml", "Sante Diagnostics - Login");
    }

    public static void main(String[] args) {
        launch(args);
    }
}