package com.santediagnostics.auth;

import com.santediagnostics.db.DBConnection;
import com.santediagnostics.models.User;
import com.santediagnostics.utils.SceneNavigator;
import com.santediagnostics.utils.AuditLogger;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both email and password.");
            return;
        }

        User user = getUserByEmail(email);

        if (user == null) {
            errorLabel.setText("No account found with that email.");
            return;
        }

        boolean verified = BCrypt.checkpw(password, user.getPasswordHash());

        if (!verified) {
            errorLabel.setText("Incorrect password. Please try again.");
            return;
        }

        // Set current session
        SessionManager.getInstance().setCurrentUser(user);

        // Log the login action
        AuditLogger.log(user.getId(), "User logged in");

        // Force password change on first login
        if (user.isFirstLogin()) {
            SceneNavigator.navigateTo("/fxml/change-password.fxml", "Change Password");
            return;
        }

        // Navigate based on role
        switch (user.getRole()) {
            case "SUPER_ADMIN":
                SceneNavigator.navigateTo("/fxml/admin/admin-dashboard.fxml", "Super Admin Dashboard");
                break;
            case "LAB_ATTENDANT":
                SceneNavigator.navigateTo("/fxml/attendant/attendant-dashboard.fxml", "Lab Attendant Dashboard");
                break;
            case "CUSTOMER":
                SceneNavigator.navigateTo("/fxml/customer/customer-dashboard.fxml", "Customer Dashboard");
                break;
            default:
                errorLabel.setText("Unknown role. Contact administrator.");
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
            System.out.println("Login query error: " + e.getMessage());
        }
        return null;
    }
}