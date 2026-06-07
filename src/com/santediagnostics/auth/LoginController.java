package com.santediagnostics.auth;

import com.santediagnostics.db.DBConnection;
import com.santediagnostics.models.User;
import com.santediagnostics.utils.AuditLogger;
import com.santediagnostics.utils.SceneNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private void handleLogin() {

        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter email and password.");
            return;
        }

        User user = getUserByEmail(email);

        if (user == null) {
            errorLabel.setText("Account not found.");
            return;
        }

        boolean validPassword = BCrypt.checkpw(password, user.getPasswordHash());

        if (!validPassword) {
            errorLabel.setText("Incorrect password.");
            return;
        }

        SessionManager.getInstance().setCurrentUser(user);

        AuditLogger.log(user.getId(), "User logged in");

        System.out.println("Logged in as: " + user.getRole());

        if (user.isFirstLogin()) {
            // Make sure change-password.fxml exists under resources/fxml/
            SceneNavigator.navigateTo("/fxml/change-password.fxml", "Change Password");
            return;
        }

        switch (user.getRole()) {

            case "SUPER_ADMIN":
                // Assuming admin dashboard is at resources/fxml/admin/admin-dashboard.fxml
                SceneNavigator.navigateTo("/fxml/admin/admin-dashboard.fxml", "Super Admin Dashboard");
                break;

            case "LAB_ATTENDANT":
                // Correct path based on your file system
                SceneNavigator.navigateTo("/fxml/attendant/attendant-dashboard.fxml", "Lab Attendant Dashboard");
                break;

            case "CUSTOMER":
                // If customer dashboard exists, use /fxml/customer/customer-dashboard.fxml
                SceneNavigator.navigateTo("/fxml/customer/customer-dashboard.fxml", "Customer Dashboard");
                break;

            default:
                errorLabel.setText("Unknown role: " + user.getRole());
        }
    }

    private User getUserByEmail(String email) {

        String sql = "SELECT * FROM users WHERE email = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setPasswordHash(rs.getString("password_hash"));
                user.setRole(rs.getString("role"));
                user.setFirstLogin(rs.getBoolean("is_first_login"));
                user.setVerified(rs.getBoolean("is_verified"));
                return user;
            }
        } catch (SQLException e) {
            System.out.println("Login Error: " + e.getMessage());
        }
        return null;
    }
}