package com.santediagnostics.controllers.attendant;

import com.santediagnostics.auth.SessionManager;
import com.santediagnostics.db.DBConnection;
import com.santediagnostics.utils.AuditLogger;
import com.santediagnostics.utils.SceneNavigator;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;

public class SampleTrackingController {

    @FXML private TableView<ObservableList<String>> sampleTable;
    @FXML private TableColumn<ObservableList<String>, String> colRequestId;
    @FXML private TableColumn<ObservableList<String>, String> colCustomer;
    @FXML private TableColumn<ObservableList<String>, String> colTest;
    @FXML private TableColumn<ObservableList<String>, String> colSampleStatus;
    @FXML private ComboBox<String> statusCombo;
    @FXML private Label messageLabel;

    private ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colRequestId.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().get(0)));
        colCustomer.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().get(1)));
        colTest.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().get(2)));
        colSampleStatus.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().get(3)));

        sampleTable.setItems(data);
        statusCombo.setItems(FXCollections.observableArrayList("COLLECTED", "PROCESSING", "COMPLETED"));
        loadSamples();
    }

    @FXML
    public void loadSamples() {
        data.clear();
        String sql = "SELECT s.id, s.request_id, u.full_name, tt.name, s.status " +
                     "FROM samples s " +
                     "JOIN test_requests tr ON s.request_id = tr.id " +
                     "JOIN users u ON tr.customer_id = u.id " +
                     "JOIN test_types tt ON tr.test_type_id = tt.id " +
                     "WHERE s.status != 'COMPLETED' " +
                     "ORDER BY s.request_id DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                row.add(String.valueOf(rs.getInt("request_id")));
                row.add(rs.getString("full_name"));
                row.add(rs.getString("name"));
                row.add(rs.getString("status"));
                data.add(row);
            }
        } catch (SQLException e) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Error loading samples: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdateStatus() {
        ObservableList<String> selected = sampleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Select a sample first.");
            return;
        }
        String newStatus = statusCombo.getValue();
        if (newStatus == null) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Select a status.");
            return;
        }

        int requestId = Integer.parseInt(selected.get(0));
        String updateSql = "UPDATE samples SET status = ?, updated_at = NOW() WHERE request_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, requestId);
            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                AuditLogger.log(SessionManager.getInstance().getCurrentUser().getId(),
                                "Updated sample status for request #" + requestId + " to " + newStatus,
                                "samples", requestId);
                messageLabel.setStyle("-fx-text-fill: green;");
                messageLabel.setText("Status updated to " + newStatus);
                loadSamples(); // refresh
            } else {
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText("Update failed.");
            }
        } catch (SQLException e) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("DB error: " + e.getMessage());
        }
    }

    @FXML private void handleBack() {
        SceneNavigator.navigateTo("/fxml/attendant/attendant-dashboard.fxml", "Attendant Dashboard");
    }
}