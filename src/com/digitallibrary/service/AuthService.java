package com.digitallibrary.service;

import com.digitallibrary.dao.AdminDAO;

import java.sql.SQLException;

/**
 * Service handling administrator authentication and session.
 */
public class AuthService {

    private final AdminDAO adminDAO;
    private static String currentAdminUsername = null;

    public AuthService() {
        this.adminDAO = new AdminDAO();
    }

    /**
     * Authenticates an administrator.
     * @param username username
     * @param password password
     * @return true if valid credentials, false otherwise
     */
    public boolean login(String username, String password) throws SQLException {
        if (username == null || username.trim().isEmpty() ||
            password == null || password.isEmpty()) {
            return false;
        }

        boolean success = adminDAO.authenticate(username.trim(), password);
        if (success) {
            currentAdminUsername = username.trim();
        }
        return success;
    }

    /**
     * Logs out the current admin.
     */
    public void logout() {
        currentAdminUsername = null;
    }

    /**
     * Returns the currently authenticated admin username.
     */
    public static String getCurrentAdminUsername() {
        return currentAdminUsername != null ? currentAdminUsername : "Administrator";
    }
}
