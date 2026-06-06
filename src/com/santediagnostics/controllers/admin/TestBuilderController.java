package com.santediagnostics.controllers.admin;

import com.santediagnostics.auth.SessionManager;
import com.santediagnostics.db.DBConnection;
import com.santediagnostics.models.TestType;
import com.santediagnostics.utils.AuditLogger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.*;

public class TestBuilderController {

    @FXML private TextField testNameField;
    @FXML private TextField priceField;
    @FXML private TextField tatField;
    @FXML private ComboBox<String> resultFormatBox;
    @FXML private Label formMessageLabel;
    @FXML private TableView<TestType> testTypeTable;
    @FXML private TableColumn<TestType, String> colName;
    @FXML private TableColumn<TestType, Double> colPrice;
    @FXML private TableColumn<TestType, Integer> colTAT;
    @FXML private TableColumn<TestType, String> colFormat;

    private ObservableList<TestType> testList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        resultFormatBox.setItems(FXCollections.observableArrayList(
            "NUMERIC", "TEXT", "PDF", "IMAGE"
        ));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colTAT.setCellValueFactory(new PropertyValueFactory<>("turnaroundTime"));
        colFormat.setCellValueFactory(new PropertyValueFactory<>("resultFormat"));
        testTypeTable.setItems(testList);
        loadTestTypes();
    }

    private void loadTestTypes() {
        testList.clear();
        String sql = "SELECT * FROM test_types ORDER BY created_at DESC";
        try {
            Connection conn = DBConnection.getConnection();
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                TestType t = new TestType();
                t.setId(rs.getInt("id"));
                t.setName(rs.getString("name"));
                t.setPrice(rs.getDouble("price"));
                t.setTurnaroundTime(rs.getInt("turnaround_time"));
                t.setResultFormat(rs.getString("result_format"));
                testList.add(t);
            }
        } catch (SQLException e) {
            System.out.println("Load test types error: " + e.getMessage());
        }
    }

    @FXML
    private void handleSaveTest() {
        String name = testNameField.getText().trim();
        String priceText = priceField.getText().trim();
        String tatText = tatField.getText().trim();
        String format = resultFormatBox.getValue();

        if (name.isEmpty() || priceText.isEmpty() || tatText.isEmpty() || format == null) {
            formMessageLabel.setStyle("-fx-text-fill: red;");
            formMessageLabel.setText("All fields are required.");
            return;
        }

        try {
            double price = Double.parseDouble(priceText);
            int tat = Integer.parseInt(tatText);

            String sql = "INSERT INTO test_types (name, price, turnaround_time, result_format) " +
                         "VALUES (?, ?, ?, ?)";
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setDouble(2, price);
            stmt.setInt(3, tat);
            stmt.setString(4, format);
            stmt.executeUpdate();

            AuditLogger.log(
                SessionManager.getInstance().getCurrentUser().getId(),
                "Created test type: " + name,
                "test_types", 0
            );

            formMessageLabel.setStyle("-fx-text-fill: green;");
            formMessageLabel.setText("Test type saved successfully.");
            testNameField.clear();
            priceField.clear();
            tatField.clear();
            resultFormatBox.setValue(null);
            loadTestTypes();

        } catch (NumberFormatException e) {
            formMessageLabel.setStyle("-fx-text-fill: red;");
            formMessageLabel.setText("Price and TAT must be numbers.");
        } catch (SQLException e) {
            formMessageLabel.setStyle("-fx-text-fill: red;");
            formMessageLabel.setText("Error saving: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteTest() {
        TestType selected = testTypeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            formMessageLabel.setStyle("-fx-text-fill: red;");
            formMessageLabel.setText("Please select a test type to delete.");
            return;
        }

        try {
            String sql = "DELETE FROM test_types WHERE id = ?";
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, selected.getId());
            stmt.executeUpdate();

            AuditLogger.log(
                SessionManager.getInstance().getCurrentUser().getId(),
                "Deleted test type: " + selected.getName(),
                "test_types", selected.getId()
            );

            formMessageLabel.setStyle("-fx-text-fill: green;");
            formMessageLabel.setText("Test type deleted.");
            loadTestTypes();

        } catch (SQLException e) {
            formMessageLabel.setStyle("-fx-text-fill: red;");
            formMessageLabel.setText("Error deleting: " + e.getMessage());
        }
    }
}