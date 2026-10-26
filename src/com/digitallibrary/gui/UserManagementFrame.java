package com.digitallibrary.gui;

import com.digitallibrary.model.User;
import com.digitallibrary.service.UserService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * User Management GUI screen.
 * Allows adding, updating, deleting, searching, and viewing library users.
 */
public class UserManagementFrame extends JFrame {

    private final UserService userService;
    private final DashboardFrame parentDashboard;

    private JTextField txtUserId;
    private JTextField txtName;
    private JTextField txtEmail;
    private JTextField txtPhone;
    private JTextField txtSearch;

    private JTable userTable;
    private DefaultTableModel tableModel;
    private JLabel lblEmptyState;

    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;
    private JButton btnSearch;
    private JButton btnViewAll;

    public UserManagementFrame(DashboardFrame parentDashboard) {
        this.parentDashboard = parentDashboard;
        this.userService = new UserService();
        initComponents();
        loadAllUsers();
    }

    private void initComponents() {
        setTitle("DIGITAL LIBRARY MANAGEMENT SYSTEM - User Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(980, 640);
        setMinimumSize(new Dimension(880, 560));
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 14));
        mainPanel.setBackground(UIUtils.COLOR_BG);

        // Header Banner
        JPanel headerPanel = UIUtils.createHeaderBanner(
                "USER MANAGEMENT",
                "Register, update, search, and manage library members"
        );
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content Split Pane or 2-column layout (Left Form, Right Table)
        JPanel contentPanel = new JPanel(new BorderLayout(16, 0));
        contentPanel.setBackground(UIUtils.COLOR_BG);
        contentPanel.setBorder(new EmptyBorder(0, 16, 16, 16));

        // ================= Left Column: Form Card =================
        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(new CompoundBorder(
                new LineBorder(UIUtils.COLOR_BORDER, 1),
                new EmptyBorder(16, 20, 16, 20)
        ));
        formCard.setPreferredSize(new Dimension(340, 500));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 4, 6, 4);

        JLabel lblFormTitle = new JLabel("User Details");
        lblFormTitle.setFont(UIUtils.FONT_HEADER);
        lblFormTitle.setForeground(UIUtils.COLOR_TEXT_MAIN);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 4, 12, 4);
        formCard.add(lblFormTitle, gbc);

        // User ID (Read-only)
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.insets = new Insets(4, 4, 2, 4);
        JLabel lblId = new JLabel("User ID (Auto-generated)");
        lblId.setFont(UIUtils.FONT_BOLD);
        lblId.setForeground(UIUtils.COLOR_TEXT_MUTED);
        formCard.add(lblId, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(2, 4, 10, 4);
        txtUserId = new JTextField(15);
        txtUserId.setEditable(false);
        txtUserId.setBackground(new Color(241, 245, 249));
        UIUtils.styleTextField(txtUserId);
        formCard.add(txtUserId, gbc);

        // Name
        gbc.gridy = 3;
        gbc.insets = new Insets(4, 4, 2, 4);
        JLabel lblName = new JLabel("Full Name *");
        lblName.setFont(UIUtils.FONT_BOLD);
        lblName.setForeground(UIUtils.COLOR_TEXT_MUTED);
        formCard.add(lblName, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(2, 4, 10, 4);
        txtName = new JTextField(15);
        UIUtils.styleTextField(txtName);
        formCard.add(txtName, gbc);

        // Email
        gbc.gridy = 5;
        gbc.insets = new Insets(4, 4, 2, 4);
        JLabel lblEmail = new JLabel("Email Address * (Unique)");
        lblEmail.setFont(UIUtils.FONT_BOLD);
        lblEmail.setForeground(UIUtils.COLOR_TEXT_MUTED);
        formCard.add(lblEmail, gbc);

        gbc.gridy = 6;
        gbc.insets = new Insets(2, 4, 10, 4);
        txtEmail = new JTextField(15);
        UIUtils.styleTextField(txtEmail);
        formCard.add(txtEmail, gbc);

        // Phone
        gbc.gridy = 7;
        gbc.insets = new Insets(4, 4, 2, 4);
        JLabel lblPhone = new JLabel("Phone Number *");
        lblPhone.setFont(UIUtils.FONT_BOLD);
        lblPhone.setForeground(UIUtils.COLOR_TEXT_MUTED);
        formCard.add(lblPhone, gbc);

        gbc.gridy = 8;
        gbc.insets = new Insets(2, 4, 16, 4);
        txtPhone = new JTextField(15);
        UIUtils.styleTextField(txtPhone);
        formCard.add(txtPhone, gbc);

        // Form Action Buttons (Add, Update, Delete, Clear)
        gbc.gridy = 9;
        gbc.insets = new Insets(8, 4, 4, 4);
        JPanel buttonGrid = new JPanel(new GridLayout(2, 2, 8, 8));
        buttonGrid.setOpaque(false);

        btnAdd = UIUtils.createPrimaryButton("Add User");
        btnUpdate = UIUtils.createSecondaryButton("Update");
        btnDelete = UIUtils.createDangerButton("Delete");
        btnClear = UIUtils.createLightButton("Clear Form");

        buttonGrid.add(btnAdd);
        buttonGrid.add(btnUpdate);
        buttonGrid.add(btnDelete);
        buttonGrid.add(btnClear);
        formCard.add(buttonGrid, gbc);

        contentPanel.add(formCard, BorderLayout.WEST);

        // ================= Right Column: Search + Table =================
        JPanel tableContainer = new JPanel(new BorderLayout(0, 10));
        tableContainer.setBackground(UIUtils.COLOR_BG);

        // Search Bar at Top of Table
        JPanel searchBar = new JPanel(new BorderLayout(8, 0));
        searchBar.setBackground(Color.WHITE);
        searchBar.setBorder(new CompoundBorder(
                new LineBorder(UIUtils.COLOR_BORDER, 1),
                new EmptyBorder(8, 12, 8, 12)
        ));

        JLabel lblSearchIcon = new JLabel("Search Users:");
        lblSearchIcon.setFont(UIUtils.FONT_BOLD);
        lblSearchIcon.setForeground(UIUtils.COLOR_TEXT_MUTED);

        txtSearch = new JTextField();
        UIUtils.styleTextField(txtSearch);
        txtSearch.setToolTipText("Search by name, email, or phone number");

        btnSearch = UIUtils.createPrimaryButton("Search");
        btnSearch.setPreferredSize(new Dimension(100, 32));

        btnViewAll = UIUtils.createSecondaryButton("View All");
        btnViewAll.setPreferredSize(new Dimension(100, 32));

        JPanel searchBtnWrapper = new JPanel(new GridLayout(1, 2, 6, 0));
        searchBtnWrapper.setOpaque(false);
        searchBtnWrapper.add(btnSearch);
        searchBtnWrapper.add(btnViewAll);

        searchBar.add(lblSearchIcon, BorderLayout.WEST);
        searchBar.add(txtSearch, BorderLayout.CENTER);
        searchBar.add(searchBtnWrapper, BorderLayout.EAST);

        tableContainer.add(searchBar, BorderLayout.NORTH);

        // Users JTable
        String[] columnNames = {"User ID", "Full Name", "Email Address", "Phone Number", "Registration Date"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only cells
            }
        };

        userTable = new JTable(tableModel);
        UIUtils.styleTable(userTable);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Column widths
        userTable.getColumnModel().getColumn(0).setPreferredWidth(70);
        userTable.getColumnModel().getColumn(1).setPreferredWidth(160);
        userTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        userTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        userTable.getColumnModel().getColumn(4).setPreferredWidth(140);

        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(new LineBorder(UIUtils.COLOR_BORDER, 1, true));
        tableContainer.add(scrollPane, BorderLayout.CENTER);
        lblEmptyState = new JLabel("No users found.", SwingConstants.CENTER);
        lblEmptyState.setFont(UIUtils.FONT_REGULAR);
        lblEmptyState.setForeground(UIUtils.COLOR_TEXT_MUTED);
        lblEmptyState.setBorder(new EmptyBorder(10, 0, 2, 0));
        tableContainer.add(lblEmptyState, BorderLayout.SOUTH);

        contentPanel.add(tableContainer, BorderLayout.CENTER);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);

        // ================= Event Listeners =================
        // Select row populates form
        userTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && userTable.getSelectedRow() != -1) {
                populateFormFromSelectedRow();
            }
        });

        btnAdd.addActionListener(e -> handleAddUser());
        btnUpdate.addActionListener(e -> handleUpdateUser());
        btnDelete.addActionListener(e -> handleDeleteUser());
        btnClear.addActionListener(e -> clearForm());
        btnSearch.addActionListener(e -> handleSearch());
        btnViewAll.addActionListener(e -> {
            txtSearch.setText("");
            loadAllUsers();
        });

        // Search on Enter key
        txtSearch.addActionListener(e -> handleSearch());
    }

    private void loadAllUsers() {
        tableModel.setRowCount(0);
        try {
            List<User> users = userService.getAllUsers();
            for (User u : users) {
                tableModel.addRow(new Object[]{
                        u.getUserId(),
                        u.getName(),
                        u.getEmail(),
                        u.getPhone(),
                        u.getCreatedAt() != null ? u.getCreatedAt().toString().substring(0, 19) : "-"
                });
            }
            updateEmptyState();
        } catch (Exception e) {
            UIUtils.showError(this, "Failed to load users from database: " + e.getMessage());
        }
    }

    private void handleSearch() {
        String query = txtSearch.getText().trim();
        tableModel.setRowCount(0);
        try {
            List<User> results = userService.searchUsers(query);
            for (User u : results) {
                tableModel.addRow(new Object[]{
                        u.getUserId(),
                        u.getName(),
                        u.getEmail(),
                        u.getPhone(),
                        u.getCreatedAt() != null ? u.getCreatedAt().toString().substring(0, 19) : "-"
                });
            }
            updateEmptyState();
        } catch (Exception e) {
            UIUtils.showError(this, "Search error: " + e.getMessage());
        }
    }

    private void populateFormFromSelectedRow() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow != -1) {
            int row = userTable.convertRowIndexToModel(selectedRow);
            txtUserId.setText(tableModel.getValueAt(row, 0).toString());
            txtName.setText(tableModel.getValueAt(row, 1).toString());
            txtEmail.setText(tableModel.getValueAt(row, 2).toString());
            txtPhone.setText(tableModel.getValueAt(row, 3).toString());
        }
    }

    private void handleAddUser() {
        try {
            String name = txtName.getText().trim();
            String email = txtEmail.getText().trim();
            String phone = txtPhone.getText().trim();

            User user = new User(name, email, phone);
            boolean success = userService.registerUser(user);

            if (success) {
                UIUtils.showSuccess(this, "User registered successfully with ID: " + user.getUserId());
                clearForm();
                loadAllUsers();
                if (parentDashboard != null) {
                    parentDashboard.refreshMetrics();
                }
            } else {
                UIUtils.showError(this, "Could not insert user record into MySQL.");
            }
        } catch (IllegalArgumentException ex) {
            UIUtils.showWarning(this, ex.getMessage());
        } catch (Exception ex) {
            UIUtils.showError(this, "Database error: " + ex.getMessage());
        }
    }

    private void handleUpdateUser() {
        String idStr = txtUserId.getText().trim();
        if (idStr.isEmpty()) {
            UIUtils.showWarning(this, "Please select a user from the table to update.");
            return;
        }

        try {
            int userId = Integer.parseInt(idStr);
            String name = txtName.getText().trim();
            String email = txtEmail.getText().trim();
            String phone = txtPhone.getText().trim();

            User user = new User(userId, name, email, phone);
            boolean success = userService.updateUser(user);

            if (success) {
                UIUtils.showSuccess(this, "User ID " + userId + " updated successfully!");
                loadAllUsers();
                if (parentDashboard != null) {
                    parentDashboard.refreshMetrics();
                }
            } else {
                UIUtils.showError(this, "Could not update user in MySQL.");
            }
        } catch (IllegalArgumentException ex) {
            UIUtils.showWarning(this, ex.getMessage());
        } catch (Exception ex) {
            UIUtils.showError(this, "Database error: " + ex.getMessage());
        }
    }

    private void handleDeleteUser() {
        String idStr = txtUserId.getText().trim();
        if (idStr.isEmpty()) {
            UIUtils.showWarning(this, "Please select a user from the table to delete.");
            return;
        }

        int userId = Integer.parseInt(idStr);
        String name = txtName.getText().trim();

        if (UIUtils.showConfirm(this, "Are you sure you want to delete this user?\n\n" +
                name + " (ID: " + userId + ")")) {
            try {
                boolean success = userService.deleteUser(userId);
                if (success) {
                    UIUtils.showSuccess(this, "User deleted successfully.");
                    clearForm();
                    loadAllUsers();
                    if (parentDashboard != null) {
                        parentDashboard.refreshMetrics();
                    }
                } else {
                    UIUtils.showError(this, "Could not delete user record.");
                }
            } catch (IllegalStateException ex) {
                UIUtils.showWarning(this, ex.getMessage());
            } catch (Exception ex) {
                UIUtils.showError(this, "Database error: " + ex.getMessage());
            }
        }
    }

    private void clearForm() {
        txtUserId.setText("");
        txtName.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        userTable.clearSelection();
    }

    private void updateEmptyState() {
        lblEmptyState.setVisible(tableModel.getRowCount() == 0);
    }
}
