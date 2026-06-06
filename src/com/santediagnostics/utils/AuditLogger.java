package com.santediagnostics.utils;

import com.santediagnostics.db.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AuditLogger {

    public static void log(int userId, String action, String targetTable, int targetId) {
        String sql = "INSERT INTO audit_log (user_id, action, target_table, target_id) " +
                     "VALUES (?, ?, ?, ?)";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setString(2, action);
            stmt.setString(3, targetTable);
            stmt.setInt(4, targetId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Audit log error: " + e.getMessage());
        }
    }

    public static void log(int userId, String action) {
        String sql = "INSERT INTO audit_log (user_id, action) VALUES (?, ?)";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setString(2, action);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Audit log error: " + e.getMessage());
        }
    }
}