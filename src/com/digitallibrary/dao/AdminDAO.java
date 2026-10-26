package com.digitallibrary.dao;

import com.digitallibrary.database.DBConnection;
import com.digitallibrary.model.Admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object for Administrator entities.
 * Handles database operations using JDBC PreparedStatement.
 */
public class AdminDAO {

    /**
     * Authenticates an admin against the MySQL database.
     * @param username the admin username
     * @param password the admin password
     * @return true if credentials match a record in the database, false otherwise
     */
    public boolean authenticate(String username, String password) throws SQLException {
        String sql = "SELECT admin_id FROM admins WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Finds an admin by username.
     * @param username the username to look up
     * @return Admin object if found, null otherwise
     */
    public Admin findByUsername(String username) {
        String sql = "SELECT admin_id, username, password FROM admins WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Admin(
                            rs.getInt("admin_id"),
                            rs.getString("username"),
                            rs.getString("password")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding admin by username: " + e.getMessage());
        }
        return null;
    }
}
