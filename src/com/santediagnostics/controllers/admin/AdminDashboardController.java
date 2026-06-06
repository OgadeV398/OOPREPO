package com.santediagnostics.controllers.admin;

import com.santediagnostics.auth.SessionManager;
import com.santediagnostics.db.DBConnection;
import com.santediagnostics.utils.SceneNavigator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import java.io.IOException;
import java.sql.*;

public class AdminDashboardController {

    @FXML private Label adminNameLabel;
    @FXML private Label totalTestsLabel;
    @FXML private Label pendingRequestsLabel;
    @FXML private Label unpaidRequestsLabel;
    @FXML private Label totalUsersLabel;
    @FXML private StackPane contentArea;

    @FXML
    public void initialize() {
        adminNameLabel.setText(SessionManager.getInstance().getCurrentUser().getFullName());
        loadStats();
    }

    private void loadStats() {
        try {
            Connection conn = DBConnection.getConnection();

            ResultSet rs1 = conn.createStatement()
                .executeQuery("SELECT COUNT(*) FROM test_types");
            if (rs1.next()) totalTestsLabel.setText(String.valueOf(rs1.getInt(1)));

            ResultSet rs2 = conn.createStatement()
                .executeQuery("SELECT COUNT(*) FROM test_requests WHERE status != 'READY'");
            if (rs2.next()) pendingRequestsLabel.setText(String.valueOf(rs2.getInt(1)));

            ResultSet rs3 = conn.createStatement()
                .executeQuery("SELECT COUNT(*) FROM test_requests WHERE payment_status = 'UNPAID'");
            if (rs3.next()) unpaidRequestsLabel.setText(String.valueOf(rs3.getInt(1)));

            ResultSet rs4 = conn.createStatement()
                .executeQuery("SELECT COUNT(*) FROM users");
            if (rs4.next()) totalUsersLabel.setText(String.valueOf(rs4.getInt(1)));

        } catch (SQLException e) {
            System.out.println("Stats error: " + e.getMessage());
        }
    }

    private void loadContent(String fxmlPath) {
        try {
            Parent content = FXMLLoader.load(
                getClass().getResource(fxmlPath)
            );
            contentArea.getChildren().setAll(content);
        } catch (IOException e) {
            System.out.println("Load error: " + e.getMessage());
        }
    }

    @FXML private void showDashboard() { loadStats(); }
    @FXML private void showTestBuilder() { loadContent("/fxml/admin/test-builder.fxml"); }
    @FXML private void showRequestQueue() { loadContent("/fxml/admin/request-queue.fxml"); }
    @FXML private void showAuditLog() { loadContent("/fxml/admin/audit-log.fxml"); }
    @FXML private void showCreateAccount() { loadContent("/fxml/admin/create-account.fxml"); }

    @FXML
    private void handleLogout() {
        SessionManager.getInstance().logout();
        SceneNavigator.navigateTo("/fxml/login.fxml", "Sante Diagnostics - Login");
    }
}