package com.santediagnostics.controllers.attendant;

import com.santediagnostics.auth.SessionManager;
import com.santediagnostics.db.DBConnection;
import com.santediagnostics.utils.AuditLogger;
import com.santediagnostics.utils.SceneNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;

public class CreateCustomerController {

    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    @FXML
    private void handleCreate() {
        String name = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("All fields required.");
            return;
        }
        if (password.length() < 6) {
            messageLabel.setText("Password must be at least 6 chars.");
            return;
        }

        String hash = BCrypt.hashpw(password, BCrypt.gensalt());
        String sql = "INSERT INTO users (full_name, email, password_hash, role, is_first_login, is_verified) " +
                     "VALUES (?, ?, ?, 'CUSTOMER', TRUE, TRUE)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, hash);
            pstmt.executeUpdate();

            AuditLogger.log(SessionManager.getInstance().getCurrentUser().getId(),
                            "Created customer account for " + email, "users", 0);
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("Customer created successfully!");
            fullNameField.clear();
            emailField.clear();
            passwordField.clear();
        } catch (SQLException e) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML private void handleBack() {
        SceneNavigator.navigateTo("/fxml/attendant/attendant-dashboard.fxml", "Dashboard");
    }
}