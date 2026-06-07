package com.santediagnostics.controllers.attendant;

import com.santediagnostics.auth.SessionManager;
import com.santediagnostics.db.DBConnection;
import com.santediagnostics.utils.AuditLogger;
import com.santediagnostics.utils.SceneNavigator;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;

public class ResultUploadController {

    @FXML private ComboBox<String> requestCombo;
    @FXML private Label customerLabel;
    @FXML private Label testTypeLabel;
    @FXML private TextArea resultArea;
    @FXML private CheckBox verifiedCheckbox;
    @FXML private Label messageLabel;

    private int selectedRequestId = -1;
    private int testTypeId = -1;

    @FXML
    public void initialize() {
        loadPendingRequests();
    }

    private void loadPendingRequests() {
        requestCombo.getItems().clear();
        String sql = "SELECT tr.id, u.full_name, tt.name " +
                     "FROM test_requests tr " +
                     "JOIN users u ON tr.customer_id = u.id " +
                     "JOIN test_types tt ON tr.test_type_id = tt.id " +
                     "WHERE tr.status = 'PENDING' " +
                     "AND tr.payment_status = 'PAID' " +   // only paid requests
                     "ORDER BY tr.id DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                requestCombo.getItems().add(rs.getInt("id") + " - " + rs.getString("full_name") + " (" + rs.getString("name") + ")");
            }
        } catch (SQLException e) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Error loading requests: " + e.getMessage());
        }
    }

    @FXML
    private void loadRequestDetails() {
        String selected = requestCombo.getValue();
        if (selected == null || selected.isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Select a request first.");
            return;
        }
        selectedRequestId = Integer.parseInt(selected.split(" - ")[0]);

        String sql = "SELECT u.full_name, tt.name, tt.id as test_type_id " +
                     "FROM test_requests tr " +
                     "JOIN users u ON tr.customer_id = u.id " +
                     "JOIN test_types tt ON tr.test_type_id = tt.id " +
                     "WHERE tr.id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, selectedRequestId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                customerLabel.setText(rs.getString("full_name"));
                testTypeLabel.setText(rs.getString("name"));
                testTypeId = rs.getInt("test_type_id");
                messageLabel.setText("");
            } else {
                customerLabel.setText("Not found");
                testTypeLabel.setText("Not found");
            }
        } catch (SQLException e) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpload() {
        if (selectedRequestId == -1) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Load request details first.");
            return;
        }
        String resultText = resultArea.getText().trim();
        if (resultText.isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Result cannot be empty.");
            return;
        }

        boolean verified = verifiedCheckbox.isSelected();

        try (Connection conn = DBConnection.getConnection()) {
            // Insert into results table
            String insertResult = "INSERT INTO results (request_id, result_text, is_verified, is_visible_to_customer, uploaded_at) " +
                                  "VALUES (?, ?, ?, ?, NOW())";
            PreparedStatement pstmt = conn.prepareStatement(insertResult);
            pstmt.setInt(1, selectedRequestId);
            pstmt.setString(2, resultText);
            pstmt.setBoolean(3, verified);
            pstmt.setBoolean(4, verified);  // if verified, visible to customer
            pstmt.executeUpdate();

            // Update test_requests status to 'READY'
            String updateReq = "UPDATE test_requests SET status = 'READY', ready_at = NOW() WHERE id = ?";
            PreparedStatement pstmt2 = conn.prepareStatement(updateReq);
            pstmt2.setInt(1, selectedRequestId);
            pstmt2.executeUpdate();

            // Update sample status to COMPLETED (optional)
            String updateSample = "UPDATE samples SET status = 'COMPLETED', updated_at = NOW() WHERE request_id = ?";
            PreparedStatement pstmt3 = conn.prepareStatement(updateSample);
            pstmt3.setInt(1, selectedRequestId);
            pstmt3.executeUpdate();

            AuditLogger.log(SessionManager.getInstance().getCurrentUser().getId(),
                            "Uploaded result for request #" + selectedRequestId + " (verified=" + verified + ")",
                            "results", selectedRequestId);

            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("Result uploaded successfully!");

            // Clear form
            resultArea.clear();
            verifiedCheckbox.setSelected(false);
            requestCombo.setValue(null);
            customerLabel.setText("");
            testTypeLabel.setText("");
            selectedRequestId = -1;
            loadPendingRequests();  // refresh the dropdown
        } catch (SQLException e) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("DB error: " + e.getMessage());
        }
    }

    @FXML private void handleBack() {
        SceneNavigator.navigateTo("/fxml/attendant/attendant-dashboard.fxml", "Attendant Dashboard");
    }
}