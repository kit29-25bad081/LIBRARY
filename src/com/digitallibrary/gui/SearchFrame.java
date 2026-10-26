package com.digitallibrary.gui;

import com.digitallibrary.model.Ebook;
import com.digitallibrary.service.EbookService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Dedicated E-book Search screen.
 * Allows searching by Title, Author, Category, ISBN, and Availability filter.
 */
public class SearchFrame extends JFrame {

    private final EbookService ebookService;
    private final DashboardFrame parentDashboard;

    private JTextField txtTitle;
    private JTextField txtAuthor;
    private JComboBox<String> cmbCategory;
    private JTextField txtIsbn;
    private JCheckBox chkOnlyAvailable;

    private JTable searchTable;
    private DefaultTableModel tableModel;
    private JLabel lblResultCount;
    private JLabel lblEmptyState;

    private static final String[] SEARCH_CATEGORIES = {
            "All Categories",
            "Computer Science",
            "Software Engineering",
            "Algorithms",
            "Software Architecture",
            "Databases",
            "Systems & OS",
            "Networking",
            "Artificial Intelligence",
            "Programming Languages",
            "Mathematics",
            "General Science",
            "Other"
    };

    public SearchFrame(DashboardFrame parentDashboard) {
        this.parentDashboard = parentDashboard;
        this.ebookService = new EbookService();
        initComponents();
        performSearch(); // Initial search displays all
    }

    private void initComponents() {
        setTitle("DIGITAL LIBRARY MANAGEMENT SYSTEM - Search E-books");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 680);
        setMinimumSize(new Dimension(880, 560));
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 14));
        mainPanel.setBackground(UIUtils.COLOR_BG);

        // Header Banner
        JPanel headerPanel = UIUtils.createHeaderBanner(
                "SEARCH E-BOOKS",
                "Advanced multi-criteria catalog query with live availability filtering"
        );
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Center Area
        JPanel centerPanel = new JPanel(new BorderLayout(0, 14));
        centerPanel.setBackground(UIUtils.COLOR_BG);
        centerPanel.setBorder(new EmptyBorder(0, 18, 16, 18));

        // --- Filter Panel (Top Card) ---
        JPanel filterCard = new JPanel(new GridBagLayout());
        filterCard.setBackground(Color.WHITE);
        filterCard.setBorder(new CompoundBorder(
                new LineBorder(UIUtils.COLOR_BORDER, 1),
                new EmptyBorder(14, 18, 14, 18)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 6, 4, 6);

        // Title filter
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblT = new JLabel("Title:");
        lblT.setFont(UIUtils.FONT_BOLD);
        lblT.setForeground(UIUtils.COLOR_TEXT_MUTED);
        filterCard.add(lblT, gbc);

        gbc.gridx = 1;
        txtTitle = new JTextField(16);
        UIUtils.styleTextField(txtTitle);
        filterCard.add(txtTitle, gbc);

        // Author filter
        gbc.gridx = 2;
        JLabel lblA = new JLabel("Author:");
        lblA.setFont(UIUtils.FONT_BOLD);
        lblA.setForeground(UIUtils.COLOR_TEXT_MUTED);
        filterCard.add(lblA, gbc);

        gbc.gridx = 3;
        txtAuthor = new JTextField(16);
        UIUtils.styleTextField(txtAuthor);
        filterCard.add(txtAuthor, gbc);

        // Category filter
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lblC = new JLabel("Category:");
        lblC.setFont(UIUtils.FONT_BOLD);
        lblC.setForeground(UIUtils.COLOR_TEXT_MUTED);
        filterCard.add(lblC, gbc);

        gbc.gridx = 1;
        cmbCategory = new JComboBox<>(SEARCH_CATEGORIES);
        UIUtils.styleComboBox(cmbCategory);
        filterCard.add(cmbCategory, gbc);

        // ISBN filter
        gbc.gridx = 2;
        JLabel lblI = new JLabel("ISBN:");
        lblI.setFont(UIUtils.FONT_BOLD);
        lblI.setForeground(UIUtils.COLOR_TEXT_MUTED);
        filterCard.add(lblI, gbc);

        gbc.gridx = 3;
        txtIsbn = new JTextField(16);
        UIUtils.styleTextField(txtIsbn);
        filterCard.add(txtIsbn, gbc);

        // Availability checkbox & Action Buttons
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        chkOnlyAvailable = new JCheckBox("Show only available e-books", false);
        chkOnlyAvailable.setFont(UIUtils.FONT_BOLD);
        chkOnlyAvailable.setForeground(UIUtils.COLOR_TEXT_MAIN);
        chkOnlyAvailable.setOpaque(false);
        filterCard.add(chkOnlyAvailable, gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);

        JButton btnSearch = UIUtils.createPrimaryButton("Search Catalog");
        JButton btnReset = UIUtils.createSecondaryButton("Reset Filters");

        btnPanel.add(btnSearch);
        btnPanel.add(btnReset);
        filterCard.add(btnPanel, gbc);

        centerPanel.add(filterCard, BorderLayout.NORTH);

        // --- Table Panel (Center) ---
        JPanel tableContainer = new JPanel(new BorderLayout(0, 8));
        tableContainer.setBackground(UIUtils.COLOR_BG);

        // Result count banner
        JPanel countBanner = new JPanel(new BorderLayout());
        countBanner.setOpaque(false);
        lblResultCount = new JLabel("Found 0 e-books matching criteria");
        lblResultCount.setFont(UIUtils.FONT_BOLD);
        lblResultCount.setForeground(UIUtils.COLOR_TEXT_MAIN);
        countBanner.add(lblResultCount, BorderLayout.WEST);

        JButton btnQuickIssue = UIUtils.createSuccessButton("Issue Selected Book");
        btnQuickIssue.setToolTipText("Open the Issue window with the selected book ID pre-filled");
        btnQuickIssue.addActionListener(e -> handleQuickIssue());
        countBanner.add(btnQuickIssue, BorderLayout.EAST);

        tableContainer.add(countBanner, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"ID", "Title", "Author", "Category", "ISBN", "Publisher", "Year", "Availability"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        searchTable = new JTable(tableModel);
        UIUtils.styleTable(searchTable);
        searchTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        searchTable.getColumnModel().getColumn(7).setCellRenderer(UIUtils.createStatusRenderer());

        searchTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        searchTable.getColumnModel().getColumn(1).setPreferredWidth(220);
        searchTable.getColumnModel().getColumn(2).setPreferredWidth(140);
        searchTable.getColumnModel().getColumn(3).setPreferredWidth(130);
        searchTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        searchTable.getColumnModel().getColumn(5).setPreferredWidth(140);
        searchTable.getColumnModel().getColumn(6).setPreferredWidth(60);
        searchTable.getColumnModel().getColumn(7).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(searchTable);
        scrollPane.setBorder(new LineBorder(UIUtils.COLOR_BORDER, 1, true));
        tableContainer.add(scrollPane, BorderLayout.CENTER);
        lblEmptyState = new JLabel("No books found matching your search.", SwingConstants.CENTER);
        lblEmptyState.setFont(UIUtils.FONT_REGULAR);
        lblEmptyState.setForeground(UIUtils.COLOR_TEXT_MUTED);
        lblEmptyState.setBorder(new EmptyBorder(10, 0, 2, 0));
        tableContainer.add(lblEmptyState, BorderLayout.SOUTH);

        centerPanel.add(tableContainer, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);

        // Event Listeners
        btnSearch.addActionListener(e -> performSearch());
        btnReset.addActionListener(e -> resetFilters());
        chkOnlyAvailable.addActionListener(e -> performSearch());

        txtTitle.addActionListener(e -> performSearch());
        txtAuthor.addActionListener(e -> performSearch());
        txtIsbn.addActionListener(e -> performSearch());
        cmbCategory.addActionListener(e -> performSearch());
    }

    private void performSearch() {
        String title = txtTitle.getText().trim();
        String author = txtAuthor.getText().trim();
        String category = cmbCategory.getSelectedItem() != null ? cmbCategory.getSelectedItem().toString() : "";
        String isbn = txtIsbn.getText().trim();
        boolean onlyAvailable = chkOnlyAvailable.isSelected();

        tableModel.setRowCount(0);

        try {
            List<Ebook> results = ebookService.searchEbooks(title, author, category, isbn, onlyAvailable);
            for (Ebook b : results) {
                tableModel.addRow(new Object[]{
                        b.getEbookId(),
                        b.getTitle(),
                        b.getAuthor(),
                        b.getCategory(),
                        b.getIsbn(),
                        b.getPublisher(),
                        b.getPublicationYear(),
                        b.isAvailable() ? "AVAILABLE" : "ISSUED"
                });
            }
            lblResultCount.setText("Found " + results.size() + " e-book(s) matching criteria");
            lblEmptyState.setVisible(results.isEmpty());
        } catch (Exception e) {
            UIUtils.showError(this, "Search failed: " + e.getMessage());
        }
    }

    private void resetFilters() {
        txtTitle.setText("");
        txtAuthor.setText("");
        cmbCategory.setSelectedIndex(0);
        txtIsbn.setText("");
        chkOnlyAvailable.setSelected(false);
        performSearch();
    }

    private void handleQuickIssue() {
        int selectedViewRow = searchTable.getSelectedRow();
        if (selectedViewRow == -1) {
            UIUtils.showWarning(this, "Please select an e-book from the table first.");
            return;
        }

        int selectedRow = searchTable.convertRowIndexToModel(selectedViewRow);
        int ebookId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
        String status = tableModel.getValueAt(selectedRow, 7).toString();

        if ("ISSUED".equalsIgnoreCase(status)) {
            UIUtils.showWarning(this, "The selected e-book is currently ISSUED and cannot be borrowed until returned.");
            return;
        }

        // Open Issue Book frame with this ebook pre-selected
        IssueBookFrame issueFrame = new IssueBookFrame(parentDashboard);
        issueFrame.prefillEbook(ebookId);
        issueFrame.setVisible(true);
    }
}
