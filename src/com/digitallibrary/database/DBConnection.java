package com.digitallibrary.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Manages database connectivity for the Digital Library Management System.
 * Uses JDBC and MySQL Connector/J driver.
 *
 * Configuration defaults:
 * URL: jdbc:mysql://localhost:3306/digital_library?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
 * Username: root
 * Password: entered in the database configuration screen and saved in db.properties
 *
 * Credentials can be modified through the login screen or via db.properties.
 */
public class DBConnection {

    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "3306";
    private static final String DEFAULT_DATABASE = "digital_library";
    private static final String DEFAULT_URL = "jdbc:mysql://" + DEFAULT_HOST + ":" + DEFAULT_PORT + "/" +
            DEFAULT_DATABASE + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static String dbUrl = DEFAULT_URL;
    private static String dbUser = "root";
    private static String dbPassword = "";

    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    private static final String CONFIG_FILE = "db.properties";

    static {
        // Load driver class
        try {
            Class.forName(DRIVER_CLASS);
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found in classpath: " + e.getMessage());
        }

        // Try loading from optional db.properties if exists
        loadConfigFromFile();
    }

    private DBConnection() {
        // Private constructor to prevent instantiation
    }

    /**
     * Loads database configuration from db.properties if available.
     */
    private static void loadConfigFromFile() {
        File file = new File(CONFIG_FILE);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                Properties props = new Properties();
                props.load(fis);
                if (props.containsKey("db.url")) {
                    dbUrl = props.getProperty("db.url").trim();
                }
                if (props.containsKey("db.user")) {
                    dbUser = props.getProperty("db.user").trim();
                }
                if (props.containsKey("db.password")) {
                    dbPassword = props.getProperty("db.password");
                }
            } catch (IOException e) {
                System.err.println("Could not read " + CONFIG_FILE + ", using default credentials: " + e.getMessage());
            }
        }
    }

    /**
     * Obtains a new database connection.
     * @return active java.sql.Connection
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        if (dbPassword.isEmpty()) {
            throw new SQLException("The MySQL password is empty. Enter the MySQL root password in " +
                    "Configure Database Connection before connecting.");
        }
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }

    /**
     * Tests whether the MySQL connection is healthy.
     * @return true if connection succeeds
     * @throws SQLException with the JDBC connection failure details
     */
    public static void testConnection() throws SQLException {
        try (Connection conn = getConnection()) {
            if (!conn.isValid(2)) {
                throw new SQLException("MySQL connection failed validation.");
            }
        }
    }

    /**
     * Allows dynamically updating database credentials at runtime and persists to db.properties.
     */
    public static synchronized void updateCredentials(String host, String port, String database, String user, String password)
            throws IOException {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Enter the MySQL password. It is separate from the library admin password.");
        }
        String updatedUrl = String.format("jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                host.trim(), port.trim(), database.trim());
        String updatedUser = user.trim();

        Properties props = new Properties();
        props.setProperty("db.url", updatedUrl);
        props.setProperty("db.user", updatedUser);
        props.setProperty("db.password", password);
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            props.store(fos, "Digital Library Database Configuration");
        }

        dbUrl = updatedUrl;
        dbUser = updatedUser;
        dbPassword = password;
    }

    public static String describeSQLException(SQLException exception) {
        StringBuilder details = new StringBuilder();
        for (SQLException current = exception; current != null; current = current.getNextException()) {
            if (details.length() > 0) {
                details.append(System.lineSeparator());
            }
            details.append(current.getMessage());
            if (current.getSQLState() != null) {
                details.append(" [SQLState: ").append(current.getSQLState()).append(']');
            }
            if (current.getErrorCode() != 0) {
                details.append(" [Error code: ").append(current.getErrorCode()).append(']');
            }
            if (current.getErrorCode() == 1049) {
                details.append(System.lineSeparator())
                        .append("Database '").append(DEFAULT_DATABASE).append("' does not exist. ")
                        .append("Import database.sql from the project folder to create it.");
            }
        }
        return details.toString();
    }

    // Getters
    public static String getDbUrl() {
        return dbUrl;
    }

    public static String getDbUser() {
        return dbUser;
    }

    public static String getDbPassword() {
        return dbPassword;
    }
}
