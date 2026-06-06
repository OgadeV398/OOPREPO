package com.santediagnostics.controllers.admin;

import com.santediagnostics.db.DBConnection;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;

public class AuditLogController {

    @FXML private TextField searchField;
    @FXML private TableView<ObservableList<String>> auditTable;
    @FXML private TableColumn<ObservableList<String>, String> colId;
    @FXML private TableColumn<ObservableList<String>, String> colUser;
    @FXML private TableColumn<ObservableList<String>, String> colAction;
    @FXML private TableColumn<ObservableList<String>, String> colTarget;
    @FXML private TableColumn<ObservableList<String>, String> colTargetId;
    @FXML private TableColumn<ObservableList<String>, String> colTime;

    private ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(row -> new SimpleStringProperty(row.getValue().get(0)));
        colUser.setCellValueFactory(row -> new SimpleStringProperty(row.getValue().get(1)));
        colAction.setCellValueFactory(row -> new SimpleStringProperty(row.getValue().get(2)));
        colTarget.setCellValueFactory(row -> new SimpleStringProperty(row.getValue().get(3)));
        colTargetId.setCellValueFactory(row -> new SimpleStringProperty(row.getValue().get(4)));
        colTime.setCellValueFactory(row -> new SimpleStringProperty(row.getValue().get(5)));

        auditTable.setItems(data);
        loadAllLogs();
    }

    @FXML
    public void loadAllLogs() {
        loadLogs("");
    }

    @FXML
    private void handleSearch() {
        loadLogs(searchField.getText().trim());
    }

    private void loadLogs(String search) {
        data.clear();
        String sql = "SELECT al.id, u.full_name, al.action, " +
                     "COALESCE(al.target_table, '-'), " +
                     "COALESCE(CAST(al.target_id AS VARCHAR), '-'), " +
                     "al.performed_at " +
                     "FROM audit_log al " +
                     "JOIN users u ON al.user_id = u.id";

        if (!search.isEmpty()) {
            sql += " WHERE LOWER(al.action) LIKE LOWER('%" + search + "%') " +
                   "OR LOWER(u.full_name) LIKE LOWER('%" + search + "%')";
        }
        sql += " ORDER BY al.performed_at DESC";

        try {
            Connection conn = DBConnection.getConnection();
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= 6; i++) {
                    row.add(rs.getString(i));
                }
                data.add(row);
            }
        } catch (SQLException e) {
            System.out.println("Audit log load error: " + e.getMessage());
        }
    }
}