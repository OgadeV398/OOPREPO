package com.santediagnostics.controllers.attendant;

import com.santediagnostics.auth.SessionManager;
import com.santediagnostics.utils.SceneNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AttendantDashboardController {

    @FXML private Label welcomeLabel;

    @FXML
    public void initialize() {
        String name = SessionManager.getInstance().getCurrentUser().getFullName();
        welcomeLabel.setText("Welcome, " + name);
    }

    @FXML private void handleSampleTracking() {
        SceneNavigator.navigateTo("/fxml/attendant/sample-tracking.fxml", "Sample Tracking");
    }

    @FXML private void handleResultUpload() {
        SceneNavigator.navigateTo("/fxml/attendant/result-upload.fxml", "Upload Result");
    }

    @FXML private void handleCreateCustomer() {
        SceneNavigator.navigateTo("/fxml/attendant/create-customer.fxml", "Create Customer");
    }

    @FXML private void handleLogout() {
        SessionManager.getInstance().logout();
        SceneNavigator.navigateTo("/fxml/login.fxml", "Login");
    }
}