package com.digitallibrary.gui;

import com.digitallibrary.model.Ebook;
import com.digitallibrary.model.IssueRecord;
import com.digitallibrary.model.User;
import com.digitallibrary.service.EbookService;
import com.digitallibrary.service.IssueService;
import com.digitallibrary.service.UserService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Issue E-book GUI screen.
 * Handles issuing an e-book to a user with automatic 14-day due date calculation
 * and JDBC transaction execution (commit/rollback).
 */
public class IssueBookFrame extends JFrame {

    private final IssueService issueService;
    private final UserService userService;
    private final EbookService ebookService;
    private final DashboardFrame parentDashboard;

    private JTextField txtUserId;
    private JTextField txtEbookId;
    private JTextField txtIssueDate;
    private JTextField txtDueDate;

    // Preview labels
    private JLabel lblUserPreview;
    private JLabel lblEbookPreview;
    private JLabel lblStatusBadge;

    private JButton btnCheckUser;
    private JButton btnCheckBook;
    private JButton btnIssue;
    private JButton btnClear;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public IssueBookFrame(DashboardFrame parentDashboard) {
        this.parentDashboard = parentDashboard;
        this.issueService = new IssueService();
        this.userService = new UserService();
        this.ebookService = new EbookService();
        initComponents();
    }

    private void initComponents() {
        setTitle("DIGITAL LIBRARY MANAGEMENT SYSTEM - Issue E-book");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(780, 660);
        setMinimumSize(new Dimension(680, 600));
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 16));
        mainPanel.setBackground(UIUtils.COLOR_BG);

        // Header Banner
        JPanel headerPanel = UIUtils.createHeaderBanner(
                "ISSUE E-BOOK",
                "Assign digital book with atomic JDBC transaction (14-day borrowing period)"
        );
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Form Container
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(UIUtils.COLOR_BG);
        centerPanel.setBorder(new EmptyBorder(10, 24, 20, 24));

        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(new CompoundBorder(
                new LineBorder(UIUtils.COLOR_BORDER, 1),
                new EmptyBorder(20, 28, 20, 28)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        // --- Row 1: User ID ---
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        JLabel lblUId = new JLabel("1. Member User ID *:");
        lblUId.setFont(UIUtils.FONT_BOLD);
        lblUId.setForeground(UIUtils.COLOR_TEXT_MAIN);
        formCard.add(lblUId, gbc);

        gbc.gridx = 1;
        txtUserId = new JTextField(12);
        UIUtils.styleTextField(txtUserId);
        formCard.add(txtUserId, gbc);

        gbc.gridx = 2;
        btnCheckUser = UIUtils.createSecondaryButton("Verify User");
        btnCheckUser.setFont(UIUtils.FONT_SMALL);
        formCard.add(btnCheckUser, gbc);

        // User Preview Label
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 6, 12, 6);
        lblUserPreview = new JLabel("Enter User ID and click Verify (or press Enter)");
        lblUserPreview.setFont(UIUtils.FONT_SMALL);
        lblUserPreview.setForeground(UIUtils.COLOR_TEXT_MUTED);
        formCard.add(lblUserPreview, gbc);

        // --- Row 2: E-book ID ---
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(6, 6, 6, 6);
        JLabel lblBId = new JLabel("2. E-book ID *:");
        lblBId.setFont(UIUtils.FONT_BOLD);
        lblBId.setForeground(UIUtils.COLOR_TEXT_MAIN);
        formCard.add(lblBId, gbc);

        gbc.gridx = 1;
        txtEbookId = new JTextField(12);
        UIUtils.styleTextField(txtEbookId);
        formCard.add(txtEbookId, gbc);

        gbc.gridx = 2;
        btnCheckBook = UIUtils.createSecondaryButton("Verify Book");
        btnCheckBook.setFont(UIUtils.FONT_SMALL);
        formCard.add(btnCheckBook, gbc);

        // Book Preview Label
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 6, 12, 6);
        lblEbookPreview = new JLabel("Enter E-book ID and click Verify (or press Enter)");
        lblEbookPreview.setFont(UIUtils.FONT_SMALL);
        lblEbookPreview.setForeground(UIUtils.COLOR_TEXT_MUTED);
        formCard.add(lblEbookPreview, gbc);

        // --- Row 3: Issue Date (Auto today) ---
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(6, 6, 6, 6);
        JLabel lblIDate = new JLabel("3. Issue Date:");
        lblIDate.setFont(UIUtils.FONT_BOLD);
        lblIDate.setForeground(UIUtils.COLOR_TEXT_MAIN);
        formCard.add(lblIDate, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        LocalDate today = LocalDate.now();
        txtIssueDate = new JTextField(today.format(DATE_FMT));
        txtIssueDate.setEditable(false);
        txtIssueDate.setBackground(new Color(241, 245, 249));
        UIUtils.styleTextField(txtIssueDate);
        formCard.add(txtIssueDate, gbc);

        // --- Row 4: Due Date (Auto +14 days) ---
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        JLabel lblDDate = new JLabel("4. Due Date (+14 days):");
        lblDDate.setFont(UIUtils.FONT_BOLD);
        lblDDate.setForeground(UIUtils.COLOR_TEXT_MAIN);
        formCard.add(lblDDate, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        LocalDate dueDate = today.plusDays(14);
        txtDueDate = new JTextField(dueDate.format(DATE_FMT));
        txtDueDate.setEditable(false);
        txtDueDate.setBackground(new Color(241, 245, 249));
        UIUtils.styleTextField(txtDueDate);
        formCard.add(txtDueDate, gbc);

        // --- Status Banner ---
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(12, 6, 12, 6);
        lblStatusBadge = new JLabel("Verify the member and available book before confirming.", SwingConstants.CENTER);
        lblStatusBadge.setFont(UIUtils.FONT_SMALL);
        lblStatusBadge.setForeground(UIUtils.COLOR_PRIMARY);
        formCard.add(lblStatusBadge, gbc);

        // --- Action Buttons ---
        gbc.gridy = 7;
        gbc.insets = new Insets(10, 6, 4, 6);
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        buttonPanel.setOpaque(false);

        btnIssue = UIUtils.createPrimaryButton("CONFIRM ISSUE");
        btnClear = UIUtils.createSecondaryButton("Reset Fields");

        buttonPanel.add(btnIssue);
        buttonPanel.add(btnClear);
        formCard.add(buttonPanel, gbc);

        centerPanel.add(formCard);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);

        // Event Listeners
        btnCheckUser.addActionListener(e -> verifyUser());
        btnCheckBook.addActionListener(e -> verifyEbook());
        btnIssue.addActionListener(e -> handleIssue());
        btnClear.addActionListener(e -> clearForm());

        txtUserId.addActionListener(e -> verifyUser());
        txtEbookId.addActionListener(e -> verifyEbook());
    }

    public void prefillEbook(int ebookId) {
        txtEbookId.setText(String.valueOf(ebookId));
        verifyEbook();
    }

    private boolean verifyUser() {
        String idStr = txtUserId.getText().trim();
        if (idStr.isEmpty()) {
            lblUserPreview.setText("Please enter a User ID.");
            lblUserPreview.setForeground(UIUtils.COLOR_DANGER);
            return false;
        }

        try {
            int userId = Integer.parseInt(idStr);
            User user = userService.getUserById(userId);
            if (user != null) {
                lblUserPreview.setText("✓ " + user.getName() + " (" + user.getEmail() + ")");
                lblUserPreview.setForeground(UIUtils.COLOR_SUCCESS);
                return true;
            } else {
                lblUserPreview.setText("✗ User ID " + userId + " does not exist in database.");
                lblUserPreview.setForeground(UIUtils.COLOR_DANGER);
                return false;
            }
        } catch (NumberFormatException e) {
            lblUserPreview.setText("✗ User ID must be a numeric integer.");
            lblUserPreview.setForeground(UIUtils.COLOR_DANGER);
            return false;
        }
    }

    private boolean verifyEbook() {
        String idStr = txtEbookId.getText().trim();
        if (idStr.isEmpty()) {
            lblEbookPreview.setText("Please enter an E-book ID.");
            lblEbookPreview.setForeground(UIUtils.COLOR_DANGER);
            return false;
        }

        try {
            int ebookId = Integer.parseInt(idStr);
            Ebook ebook = ebookService.getEbookById(ebookId);
            if (ebook != null) {
                if (ebook.isAvailable()) {
                    lblEbookPreview.setText("✓ '" + ebook.getTitle() + "' [AVAILABLE]");
                    lblEbookPreview.setForeground(UIUtils.COLOR_SUCCESS);
                    return true;
                } else {
                    lblEbookPreview.setText("✗ '" + ebook.getTitle() + "' is currently ISSUED.");
                    lblEbookPreview.setForeground(UIUtils.COLOR_DANGER);
                    return false;
                }
            } else {
                lblEbookPreview.setText("✗ E-book ID " + ebookId + " does not exist.");
                lblEbookPreview.setForeground(UIUtils.COLOR_DANGER);
                return false;
            }
        } catch (NumberFormatException e) {
            lblEbookPreview.setText("✗ E-book ID must be numeric.");
            lblEbookPreview.setForeground(UIUtils.COLOR_DANGER);
            return false;
        }
    }

    private void handleIssue() {
        if (!verifyUser()) {
            UIUtils.showWarning(this, "Please verify that a valid user exists.");
            txtUserId.requestFocus();
            return;
        }

        if (!verifyEbook()) {
            UIUtils.showWarning(this, "Please verify that the e-book exists and is currently available.");
            txtEbookId.requestFocus();
            return;
        }

        int userId = Integer.parseInt(txtUserId.getText().trim());
        int ebookId = Integer.parseInt(txtEbookId.getText().trim());
        if (!UIUtils.showConfirm(this, "Review this issue before continuing:\n\n" +
                "Member: " + lblUserPreview.getText() + "\n" +
                "E-book: " + lblEbookPreview.getText() + "\n" +
                "Issue date: " + txtIssueDate.getText() + "\n" +
                "Due date: " + txtDueDate.getText())) {
            return;
        }

        try {
            IssueRecord record = issueService.issueBook(userId, ebookId);

            UIUtils.showSuccess(this,
                    "E-book issued successfully!\n\n" +
                    "Issue ID: " + record.getIssueId() + "\n" +
                    "User: " + record.getUserName() + "\n" +
                    "Book: " + record.getEbookTitle() + "\n" +
                    "Issue Date: " + record.getIssueDate() + "\n" +
                    "Due Date: " + record.getDueDate() + "\n\n" +
                    "Database transaction committed successfully.");

            clearForm();

            if (parentDashboard != null) {
                parentDashboard.refreshMetrics();
            }

        } catch (Exception ex) {
            UIUtils.showError(this, "Issue failed (Transaction rolled back):\n" + ex.getMessage());
        }
    }

    private void clearForm() {
        txtUserId.setText("");
        txtEbookId.setText("");
        lblUserPreview.setText("Enter User ID and click Verify (or press Enter)");
        lblUserPreview.setForeground(UIUtils.COLOR_TEXT_MUTED);
        lblEbookPreview.setText("Enter E-book ID and click Verify (or press Enter)");
        lblEbookPreview.setForeground(UIUtils.COLOR_TEXT_MUTED);
    }
}
