package com.digitallibrary.gui;

import com.digitallibrary.database.DBConnection;
import com.digitallibrary.service.AuthService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Login GUI window for the Digital Library Management System.
 * Authenticates administrators via MySQL database through JDBC.
 */
public class LoginFrame extends JFrame {

    private final AuthService authService;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnExit;
    private JLabel lblStatus;

    public LoginFrame() {
        this.authService = new AuthService();
        initComponents();
    }

    private void initComponents() {
        setTitle("DIGITAL LIBRARY MANAGEMENT SYSTEM - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 600);
        setResizable(false);
        setLocationRelativeTo(null); // Centered on screen

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIUtils.COLOR_BG);

        // --- Header Panel ---
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        headerPanel.setBackground(UIUtils.COLOR_PRIMARY);
        headerPanel.setBorder(new EmptyBorder(28, 20, 28, 20));

        JLabel titleLabel = new JLabel("▤  DIGITAL LIBRARY", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 25));
        titleLabel.setForeground(Color.WHITE);

        JLabel subLabel = new JLabel("MANAGEMENT SYSTEM - ADMIN PORTAL", SwingConstants.CENTER);
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLabel.setForeground(new Color(224, 231, 255));

        headerPanel.add(titleLabel);
        headerPanel.add(subLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // --- Form Card Panel ---
        JPanel cardWrapper = new JPanel(new GridBagLayout());
        cardWrapper.setBackground(UIUtils.COLOR_BG);
        cardWrapper.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(new CompoundBorder(new LineBorder(UIUtils.COLOR_BORDER, 1, true),
                new EmptyBorder(28, 32, 28, 32)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 6, 8, 6);

        // Section Title
        JLabel lblLoginPrompt = new JLabel("Sign in to your account");
        lblLoginPrompt.setFont(UIUtils.FONT_HEADER);
        lblLoginPrompt.setForeground(UIUtils.COLOR_TEXT_MAIN);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 6, 16, 6);
        formCard.add(lblLoginPrompt, gbc);

        // Username Label & Field
        gbc.gridwidth = 1;
        gbc.insets = new Insets(6, 6, 2, 6);
        gbc.gridy = 1;
        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(UIUtils.FONT_BOLD);
        lblUser.setForeground(UIUtils.COLOR_TEXT_MUTED);
        formCard.add(lblUser, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(2, 6, 12, 6);
        txtUsername = new JTextField(20);
        UIUtils.styleTextField(txtUsername);
        txtUsername.setText("admin"); // Default prompt credential
        formCard.add(txtUsername, gbc);

        // Password Label & Field
        gbc.gridy = 3;
        gbc.insets = new Insets(6, 6, 2, 6);
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(UIUtils.FONT_BOLD);
        lblPass.setForeground(UIUtils.COLOR_TEXT_MUTED);
        formCard.add(lblPass, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(2, 6, 16, 6);
        txtPassword = new JPasswordField(20);
        UIUtils.styleTextField(txtPassword);
        JPanel passwordPanel = new JPanel(new BorderLayout(6, 0));
        passwordPanel.setOpaque(false);
        passwordPanel.add(txtPassword, BorderLayout.CENTER);
        JButton togglePassword = new JButton("Show");
        togglePassword.setFont(UIUtils.FONT_SMALL);
        togglePassword.setForeground(UIUtils.COLOR_PRIMARY);
        togglePassword.setBackground(Color.WHITE);
        togglePassword.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 1, 1, UIUtils.COLOR_BORDER), new EmptyBorder(0, 10, 0, 10)));
        togglePassword.setFocusPainted(false);
        togglePassword.setToolTipText("Show or hide password");
        char passwordEchoChar = txtPassword.getEchoChar();
        togglePassword.addActionListener(e -> {
            boolean show = txtPassword.getEchoChar() != 0;
            txtPassword.setEchoChar(show ? (char) 0 : passwordEchoChar);
            togglePassword.setText(show ? "Hide" : "Show");
        });
        passwordPanel.add(togglePassword, BorderLayout.EAST);
        formCard.add(passwordPanel, gbc);

        // Status / Message Label
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 6, 8, 6);
        lblStatus = new JLabel("Sign in with your administrator account", SwingConstants.CENTER);
        lblStatus.setFont(UIUtils.FONT_SMALL);
        lblStatus.setForeground(UIUtils.COLOR_TEXT_MUTED);
        formCard.add(lblStatus, gbc);

        // Buttons Panel (LOGIN, EXIT)
        gbc.gridy = 6;
        gbc.insets = new Insets(10, 6, 6, 6);
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        buttonPanel.setOpaque(false);

        btnLogin = UIUtils.createPrimaryButton("LOGIN");
        btnExit = UIUtils.createDangerButton("EXIT");

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnExit);
        formCard.add(buttonPanel, gbc);

        // DB Settings helper link button
        gbc.gridy = 7;
        gbc.insets = new Insets(8, 6, 0, 6);
        JButton btnDbConfig = new JButton("Configure Database Connection");
        btnDbConfig.setFont(UIUtils.FONT_SMALL);
        btnDbConfig.setForeground(UIUtils.COLOR_PRIMARY);
        btnDbConfig.setContentAreaFilled(false);
        btnDbConfig.setBorderPainted(false);
        btnDbConfig.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDbConfig.addActionListener(e -> showDbConfigDialog());
        formCard.add(btnDbConfig, gbc);

        cardWrapper.add(formCard);
        mainPanel.add(cardWrapper, BorderLayout.CENTER);

        setContentPane(mainPanel);

        // Event Listeners
        btnLogin.addActionListener(e -> performLogin());
        btnExit.addActionListener(e -> handleExit());

        // Press Enter to submit in password field
        txtPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });
        txtUsername.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtPassword.requestFocus();
                }
            }
        });
    }

    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty()) {
            UIUtils.showWarning(this, "Please enter your username.");
            txtUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            UIUtils.showWarning(this, "Please enter your password.");
            txtPassword.requestFocus();
            return;
        }

        btnLogin.setEnabled(false);
        lblStatus.setText("Authenticating with MySQL...");
        lblStatus.setForeground(UIUtils.COLOR_PRIMARY);

        // Authenticate via Core Java Backend & JDBC
        SwingUtilities.invokeLater(() -> {
            try {
                boolean authenticated = authService.login(username, password);
                if (!authenticated) {
                    btnLogin.setEnabled(true);
                    lblStatus.setText("Invalid username or password.");
                    lblStatus.setForeground(UIUtils.COLOR_DANGER);
                    UIUtils.showError(this, "Invalid username or password.");
                    return;
                }

                lblStatus.setText("Login successful! Loading dashboard...");
                lblStatus.setForeground(UIUtils.COLOR_SUCCESS);

                DashboardFrame dashboard = new DashboardFrame();
                dashboard.setVisible(true);
                dispose();
            } catch (SQLException e) {
                btnLogin.setEnabled(true);
                lblStatus.setText("MySQL authentication error.");
                lblStatus.setForeground(UIUtils.COLOR_DANGER);
                UIUtils.showError(this, "Could not authenticate with MySQL.\n\n" +
                        DBConnection.describeSQLException(e));
            }
        });
    }

    private void handleExit() {
        if (UIUtils.showConfirm(this, "Are you sure you want to exit the application?")) {
            System.exit(0);
        }
    }

    private void showDbConfigDialog() {
        JTextField hostField = new JTextField("localhost", 15);
        JTextField portField = new JTextField("3306", 6);
        JTextField dbField = new JTextField("digital_library", 15);
        JTextField userField = new JTextField(DBConnection.getDbUser(), 15);
        JPasswordField passField = new JPasswordField(DBConnection.getDbPassword(), 15);

        JPanel panel = new JPanel(new GridLayout(5, 2, 6, 8));
        panel.add(new JLabel("MySQL Host:"));
        panel.add(hostField);
        panel.add(new JLabel("MySQL Port:"));
        panel.add(portField);
        panel.add(new JLabel("Database Name:"));
        panel.add(dbField);
        panel.add(new JLabel("Username:"));
        panel.add(userField);
        panel.add(new JLabel("MySQL root password:"));
        panel.add(passField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Database Connection Settings",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                DBConnection.updateCredentials(
                        hostField.getText(),
                        portField.getText(),
                        dbField.getText(),
                        userField.getText(),
                        new String(passField.getPassword())
                );
                DBConnection.testConnection();
                UIUtils.showSuccess(this, "Connected successfully to MySQL database!");
                lblStatus.setText("Connected to MySQL database.");
                lblStatus.setForeground(UIUtils.COLOR_SUCCESS);
            } catch (IOException e) {
                UIUtils.showError(this, "Could not save database settings to db.properties:\n" + e.getMessage());
            } catch (IllegalArgumentException e) {
                UIUtils.showWarning(this, e.getMessage());
            } catch (SQLException e) {
                UIUtils.showError(this, "Database settings were saved, but the connection failed:\n\n" +
                        DBConnection.describeSQLException(e));
            }
        }
    }
}
