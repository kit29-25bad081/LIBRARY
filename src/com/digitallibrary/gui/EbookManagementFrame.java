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
 * E-book Management GUI screen.
 * Allows adding, updating, deleting, searching, and viewing the e-book catalog.
 */
public class EbookManagementFrame extends JFrame {

    private final EbookService ebookService;
    private final DashboardFrame parentDashboard;

    private JTextField txtEbookId;
    private JTextField txtTitle;
    private JTextField txtAuthor;
    private JComboBox<String> cmbCategory;
    private JTextField txtIsbn;
    private JTextField txtPublisher;
    private JTextField txtYear;
    private JTextField txtUrl;
    private JCheckBox chkAvailable;
    private JTextField txtSearch;

    private JTable ebookTable;
    private DefaultTableModel tableModel;
    private JLabel lblEmptyState;

    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;
    private JButton btnSearch;
    private JButton btnViewAll;

    private static final String[] DEFAULT_CATEGORIES = {
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

    public EbookManagementFrame(DashboardFrame parentDashboard) {
        this.parentDashboard = parentDashboard;
        this.ebookService = new EbookService();
        initComponents();
        loadAllEbooks();
    }

    private void initComponents() {
        setTitle("DIGITAL LIBRARY MANAGEMENT SYSTEM - E-book Catalog Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 700);
        setMinimumSize(new Dimension(960, 600));
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 14));
        mainPanel.setBackground(UIUtils.COLOR_BG);

        // Header Banner
        JPanel headerPanel = UIUtils.createHeaderBanner(
                "E-BOOK MANAGEMENT",
                "Register, update, delete, and inspect library digital publications"
        );
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content Split: Left Form, Right Table
        JPanel contentPanel = new JPanel(new BorderLayout(16, 0));
        contentPanel.setBackground(UIUtils.COLOR_BG);
        contentPanel.setBorder(new EmptyBorder(0, 16, 16, 16));

        // ================= Left Column: Form Card =================
        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(new CompoundBorder(
                new LineBorder(UIUtils.COLOR_BORDER, 1),
                new EmptyBorder(14, 18, 14, 18)
        ));
        formCard.setPreferredSize(new Dimension(380, 560));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        JLabel lblFormTitle = new JLabel("E-book Details");
        lblFormTitle.setFont(UIUtils.FONT_HEADER);
        lblFormTitle.setForeground(UIUtils.COLOR_TEXT_MAIN);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 4, 8, 4);
        formCard.add(lblFormTitle, gbc);

        // Ebook ID (Read-only)
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.insets = new Insets(2, 4, 1, 4);
        JLabel lblId = new JLabel("E-book ID (Auto)");
        lblId.setFont(UIUtils.FONT_BOLD);
        lblId.setForeground(UIUtils.COLOR_TEXT_MUTED);
        formCard.add(lblId, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(1, 4, 6, 4);
        txtEbookId = new JTextField(15);
        txtEbookId.setEditable(false);
        txtEbookId.setBackground(new Color(241, 245, 249));
        UIUtils.styleTextField(txtEbookId);
        formCard.add(txtEbookId, gbc);

        // Title
        gbc.gridy = 3;
        gbc.insets = new Insets(2, 4, 1, 4);
        JLabel lblTitle = new JLabel("Title *");
        lblTitle.setFont(UIUtils.FONT_BOLD);
        lblTitle.setForeground(UIUtils.COLOR_TEXT_MUTED);
        formCard.add(lblTitle, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(1, 4, 6, 4);
        txtTitle = new JTextField(15);
        UIUtils.styleTextField(txtTitle);
        formCard.add(txtTitle, gbc);

        // Author
        gbc.gridy = 5;
        gbc.insets = new Insets(2, 4, 1, 4);
        JLabel lblAuthor = new JLabel("Author *");
        lblAuthor.setFont(UIUtils.FONT_BOLD);
        lblAuthor.setForeground(UIUtils.COLOR_TEXT_MUTED);
        formCard.add(lblAuthor, gbc);

        gbc.gridy = 6;
        gbc.insets = new Insets(1, 4, 6, 4);
        txtAuthor = new JTextField(15);
        UIUtils.styleTextField(txtAuthor);
        formCard.add(txtAuthor, gbc);

        // Category (Dropdown)
        gbc.gridy = 7;
        gbc.insets = new Insets(2, 4, 1, 4);
        JLabel lblCat = new JLabel("Category *");
        lblCat.setFont(UIUtils.FONT_BOLD);
        lblCat.setForeground(UIUtils.COLOR_TEXT_MUTED);
        formCard.add(lblCat, gbc);

        gbc.gridy = 8;
        gbc.insets = new Insets(1, 4, 6, 4);
        cmbCategory = new JComboBox<>(DEFAULT_CATEGORIES);
        cmbCategory.setEditable(true);
        UIUtils.styleComboBox(cmbCategory);
        formCard.add(cmbCategory, gbc);

        // ISBN
        gbc.gridy = 9;
        gbc.insets = new Insets(2, 4, 1, 4);
        JLabel lblIsbn = new JLabel("ISBN * (Unique)");
        lblIsbn.setFont(UIUtils.FONT_BOLD);
        lblIsbn.setForeground(UIUtils.COLOR_TEXT_MUTED);
        formCard.add(lblIsbn, gbc);

        gbc.gridy = 10;
        gbc.insets = new Insets(1, 4, 6, 4);
        txtIsbn = new JTextField(15);
        UIUtils.styleTextField(txtIsbn);
        formCard.add(txtIsbn, gbc);

        // Publisher
        gbc.gridy = 11;
        gbc.insets = new Insets(2, 4, 1, 4);
        JLabel lblPublisher = new JLabel("Publisher *");
        lblPublisher.setFont(UIUtils.FONT_BOLD);
        lblPublisher.setForeground(UIUtils.COLOR_TEXT_MUTED);
        formCard.add(lblPublisher, gbc);

        gbc.gridy = 12;
        gbc.insets = new Insets(1, 4, 6, 4);
        txtPublisher = new JTextField(15);
        UIUtils.styleTextField(txtPublisher);
        formCard.add(txtPublisher, gbc);

        // Publication Year
        gbc.gridy = 13;
        gbc.insets = new Insets(2, 4, 1, 4);
        JLabel lblYear = new JLabel("Publication Year * (e.g. 2024)");
        lblYear.setFont(UIUtils.FONT_BOLD);
        lblYear.setForeground(UIUtils.COLOR_TEXT_MUTED);
        formCard.add(lblYear, gbc);

        gbc.gridy = 14;
        gbc.insets = new Insets(1, 4, 6, 4);
        txtYear = new JTextField(15);
        UIUtils.styleTextField(txtYear);
        formCard.add(txtYear, gbc);

        // E-book URL
        gbc.gridy = 15;
        gbc.insets = new Insets(2, 4, 1, 4);
        JLabel lblUrl = new JLabel("E-book Download/View URL");
        lblUrl.setFont(UIUtils.FONT_BOLD);
        lblUrl.setForeground(UIUtils.COLOR_TEXT_MUTED);
        formCard.add(lblUrl, gbc);

        gbc.gridy = 16;
        gbc.insets = new Insets(1, 4, 6, 4);
        txtUrl = new JTextField(15);
        UIUtils.styleTextField(txtUrl);
        formCard.add(txtUrl, gbc);

        // Available checkbox
        gbc.gridy = 17;
        gbc.insets = new Insets(2, 4, 10, 4);
        chkAvailable = new JCheckBox("Available for borrowing", true);
        chkAvailable.setFont(UIUtils.FONT_BOLD);
        chkAvailable.setOpaque(false);
        formCard.add(chkAvailable, gbc);

        // Action Buttons
        gbc.gridy = 18;
        gbc.insets = new Insets(6, 4, 2, 4);
        JPanel buttonGrid = new JPanel(new GridLayout(2, 2, 8, 8));
        buttonGrid.setOpaque(false);

        btnAdd = UIUtils.createPrimaryButton("Add E-book");
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

        JLabel lblSearchIcon = new JLabel("Search E-books:");
        lblSearchIcon.setFont(UIUtils.FONT_BOLD);
        lblSearchIcon.setForeground(UIUtils.COLOR_TEXT_MUTED);

        txtSearch = new JTextField();
        UIUtils.styleTextField(txtSearch);
        txtSearch.setToolTipText("Search title, author, category, or ISBN");

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

        // Table
        String[] columnNames = {"ID", "Title", "Author", "Category", "ISBN", "Publisher", "Year", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        ebookTable = new JTable(tableModel);
        UIUtils.styleTable(ebookTable);
        ebookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ebookTable.getColumnModel().getColumn(7).setCellRenderer(UIUtils.createStatusRenderer());

        // Column widths
        ebookTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        ebookTable.getColumnModel().getColumn(1).setPreferredWidth(210);
        ebookTable.getColumnModel().getColumn(2).setPreferredWidth(140);
        ebookTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        ebookTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        ebookTable.getColumnModel().getColumn(5).setPreferredWidth(130);
        ebookTable.getColumnModel().getColumn(6).setPreferredWidth(60);
        ebookTable.getColumnModel().getColumn(7).setPreferredWidth(90);

        JScrollPane scrollPane = new JScrollPane(ebookTable);
        scrollPane.setBorder(new LineBorder(UIUtils.COLOR_BORDER, 1, true));
        tableContainer.add(scrollPane, BorderLayout.CENTER);
        lblEmptyState = new JLabel("No e-books found.", SwingConstants.CENTER);
        lblEmptyState.setFont(UIUtils.FONT_REGULAR);
        lblEmptyState.setForeground(UIUtils.COLOR_TEXT_MUTED);
        lblEmptyState.setBorder(new EmptyBorder(10, 0, 2, 0));
        tableContainer.add(lblEmptyState, BorderLayout.SOUTH);

        contentPanel.add(tableContainer, BorderLayout.CENTER);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);

        // ================= Event Listeners =================
        ebookTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && ebookTable.getSelectedRow() != -1) {
                populateFormFromSelectedRow();
            }
        });

        btnAdd.addActionListener(e -> handleAddEbook());
        btnUpdate.addActionListener(e -> handleUpdateEbook());
        btnDelete.addActionListener(e -> handleDeleteEbook());
        btnClear.addActionListener(e -> clearForm());
        btnSearch.addActionListener(e -> handleSearch());
        btnViewAll.addActionListener(e -> {
            txtSearch.setText("");
            loadAllEbooks();
        });

        txtSearch.addActionListener(e -> handleSearch());
    }

    private void loadAllEbooks() {
        tableModel.setRowCount(0);
        try {
            List<Ebook> list = ebookService.getAllEbooks();
            for (Ebook b : list) {
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
            updateEmptyState();
        } catch (Exception e) {
            UIUtils.showError(this, "Failed to load e-books: " + e.getMessage());
        }
    }

    private void handleSearch() {
        String query = txtSearch.getText().trim();
        tableModel.setRowCount(0);
        try {
            // Search across title, author, category, or isbn
            List<Ebook> list = ebookService.searchEbooks(query, query, null, query, false);
            for (Ebook b : list) {
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
            updateEmptyState();
        } catch (Exception e) {
            UIUtils.showError(this, "Search error: " + e.getMessage());
        }
    }

    private void populateFormFromSelectedRow() {
        int selectedRow = ebookTable.getSelectedRow();
        if (selectedRow != -1) {
            int row = ebookTable.convertRowIndexToModel(selectedRow);
            int ebookId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
            Ebook ebook = ebookService.getEbookById(ebookId);
            if (ebook != null) {
                txtEbookId.setText(String.valueOf(ebook.getEbookId()));
                txtTitle.setText(ebook.getTitle());
                txtAuthor.setText(ebook.getAuthor());
                cmbCategory.setSelectedItem(ebook.getCategory());
                txtIsbn.setText(ebook.getIsbn());
                txtPublisher.setText(ebook.getPublisher());
                txtYear.setText(String.valueOf(ebook.getPublicationYear()));
                txtUrl.setText(ebook.getEbookUrl() != null ? ebook.getEbookUrl() : "");
                chkAvailable.setSelected(ebook.isAvailable());
            }
        }
    }

    private void handleAddEbook() {
        try {
            String title = txtTitle.getText().trim();
            String author = txtAuthor.getText().trim();
            String category = cmbCategory.getSelectedItem() != null ? cmbCategory.getSelectedItem().toString().trim() : "";
            String isbn = txtIsbn.getText().trim();
            String publisher = txtPublisher.getText().trim();
            String yearStr = txtYear.getText().trim();
            String url = txtUrl.getText().trim();
            boolean available = chkAvailable.isSelected();

            if (yearStr.isEmpty()) {
                throw new IllegalArgumentException("Publication year is required.");
            }
            int year = Integer.parseInt(yearStr);

            Ebook ebook = new Ebook(0, title, author, category, isbn, publisher, year, url, available);
            boolean success = ebookService.registerEbook(ebook);

            if (success) {
                UIUtils.showSuccess(this, "E-book registered successfully with ID: " + ebook.getEbookId());
                clearForm();
                loadAllEbooks();
                if (parentDashboard != null) {
                    parentDashboard.refreshMetrics();
                }
            } else {
                UIUtils.showError(this, "Could not insert e-book into MySQL.");
            }
        } catch (NumberFormatException ex) {
            UIUtils.showWarning(this, "Publication year must be a valid integer number (e.g. 2024).");
        } catch (IllegalArgumentException ex) {
            UIUtils.showWarning(this, ex.getMessage());
        } catch (Exception ex) {
            UIUtils.showError(this, "Database error: " + ex.getMessage());
        }
    }

    private void handleUpdateEbook() {
        String idStr = txtEbookId.getText().trim();
        if (idStr.isEmpty()) {
            UIUtils.showWarning(this, "Please select an e-book from the table to update.");
            return;
        }

        try {
            int ebookId = Integer.parseInt(idStr);
            String title = txtTitle.getText().trim();
            String author = txtAuthor.getText().trim();
            String category = cmbCategory.getSelectedItem() != null ? cmbCategory.getSelectedItem().toString().trim() : "";
            String isbn = txtIsbn.getText().trim();
            String publisher = txtPublisher.getText().trim();
            String yearStr = txtYear.getText().trim();
            String url = txtUrl.getText().trim();
            boolean available = chkAvailable.isSelected();

            int year = Integer.parseInt(yearStr);

            Ebook ebook = new Ebook(ebookId, title, author, category, isbn, publisher, year, url, available);
            boolean success = ebookService.updateEbook(ebook);

            if (success) {
                UIUtils.showSuccess(this, "E-book ID " + ebookId + " updated successfully!");
                loadAllEbooks();
                if (parentDashboard != null) {
                    parentDashboard.refreshMetrics();
                }
            } else {
                UIUtils.showError(this, "Could not update e-book record.");
            }
        } catch (NumberFormatException ex) {
            UIUtils.showWarning(this, "Publication year must be a valid integer number.");
        } catch (IllegalArgumentException ex) {
            UIUtils.showWarning(this, ex.getMessage());
        } catch (Exception ex) {
            UIUtils.showError(this, "Database error: " + ex.getMessage());
        }
    }

    private void handleDeleteEbook() {
        String idStr = txtEbookId.getText().trim();
        if (idStr.isEmpty()) {
            UIUtils.showWarning(this, "Please select an e-book from the table to delete.");
            return;
        }

        int ebookId = Integer.parseInt(idStr);
        String title = txtTitle.getText().trim();

        if (UIUtils.showConfirm(this, "Are you sure you want to delete e-book '" + title + "' (ID: " + ebookId + ")?")) {
            try {
                boolean success = ebookService.deleteEbook(ebookId);
                if (success) {
                    UIUtils.showSuccess(this, "E-book deleted successfully.");
                    clearForm();
                    loadAllEbooks();
                    if (parentDashboard != null) {
                        parentDashboard.refreshMetrics();
                    }
                } else {
                    UIUtils.showError(this, "Could not delete e-book record.");
                }
            } catch (IllegalStateException ex) {
                UIUtils.showWarning(this, ex.getMessage());
            } catch (Exception ex) {
                UIUtils.showError(this, "Database error: " + ex.getMessage());
            }
        }
    }

    private void clearForm() {
        txtEbookId.setText("");
        txtTitle.setText("");
        txtAuthor.setText("");
        cmbCategory.setSelectedIndex(0);
        txtIsbn.setText("");
        txtPublisher.setText("");
        txtYear.setText("");
        txtUrl.setText("");
        chkAvailable.setSelected(true);
        ebookTable.clearSelection();
    }

    private void updateEmptyState() {
        lblEmptyState.setVisible(tableModel.getRowCount() == 0);
    }
}
