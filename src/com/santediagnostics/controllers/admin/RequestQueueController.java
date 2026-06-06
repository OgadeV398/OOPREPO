package com.santediagnostics.controllers.admin;

import com.santediagnostics.auth.SessionManager;
import com.santediagnostics.db.DBConnection;
import com.santediagnostics.utils.AuditLogger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;

public class RequestQueueController {

    @FXML private ComboBox<String> paymentFilterBox;
    @FXML private Label messageLabel;
    @FXML private TableView<ObservableList<String>> requestTable;
    @FXML private TableColumn<ObservableList<String>, String> colId;
    @FXML private TableColumn<ObservableList<String>, String> colCustomer;
    @FXML private TableColumn<ObservableList<String>, String> colTest;
    @FXML private TableColumn<ObservableList<String>, String> colStatus;
    @FXML private TableColumn<ObservableList<String>, String> colPayment;
    @FXML private TableColumn<ObservableList<String>, String> colDate;

    private ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        paymentFilterBox.setItems(FXCollections.observableArrayList("ALL", "PAID", "UNPAID"));
        paymentFilterBox.setValue("ALL");

        colId.setCellValueFactory(row -> new SimpleStringProperty(row.getValue().get(0)));
        colCustomer.setCellValueFactory(row -> new SimpleStringProperty(row.getValue().get(1)));
        colTest.setCellValueFactory(row -> new SimpleStringProperty(row.getValue().get(2)));
        colStatus.setCellValueFactory(row -> new SimpleStringProperty(row.getValue().get(3)));
        colPayment.setCellValueFactory(row -> new SimpleStringProperty(row.getValue().get(4)));
        colDate.setCellValueFactory(row -> new SimpleStringProperty(row.getValue().get(5)));

        requestTable.setItems(data);
        loadRequests();
    }

    @FXML
    public void loadRequests() {
        data.clear();
        String filter = paymentFilterBox.getValue();

        String sql = "SELECT tr.id, u.full_name, tt.name, tr.status, " +
                     "tr.payment_status, tr.requested_at " +
                     "FROM test_requests tr " +
                     "JOIN users u ON tr.customer_id = u.id " +
                     "JOIN test_types tt ON tr.test_type_id = tt.id";

        if (!"ALL".equals(filter)) {
            sql += " WHERE tr.payment_status = '" + filter + "'";
        }
        sql += " ORDER BY tr.requested_at DESC";

        try {
            Connection conn = DBConnection.getConnection();
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                row.add(rs.getString("id"));
                row.add(rs.getString("full_name"));
                row.add(rs.getString("name"));
                row.add(rs.getString("status"));
                row.add(rs.getString("payment_status"));
                row.add(rs.getString("requested_at"));
                data.add(row);
            }
        } catch (SQLException e) {
            messageLabel.setText("Error loading requests: " + e.getMessage());
        }
    }

    @FXML
    private void handleMarkPaid() {
        updatePaymentStatus("PAID");
    }

    @FXML
    private void handleMarkUnpaid() {
        updatePaymentStatus("UNPAID");
    }

    private void updatePaymentStatus(String status) {
        ObservableList<String> selected = requestTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Please select a request first.");
            return;
        }

        int requestId = Integer.parseInt(selected.get(0));
        String sql = "UPDATE test_requests SET payment_status = ? WHERE id = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setInt(2, requestId);
            stmt.executeUpdate();

            AuditLogger.log(
                SessionManager.getInstance().getCurrentUser().getId(),
                "Marked request #" + requestId + " as " + status,
                "test_requests", requestId
            );

            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("Request #" + requestId + " marked as " + status);
            loadRequests();

        } catch (SQLException e) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Error updating: " + e.getMessage());
        }
    }
}