package com.santediagnostics.controllers.admin;

import com.santediagnostics.auth.SessionManager;
import com.santediagnostics.db.DBConnection;
import com.santediagnostics.utils.AuditLogger;
import org.mindrot.jbcrypt.BCrypt;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;

public class CreateAccountController {

    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleBox;
    @FXML private Label messageLabel;
    @FXML private TableView<ObservableList<String>> accountTable;
    @FXML private TableColumn<ObservableList<String>, String> colName;
    @FXML private TableColumn<ObservableList<String>, String> colEmail;
    @FXML private TableColumn<ObservableList<String>, String> colRole;
    @FXML private TableColumn<ObservableList<String>, String> colCreated;

    private ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        roleBox.setItems(FXCollections.observableArrayList("LAB_ATTENDANT", "CUSTOMER"));

        colName.setCellValueFactory(row -> new SimpleStringProperty(row.getValue().get(0)));
        colEmail.setCellValueFactory(row -> new SimpleStringProperty(row.getValue().get(1)));
        colRole.setCellValueFactory(row -> new SimpleStringProperty(row.getValue().get(2)));
        colCreated.setCellValueFactory(row -> new SimpleStringProperty(row.getValue().get(3)));

        accountTable.setItems(data);
        loadAccounts();
    }

    @FXML
    private void handleCreateAccount() {
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String role = roleBox.getValue();

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || role == null) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("All fields are required.");
            return;
        }

        if (!email.contains("@")) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Please enter a valid email address.");
            return;
        }

        if (password.length() < 6) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Password must be at least 6 characters.");
            return;
        }

        // Hash the password
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        String sql = "INSERT INTO users (full_name, email, password_hash, role, " +
                     "is_first_login, is_verified) VALUES (?, ?, ?, ?, TRUE, TRUE)";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, fullName);
            stmt.setString(2, email);
            stmt.setString(3, hashedPassword);
            stmt.setString(4, role);
            stmt.executeUpdate();

            AuditLogger.log(
                SessionManager.getInstance().getCurrentUser().getId(),
                "Created " + role + " account for: " + email,
                "users", 0
            );

            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("Account created successfully for " + fullName);

            fullNameField.clear();
            emailField.clear();
            passwordField.clear();
            roleBox.setValue(null);
            loadAccounts();

        } catch (SQLException e) {
            if (e.getMessage().contains("duplicate") || e.getMessage().contains("unique")) {
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText("An account with that email already exists.");
            } else {
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText("Error creating account: " + e.getMessage());
            }
        }
    }

    private void loadAccounts() {
        data.clear();
        String sql = "SELECT full_name, email, role, created_at FROM users " +
                     "WHERE role != 'SUPER_ADMIN' ORDER BY created_at DESC";
        try {
            Connection conn = DBConnection.getConnection();
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                row.add(rs.getString("full_name"));
                row.add(rs.getString("email"));
                row.add(rs.getString("role"));
                row.add(rs.getString("created_at"));
                data.add(row);
            }
        } catch (SQLException e) {
            System.out.println("Load accounts error: " + e.getMessage());
        }
    }
}