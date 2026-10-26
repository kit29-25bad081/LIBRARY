package com.digitallibrary.gui;

import com.digitallibrary.model.IssueRecord;
import com.digitallibrary.service.IssueService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.time.LocalDate;

/**
 * Return E-book GUI screen.
 * Processes book returns via Issue ID with validation preventing double returns
 * and atomic JDBC transaction execution.
 */
public class ReturnBookFrame extends JFrame {

    private final IssueService issueService;
    private final DashboardFrame parentDashboard;

    private JTextField txtIssueId;
    private JButton btnFindIssue;
    private JButton btnReturn;
    private JButton btnClear;

    // Display fields for verified record
    private JLabel lblUserName;
    private JLabel lblUserEmail;
    private JLabel lblBookTitle;
    private JLabel lblBookIsbn;
    private JLabel lblIssueDate;
    private JLabel lblDueDate;
    private JLabel lblCurrentStatus;
    private JLabel lblReturnDate;

    private IssueRecord currentRecord;

    public ReturnBookFrame(DashboardFrame parentDashboard) {
        this.parentDashboard = parentDashboard;
        this.issueService = new IssueService();
        initComponents();
    }

    private void initComponents() {
        setTitle("DIGITAL LIBRARY MANAGEMENT SYSTEM - Return E-book");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(720, 640);
        setResizable(false);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 16));
        mainPanel.setBackground(UIUtils.COLOR_BG);

        // Header Banner
        JPanel headerPanel = UIUtils.createHeaderBanner(
                "RETURN E-BOOK",
                "Process book return with atomic transaction and status tracking"
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

        // --- Row 1: Issue ID Input ---
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        JLabel lblId = new JLabel("Enter Issue ID *:");
        lblId.setFont(UIUtils.FONT_HEADER);
        lblId.setForeground(UIUtils.COLOR_TEXT_MAIN);
        formCard.add(lblId, gbc);

        gbc.gridx = 1;
        txtIssueId = new JTextField(12);
        UIUtils.styleTextField(txtIssueId);
        formCard.add(txtIssueId, gbc);

        gbc.gridx = 2;
        btnFindIssue = UIUtils.createPrimaryButton("Look Up Issue");
        formCard.add(btnFindIssue, gbc);

        // --- Details Card (Subpanel) ---
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(16, 6, 16, 6);

        JPanel detailsPanel = new JPanel(new GridLayout(8, 2, 8, 8));
        detailsPanel.setBackground(new Color(248, 250, 252));
        detailsPanel.setBorder(new CompoundBorder(
                new LineBorder(UIUtils.COLOR_BORDER, 1),
                new EmptyBorder(14, 16, 14, 16)
        ));

        lblUserName = new JLabel("-");
        lblUserEmail = new JLabel("-");
        lblBookTitle = new JLabel("-");
        lblBookIsbn = new JLabel("-");
        lblIssueDate = new JLabel("-");
        lblDueDate = new JLabel("-");
        lblCurrentStatus = new JLabel("No record selected");
        lblReturnDate = new JLabel("-");

        addDetailRow(detailsPanel, "Borrower Name:", lblUserName);
        addDetailRow(detailsPanel, "Borrower Email:", lblUserEmail);
        addDetailRow(detailsPanel, "E-book Title:", lblBookTitle);
        addDetailRow(detailsPanel, "E-book ISBN:", lblBookIsbn);
        addDetailRow(detailsPanel, "Issue Date:", lblIssueDate);
        addDetailRow(detailsPanel, "Due Date:", lblDueDate);
        addDetailRow(detailsPanel, "Return Date:", lblReturnDate);
        addDetailRow(detailsPanel, "Current Status:", lblCurrentStatus);

        formCard.add(detailsPanel, gbc);

        // --- Action Buttons ---
        gbc.gridy = 2;
        gbc.insets = new Insets(8, 6, 4, 6);
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        buttonPanel.setOpaque(false);

        btnReturn = UIUtils.createSuccessButton("CONFIRM RETURN");
        btnReturn.setEnabled(false); // Disabled until a valid unreturned issue is verified
        btnClear = UIUtils.createSecondaryButton("Reset Fields");

        buttonPanel.add(btnReturn);
        buttonPanel.add(btnClear);
        formCard.add(buttonPanel, gbc);

        centerPanel.add(formCard);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);

        // Event Listeners
        btnFindIssue.addActionListener(e -> lookupIssueRecord());
        btnReturn.addActionListener(e -> handleReturn());
        btnClear.addActionListener(e -> resetDetails());

        txtIssueId.addActionListener(e -> lookupIssueRecord());
    }

    private void addDetailRow(JPanel panel, String labelText, JLabel valueLabel) {
        JLabel label = new JLabel(labelText);
        label.setFont(UIUtils.FONT_BOLD);
        label.setForeground(UIUtils.COLOR_TEXT_MUTED);

        valueLabel.setFont(UIUtils.FONT_REGULAR);
        valueLabel.setForeground(UIUtils.COLOR_TEXT_MAIN);

        panel.add(label);
        panel.add(valueLabel);
    }

    private void lookupIssueRecord() {
        String idStr = txtIssueId.getText().trim();
        if (idStr.isEmpty()) {
            UIUtils.showWarning(this, "Please enter an Issue ID.");
            txtIssueId.requestFocus();
            return;
        }

        try {
            int issueId = Integer.parseInt(idStr);
            currentRecord = issueService.getIssueRecordById(issueId);

            if (currentRecord == null) {
                resetDetails();
                UIUtils.showError(this, "Issue record ID " + issueId + " not found in database.");
                return;
            }

            lblUserName.setText(currentRecord.getUserName() != null ? currentRecord.getUserName() : "User ID: " + currentRecord.getUserId());
            lblUserEmail.setText(currentRecord.getUserEmail() != null ? currentRecord.getUserEmail() : "-");
            lblBookTitle.setText(currentRecord.getEbookTitle() != null ? currentRecord.getEbookTitle() : "Book ID: " + currentRecord.getEbookId());
            lblBookIsbn.setText(currentRecord.getEbookIsbn() != null ? currentRecord.getEbookIsbn() : "-");
            lblIssueDate.setText(currentRecord.getIssueDate() != null ? currentRecord.getIssueDate().toString() : "-");
            lblDueDate.setText(currentRecord.getDueDate() != null ? currentRecord.getDueDate().toString() : "-");
            lblReturnDate.setText(currentRecord.getReturnDate() != null ? currentRecord.getReturnDate().toString() : "-");

            String status = currentRecord.getStatus();
            lblCurrentStatus.setText(status);

            if ("RETURNED".equalsIgnoreCase(status)) {
                lblCurrentStatus.setForeground(UIUtils.COLOR_DANGER);
                lblCurrentStatus.setText("ALREADY RETURNED on " + currentRecord.getReturnDate());
                btnReturn.setEnabled(false);
                UIUtils.showWarning(this, "This e-book is ALREADY RETURNED!\nReturned on: " + currentRecord.getReturnDate() +
                        "\nAn already returned book cannot be returned again.");
            } else {
                lblCurrentStatus.setForeground(UIUtils.COLOR_WARNING);
                lblCurrentStatus.setText("ISSUED (Active Borrowing)");
                btnReturn.setEnabled(true);
            }

        } catch (NumberFormatException e) {
            UIUtils.showWarning(this, "Issue ID must be a valid integer number.");
        } catch (Exception e) {
            UIUtils.showError(this, "Database error: " + e.getMessage());
        }
    }

    private void handleReturn() {
        if (currentRecord == null) {
            UIUtils.showWarning(this, "Please look up a valid issue record first.");
            return;
        }

        if ("RETURNED".equalsIgnoreCase(currentRecord.getStatus())) {
            UIUtils.showWarning(this, "This book has already been returned!");
            return;
        }

        if (!UIUtils.showConfirm(this, "Confirm return of:\n'" + currentRecord.getEbookTitle() +
                "' borrowed by " + currentRecord.getUserName() + "?")) {
            return;
        }

        try {
            IssueRecord updated = issueService.returnBook(currentRecord.getIssueId());

            UIUtils.showSuccess(this,
                    "E-book returned successfully!\n\n" +
                    "Issue ID: " + updated.getIssueId() + "\n" +
                    "Return Date: " + updated.getReturnDate() + "\n" +
                    "Status: RETURNED\n" +
                    "E-book catalog availability restored to AVAILABLE.\n\n" +
                    "Database transaction committed successfully.");

            resetDetails();

            if (parentDashboard != null) {
                parentDashboard.refreshMetrics();
            }

        } catch (IllegalStateException ex) {
            UIUtils.showWarning(this, ex.getMessage());
        } catch (Exception ex) {
            UIUtils.showError(this, "Return failed (Transaction rolled back):\n" + ex.getMessage());
        }
    }

    private void resetDetails() {
        txtIssueId.setText("");
        lblUserName.setText("-");
        lblUserEmail.setText("-");
        lblBookTitle.setText("-");
        lblBookIsbn.setText("-");
        lblIssueDate.setText("-");
        lblDueDate.setText("-");
        lblReturnDate.setText("-");
        lblCurrentStatus.setText("No record selected");
        lblCurrentStatus.setForeground(UIUtils.COLOR_TEXT_MAIN);
        btnReturn.setEnabled(false);
        currentRecord = null;
    }
}
