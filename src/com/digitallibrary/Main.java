package com.digitallibrary;

import com.digitallibrary.database.DBConnection;
import com.digitallibrary.gui.LoginFrame;

import javax.swing.*;
import java.sql.SQLException;

/**
 * Main application entry point for the Digital Library Management System.
 * Initializes the Java Swing desktop application and displays the LoginFrame.
 *
 * Requirements satisfied:
 * - Desktop GUI window only (no browser, no localhost web server)
 * - Java Swing Frontend -> Core Java Backend -> DAO -> JDBC -> MySQL Database
 * - Prepares Look & Feel and checks JDBC connection
 */
public class Main {

    public static void main(String[] args) {
        // Set modern native/system Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Could not set system look and feel: " + e.getMessage());
        }

        // Print startup banner in console
        System.out.println("=================================================");
        System.out.println("    DIGITAL LIBRARY MANAGEMENT SYSTEM");
        System.out.println("    College Academic Java Desktop Application");
        System.out.println("=================================================");
        System.out.println("Frontend: Java Swing");
        System.out.println("Backend:  Core Java");
        System.out.println("Database: MySQL (digital_library)");
        System.out.println("Driver:   MySQL Connector/J (JDBC)");
        System.out.println("Checking database connectivity...");

        try {
            DBConnection.testConnection();
            System.out.println("[SUCCESS] Successfully connected to MySQL database!");
        } catch (SQLException e) {
            System.out.println("[NOTICE] Could not connect to MySQL at " + DBConnection.getDbUrl() + ".");
            System.out.println("         JDBC error: " + DBConnection.describeSQLException(e));
        }

        // Launch Java Swing GUI window on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            try {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Error initializing Digital Library GUI:\n" + e.getMessage(),
                        "Initialization Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
