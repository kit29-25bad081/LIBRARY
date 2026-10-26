package com.digitallibrary.gui;

import com.digitallibrary.model.IssueRecord;
import com.digitallibrary.service.IssueService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Issue History GUI screen.
 * Displays all past and active borrowing transactions with filtering options (All, Issued, Returned).
 */
public class IssueHistoryFrame extends JFrame {

    private final IssueService issueService;
    private final DashboardFrame parentDashboard;

    private JTable historyTable;
    private DefaultTableModel tableModel;

    private JRadioButton rbAll;
    private JRadioButton rbIssued;
    private JRadioButton rbReturned;
    private JTextField txtSearch;
    private JLabel lblCount;
    private JLabel lblEmptyState;

    public IssueHistoryFrame(DashboardFrame parentDashboard) {
        this.parentDashboard = parentDashboard;
        this.issueService = new IssueService();
        initComponents();
        loadHistory("ALL");
    }

    private void initComponents() {
        setTitle("DIGITAL LIBRARY MANAGEMENT SYSTEM - Issue & Return History");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1060, 680);
        setMinimumSize(new Dimension(920, 560));
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 14));
        mainPanel.setBackground(UIUtils.COLOR_BG);

        // Header Banner
        JPanel headerPanel = UIUtils.createHeaderBanner(
                "ISSUE & RETURN HISTORY",
                "Complete audit trail of all borrowing transactions, due dates, and return statuses"
        );
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Center Content
        JPanel centerPanel = new JPanel(new BorderLayout(0, 12));
        centerPanel.setBackground(UIUtils.COLOR_BG);
        centerPanel.setBorder(new EmptyBorder(0, 18, 16, 18));

        // --- Controls Card (Filter Bar) ---
        JPanel filterCard = new JPanel(new BorderLayout(16, 0));
        filterCard.setBackground(Color.WHITE);
        filterCard.setBorder(new CompoundBorder(
                new LineBorder(UIUtils.COLOR_BORDER, 1),
                new EmptyBorder(12, 18, 12, 18)
        ));

        // Radio Buttons (All, Issued, Returned)
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        radioPanel.setOpaque(false);

        JLabel lblFilter = new JLabel("Filter Status:");
        lblFilter.setFont(UIUtils.FONT_BOLD);
        lblFilter.setForeground(UIUtils.COLOR_TEXT_MAIN);

        rbAll = new JRadioButton("All Records", true);
        rbIssued = new JRadioButton("Currently Issued (Active)");
        rbReturned = new JRadioButton("Returned");

        rbAll.setFont(UIUtils.FONT_REGULAR);
        rbIssued.setFont(UIUtils.FONT_REGULAR);
        rbReturned.setFont(UIUtils.FONT_REGULAR);

        rbAll.setOpaque(false);
        rbIssued.setOpaque(false);
        rbReturned.setOpaque(false);

        ButtonGroup filterGroup = new ButtonGroup();
        filterGroup.add(rbAll);
        filterGroup.add(rbIssued);
        filterGroup.add(rbReturned);

        radioPanel.add(lblFilter);
        radioPanel.add(rbAll);
        radioPanel.add(rbIssued);
        radioPanel.add(rbReturned);

        // Search & Refresh on right
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionPanel.setOpaque(false);

        JLabel lblSearch = new JLabel("Filter text:");
        lblSearch.setFont(UIUtils.FONT_BOLD);
        lblSearch.setForeground(UIUtils.COLOR_TEXT_MUTED);

        txtSearch = new JTextField(14);
        UIUtils.styleTextField(txtSearch);
        txtSearch.setToolTipText("Filter by user name, e-book title, or issue ID");

        JButton btnSearch = UIUtils.createPrimaryButton("Filter");
        btnSearch.setPreferredSize(new Dimension(80, 32));

        JButton btnRefresh = UIUtils.createSecondaryButton("Refresh");
        btnRefresh.setPreferredSize(new Dimension(85, 32));

        actionPanel.add(lblSearch);
        actionPanel.add(txtSearch);
        actionPanel.add(btnSearch);
        actionPanel.add(btnRefresh);

        filterCard.add(radioPanel, BorderLayout.WEST);
        filterCard.add(actionPanel, BorderLayout.EAST);

        centerPanel.add(filterCard, BorderLayout.NORTH);

        // --- Table Panel ---
        JPanel tableContainer = new JPanel(new BorderLayout(0, 8));
        tableContainer.setBackground(UIUtils.COLOR_BG);

        // Metric banner
        JPanel countBanner = new JPanel(new BorderLayout());
        countBanner.setOpaque(false);
        lblCount = new JLabel("Showing 0 records");
        lblCount.setFont(UIUtils.FONT_BOLD);
        lblCount.setForeground(UIUtils.COLOR_TEXT_MUTED);
        countBanner.add(lblCount, BorderLayout.WEST);
        tableContainer.add(countBanner, BorderLayout.NORTH);

        // Columns required by prompt: Issue ID, User Name, E-book Title, Issue Date, Due Date, Return Date, Status
        String[] columnNames = {"Issue ID", "User Name", "E-book Title", "Issue Date", "Due Date", "Return Date", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        historyTable = new JTable(tableModel);
        UIUtils.styleTable(historyTable);
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Column widths
        historyTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        historyTable.getColumnModel().getColumn(1).setPreferredWidth(160);
        historyTable.getColumnModel().getColumn(2).setPreferredWidth(240);
        historyTable.getColumnModel().getColumn(3).setPreferredWidth(90);
        historyTable.getColumnModel().getColumn(4).setPreferredWidth(90);
        historyTable.getColumnModel().getColumn(5).setPreferredWidth(90);
        historyTable.getColumnModel().getColumn(6).setPreferredWidth(100);

        // Custom status column renderer
        historyTable.getColumnModel().getColumn(6).setCellRenderer(UIUtils.createStatusRenderer());

        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBorder(new LineBorder(UIUtils.COLOR_BORDER, 1, true));
        tableContainer.add(scrollPane, BorderLayout.CENTER);
        lblEmptyState = new JLabel("No transaction records found.", SwingConstants.CENTER);
        lblEmptyState.setFont(UIUtils.FONT_REGULAR);
        lblEmptyState.setForeground(UIUtils.COLOR_TEXT_MUTED);
        lblEmptyState.setBorder(new EmptyBorder(10, 0, 2, 0));
        tableContainer.add(lblEmptyState, BorderLayout.SOUTH);

        centerPanel.add(tableContainer, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);

        // Event Listeners
        rbAll.addActionListener(e -> applyFilter());
        rbIssued.addActionListener(e -> applyFilter());
        rbReturned.addActionListener(e -> applyFilter());
        btnSearch.addActionListener(e -> applyFilter());
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            applyFilter();
        });
        txtSearch.addActionListener(e -> applyFilter());
    }

    private void applyFilter() {
        String filter = "ALL";
        if (rbIssued.isSelected()) {
            filter = "ISSUED";
        } else if (rbReturned.isSelected()) {
            filter = "RETURNED";
        }
        loadHistory(filter);
    }

    private void loadHistory(String filter) {
        tableModel.setRowCount(0);
        String searchTxt = txtSearch.getText().trim().toLowerCase();

        try {
            List<IssueRecord> records = issueService.getIssueHistory(filter);
            int count = 0;

            for (IssueRecord r : records) {
                // Client-side text filter matching
                if (!searchTxt.isEmpty()) {
                    boolean match = (r.getUserName() != null && r.getUserName().toLowerCase().contains(searchTxt)) ||
                                    (r.getEbookTitle() != null && r.getEbookTitle().toLowerCase().contains(searchTxt)) ||
                                    String.valueOf(r.getIssueId()).contains(searchTxt) ||
                                    (r.getUserEmail() != null && r.getUserEmail().toLowerCase().contains(searchTxt));
                    if (!match) {
                        continue;
                    }
                }

                // Calculate overdue status dynamically if still ISSUED
                String displayStatus = r.getStatus();
                if ("ISSUED".equalsIgnoreCase(displayStatus) && r.getDueDate() != null) {
                    if (LocalDate.now().isAfter(r.getDueDate())) {
                        displayStatus = "OVERDUE";
                    }
                }

                tableModel.addRow(new Object[]{
                        r.getIssueId(),
                        r.getUserName() != null ? r.getUserName() : "User #" + r.getUserId(),
                        r.getEbookTitle() != null ? r.getEbookTitle() : "Book #" + r.getEbookId(),
                        r.getIssueDate() != null ? r.getIssueDate().toString() : "-",
                        r.getDueDate() != null ? r.getDueDate().toString() : "-",
                        r.getReturnDate() != null ? r.getReturnDate().toString() : "-",
                        displayStatus
                });
                count++;
            }

            lblCount.setText("Showing " + count + " transaction record(s)");
            lblEmptyState.setVisible(count == 0);

        } catch (Exception e) {
            UIUtils.showError(this, "Failed to load issue history: " + e.getMessage());
        }
    }
}
