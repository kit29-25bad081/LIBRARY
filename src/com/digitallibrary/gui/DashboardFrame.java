package com.digitallibrary.gui;

import com.digitallibrary.service.AuthService;
import com.digitallibrary.service.EbookService;
import com.digitallibrary.service.IssueService;
import com.digitallibrary.service.UserService;
import com.digitallibrary.model.IssueRecord;
import com.digitallibrary.database.DBConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.time.LocalDate;

/**
 * Main Dashboard window of the Digital Library Management System.
 * Displays real-time metrics and navigation buttons to all sub-modules.
 */
public class DashboardFrame extends JFrame {

    private final UserService userService;
    private final EbookService ebookService;
    private final AuthService authService;
    private final IssueService issueService;

    // Stat metric value labels
    private JLabel lblTotalUsers;
    private JLabel lblTotalEbooks;
    private JLabel lblAvailableEbooks;
    private JLabel lblIssuedEbooks;
    private JLabel lblDatabaseStatus;
    private JPanel recentActivityPanel;

    public DashboardFrame() {
        this.userService = new UserService();
        this.ebookService = new EbookService();
        this.authService = new AuthService();
        this.issueService = new IssueService();
        initComponents();
        refreshMetrics();
        refreshRecentActivity();
    }

    private void initComponents() {
        setTitle("DIGITAL LIBRARY MANAGEMENT SYSTEM - Dashboard");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                handleExit();
            }
        });
        setSize(1220, 790);
        setMinimumSize(new Dimension(980, 660));
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIUtils.COLOR_BG);

        JPanel sidebar = createSidebar();
        mainPanel.add(sidebar, BorderLayout.WEST);

        JPanel content = new JPanel(new BorderLayout(0, 18));
        content.setBackground(UIUtils.COLOR_BG);
        content.setBorder(new EmptyBorder(22, 26, 14, 26));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Dashboard");
        title.setFont(UIUtils.FONT_TITLE);
        title.setForeground(UIUtils.COLOR_TEXT_MAIN);
        JLabel subtitle = new JLabel("Your library at a glance");
        subtitle.setFont(UIUtils.FONT_REGULAR);
        subtitle.setForeground(UIUtils.COLOR_TEXT_MUTED);
        heading.add(title);
        heading.add(Box.createVerticalStrut(4));
        heading.add(subtitle);
        headerPanel.add(heading, BorderLayout.WEST);

        JPanel profile = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        profile.setOpaque(false);
        JLabel avatar = new JLabel("A", SwingConstants.CENTER);
        avatar.setOpaque(true);
        avatar.setBackground(new Color(219, 234, 254));
        avatar.setForeground(UIUtils.COLOR_PRIMARY);
        avatar.setFont(UIUtils.FONT_BOLD);
        avatar.setPreferredSize(new Dimension(38, 38));
        JPanel accountCopy = new JPanel();
        accountCopy.setOpaque(false);
        accountCopy.setLayout(new BoxLayout(accountCopy, BoxLayout.Y_AXIS));
        JLabel adminName = new JLabel(AuthService.getCurrentAdminUsername());
        adminName.setFont(UIUtils.FONT_BOLD);
        adminName.setForeground(UIUtils.COLOR_TEXT_MAIN);
        JLabel adminRole = new JLabel("Administrator");
        adminRole.setFont(UIUtils.FONT_SMALL);
        adminRole.setForeground(UIUtils.COLOR_TEXT_MUTED);
        accountCopy.add(adminName);
        accountCopy.add(adminRole);
        profile.add(avatar);
        profile.add(accountCopy);
        headerPanel.add(profile, BorderLayout.EAST);
        content.add(headerPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(UIUtils.COLOR_BG);

        JLabel welcome = new JLabel("Good day, " + AuthService.getCurrentAdminUsername());
        welcome.setFont(UIUtils.FONT_HEADER);
        welcome.setForeground(UIUtils.COLOR_TEXT_MAIN);
        welcome.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(welcome);
        JLabel welcomeSub = new JLabel("Here is the latest overview of your library.");
        welcomeSub.setFont(UIUtils.FONT_REGULAR);
        welcomeSub.setForeground(UIUtils.COLOR_TEXT_MUTED);
        welcomeSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(Box.createVerticalStrut(4));
        centerPanel.add(welcomeSub);
        centerPanel.add(Box.createVerticalStrut(20));

        JButton btnRefresh = UIUtils.createSecondaryButton("Refresh Stats");
        btnRefresh.addActionListener(e -> {
            refreshMetrics();
            refreshRecentActivity();
        });

        JPanel statsHeading = new JPanel(new BorderLayout());
        statsHeading.setOpaque(false);
        statsHeading.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        JLabel statsTitle = new JLabel("Library overview");
        statsTitle.setFont(UIUtils.FONT_SUBHEADER);
        statsTitle.setForeground(UIUtils.COLOR_TEXT_MAIN);
        statsHeading.add(statsTitle, BorderLayout.WEST);
        statsHeading.add(btnRefresh, BorderLayout.EAST);
        statsHeading.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(statsHeading);
        centerPanel.add(Box.createVerticalStrut(12));

        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 14, 0));
        statsPanel.setOpaque(false);
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 124));
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblTotalUsers = new JLabel("0", SwingConstants.CENTER);
        lblTotalEbooks = new JLabel("0", SwingConstants.CENTER);
        lblAvailableEbooks = new JLabel("0", SwingConstants.CENTER);
        lblIssuedEbooks = new JLabel("0", SwingConstants.CENTER);

        statsPanel.add(createStatCard("USERS", "♙", lblTotalUsers, UIUtils.COLOR_PRIMARY_HOVER));
        statsPanel.add(createStatCard("E-BOOKS", "▤", lblTotalEbooks, UIUtils.COLOR_ACCENT));
        statsPanel.add(createStatCard("AVAILABLE", "✓", lblAvailableEbooks, UIUtils.COLOR_SUCCESS));
        statsPanel.add(createStatCard("ON LOAN", "↗", lblIssuedEbooks, UIUtils.COLOR_WARNING));

        centerPanel.add(statsPanel);
        centerPanel.add(Box.createVerticalStrut(22));

        JLabel actionsTitle = new JLabel("Quick actions");
        actionsTitle.setFont(UIUtils.FONT_SUBHEADER);
        actionsTitle.setForeground(UIUtils.COLOR_TEXT_MAIN);
        actionsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(actionsTitle);
        centerPanel.add(Box.createVerticalStrut(10));
        JPanel actions = new JPanel(new GridLayout(1, 3, 14, 0));
        actions.setOpaque(false);
        actions.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));
        actions.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton addUser = UIUtils.createPrimaryButton("+  Add User");
        JButton addBook = UIUtils.createStyledButton("+  Add E-Book", UIUtils.COLOR_ACCENT, Color.WHITE);
        JButton issueBook = UIUtils.createSuccessButton("Issue a Book");
        Dimension quickActionSize = new Dimension(150, 52);
        addUser.setPreferredSize(quickActionSize);
        addBook.setPreferredSize(quickActionSize);
        issueBook.setPreferredSize(quickActionSize);
        addUser.addActionListener(e -> new UserManagementFrame(this).setVisible(true));
        addBook.addActionListener(e -> new EbookManagementFrame(this).setVisible(true));
        issueBook.addActionListener(e -> new IssueBookFrame(this).setVisible(true));
        actions.add(addUser);
        actions.add(addBook);
        actions.add(issueBook);
        centerPanel.add(actions);
        centerPanel.add(Box.createVerticalStrut(22));

        JPanel activityCard = UIUtils.createCardPanel();
        activityCard.setLayout(new BorderLayout(0, 12));
        activityCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        activityCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 360));
        JPanel activityHeading = new JPanel(new BorderLayout());
        activityHeading.setOpaque(false);
        JLabel activityTitle = new JLabel("Recent activity");
        activityTitle.setFont(UIUtils.FONT_SUBHEADER);
        activityTitle.setForeground(UIUtils.COLOR_TEXT_MAIN);
        JLabel activityCaption = new JLabel("Latest issue and return transactions");
        activityCaption.setFont(UIUtils.FONT_SMALL);
        activityCaption.setForeground(UIUtils.COLOR_TEXT_MUTED);
        JPanel activityCopy = new JPanel();
        activityCopy.setOpaque(false);
        activityCopy.setLayout(new BoxLayout(activityCopy, BoxLayout.Y_AXIS));
        activityCopy.add(activityTitle);
        activityCopy.add(Box.createVerticalStrut(3));
        activityCopy.add(activityCaption);
        activityHeading.add(activityCopy, BorderLayout.WEST);
        activityCard.add(activityHeading, BorderLayout.NORTH);
        recentActivityPanel = new JPanel();
        recentActivityPanel.setOpaque(false);
        recentActivityPanel.setLayout(new BoxLayout(recentActivityPanel, BoxLayout.Y_AXIS));
        activityCard.add(recentActivityPanel, BorderLayout.CENTER);
        centerPanel.add(activityCard);

        JScrollPane contentScroll = new JScrollPane(centerPanel);
        contentScroll.setBorder(null);
        contentScroll.getViewport().setBackground(UIUtils.COLOR_BG);
        content.add(contentScroll, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(4, 0, 0, 0));
        lblDatabaseStatus = new JLabel("● Checking database connection...");
        lblDatabaseStatus.setFont(UIUtils.FONT_SMALL);
        lblDatabaseStatus.setForeground(UIUtils.COLOR_TEXT_MUTED);
        JLabel lblVer = new JLabel("Digital Library  •  Desktop Edition");
        lblVer.setFont(UIUtils.FONT_SMALL);
        lblVer.setForeground(UIUtils.COLOR_TEXT_MUTED);
        footer.add(lblDatabaseStatus, BorderLayout.WEST);
        footer.add(lblVer, BorderLayout.EAST);
        content.add(footer, BorderLayout.SOUTH);
        mainPanel.add(content, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(Color.WHITE);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UIUtils.COLOR_BORDER));
        sidebar.setPreferredSize(new Dimension(228, 0));

        JPanel brand = new JPanel();
        brand.setBackground(Color.WHITE);
        brand.setBorder(new EmptyBorder(22, 18, 24, 16));
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        JLabel brandIcon = new JLabel("DIGITAL LIBRARY");
        brandIcon.setFont(UIUtils.FONT_SUBHEADER);
        brandIcon.setForeground(UIUtils.COLOR_PRIMARY);
        JLabel brandSub = new JLabel("MANAGEMENT SYSTEM");
        brandSub.setFont(UIUtils.FONT_SMALL);
        brandSub.setForeground(UIUtils.COLOR_TEXT_MUTED);
        brandSub.setBorder(new EmptyBorder(5, 28, 0, 0));
        brand.add(brandIcon);
        brand.add(brandSub);
        sidebar.add(brand, BorderLayout.NORTH);

        JPanel nav = new JPanel();
        nav.setBackground(Color.WHITE);
        nav.setBorder(new EmptyBorder(0, 12, 12, 12));
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.add(createSidebarButton("Dashboard", true, null));
        nav.add(Box.createVerticalStrut(6));
        nav.add(createSidebarButton("User Management", false,
                () -> new UserManagementFrame(this).setVisible(true)));
        nav.add(createSidebarButton("E-Book Management", false,
                () -> new EbookManagementFrame(this).setVisible(true)));
        nav.add(createSidebarButton("Search E-Book", false,
                () -> new SearchFrame(this).setVisible(true)));
        nav.add(createSidebarButton("Issue E-Book", false,
                () -> new IssueBookFrame(this).setVisible(true)));
        nav.add(createSidebarButton("Return E-Book", false,
                () -> new ReturnBookFrame(this).setVisible(true)));
        nav.add(createSidebarButton("Issue History", false,
                () -> new IssueHistoryFrame(this).setVisible(true)));
        sidebar.add(nav, BorderLayout.CENTER);

        JPanel accountActions = new JPanel();
        accountActions.setBackground(Color.WHITE);
        accountActions.setBorder(new EmptyBorder(12, 12, 18, 12));
        accountActions.setLayout(new BoxLayout(accountActions, BoxLayout.Y_AXIS));
        accountActions.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UIUtils.COLOR_BORDER),
                new EmptyBorder(12, 12, 18, 12)));
        accountActions.add(createSidebarButton("Log out", false, this::handleLogout));
        accountActions.add(createSidebarButton("Exit application", false, this::handleExit));
        sidebar.add(accountActions, BorderLayout.SOUTH);
        return sidebar;
    }

    private JButton createSidebarButton(String title, boolean active, Runnable action) {
        JButton button = new JButton(title);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(active ? UIUtils.FONT_BOLD : UIUtils.FONT_REGULAR);
        button.setForeground(active ? UIUtils.COLOR_PRIMARY : UIUtils.COLOR_TEXT_MUTED);
        button.setBackground(active ? new Color(239, 244, 255) : Color.WHITE);
        button.setBorder(new EmptyBorder(11, 12, 11, 8));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setToolTipText(title);
        if (active) {
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 3, 0, 0, UIUtils.COLOR_PRIMARY_HOVER),
                    new EmptyBorder(11, 9, 11, 8)));
        }
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!active) {
                    button.setBackground(UIUtils.COLOR_BG);
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(active ? new Color(239, 244, 255) : Color.WHITE);
            }
        });
        if (action != null) {
            button.addActionListener(e -> action.run());
        }
        return button;
    }

    private JPanel createStatCard(String title, String icon, JLabel valueLabel, Color accentColor) {
        JPanel card = UIUtils.createCardPanel();
        card.setLayout(new BorderLayout(10, 0));
        JPanel iconBadge = new JPanel(new GridBagLayout());
        iconBadge.setBackground(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 24));
        iconBadge.setPreferredSize(new Dimension(48, 48));
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Symbol", Font.BOLD, 22));
        iconLabel.setForeground(accentColor);
        iconBadge.add(iconLabel);

        JPanel labels = new JPanel();
        labels.setOpaque(false);
        labels.setLayout(new BoxLayout(labels, BoxLayout.Y_AXIS));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIUtils.FONT_SMALL);
        titleLabel.setForeground(UIUtils.COLOR_TEXT_MUTED);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(UIUtils.COLOR_TEXT_MAIN);
        labels.add(titleLabel);
        labels.add(Box.createVerticalStrut(7));
        labels.add(valueLabel);
        card.add(iconBadge, BorderLayout.WEST);
        card.add(labels, BorderLayout.CENTER);
        return card;
    }

    private JPanel createActivityRow(IssueRecord record) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(9, 0, 9, 0));
        String status = record.getStatus() == null ? "ISSUED" : record.getStatus();
        if ("ISSUED".equalsIgnoreCase(status) && record.getDueDate() != null &&
                LocalDate.now().isAfter(record.getDueDate())) {
            status = "OVERDUE";
        }
        JLabel marker = new JLabel("●");
        marker.setForeground("RETURNED".equalsIgnoreCase(status) ? UIUtils.COLOR_SUCCESS : UIUtils.COLOR_PRIMARY_HOVER);
        marker.setFont(UIUtils.FONT_SMALL);
        JPanel description = new JPanel();
        description.setOpaque(false);
        description.setLayout(new BoxLayout(description, BoxLayout.Y_AXIS));
        JLabel title = new JLabel((record.getUserName() != null ? record.getUserName() : "Member") +
                "  ·  " + (record.getEbookTitle() != null ? record.getEbookTitle() : "E-book"));
        title.setFont(UIUtils.FONT_BOLD);
        title.setForeground(UIUtils.COLOR_TEXT_MAIN);
        JLabel meta = new JLabel(status + "  ·  " +
                (record.getIssueDate() != null ? record.getIssueDate().toString() : "Date unavailable"));
        meta.setFont(UIUtils.FONT_SMALL);
        meta.setForeground(UIUtils.COLOR_TEXT_MUTED);
        description.add(title);
        description.add(Box.createVerticalStrut(4));
        description.add(meta);
        row.add(marker, BorderLayout.WEST);
        row.add(description, BorderLayout.CENTER);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        return row;
    }

    private void refreshRecentActivity() {
        recentActivityPanel.removeAll();
        List<IssueRecord> recent = issueService.getIssueHistory("ALL");
        int displayed = 0;
        for (IssueRecord record : recent) {
            if (displayed == 5) {
                break;
            }
            recentActivityPanel.add(createActivityRow(record));
            recentActivityPanel.add(new JSeparator());
            displayed++;
        }
        if (displayed == 0) {
            JLabel emptyState = new JLabel("No library transactions yet.");
            emptyState.setFont(UIUtils.FONT_REGULAR);
            emptyState.setForeground(UIUtils.COLOR_TEXT_MUTED);
            emptyState.setBorder(new EmptyBorder(12, 0, 8, 0));
            recentActivityPanel.add(emptyState);
        }
        recentActivityPanel.revalidate();
        recentActivityPanel.repaint();
    }

    /**
     * Refreshes all metrics from MySQL using JDBC via the Service layer.
     */
    public void refreshMetrics() {
        try {
            int users = userService.getTotalUsersCount();
            int ebooks = ebookService.getTotalEbooksCount();
            int available = ebookService.getAvailableEbooksCount();
            int issued = ebookService.getIssuedEbooksCount();

            lblTotalUsers.setText(String.valueOf(users));
            lblTotalEbooks.setText(String.valueOf(ebooks));
            lblAvailableEbooks.setText(String.valueOf(available));
            lblIssuedEbooks.setText(String.valueOf(issued));
            DBConnection.testConnection();
            lblDatabaseStatus.setText("●  Database connected  ·  MySQL localhost:3306  ·  digital_library");
            lblDatabaseStatus.setForeground(UIUtils.COLOR_SUCCESS);
        } catch (Exception e) {
            lblDatabaseStatus.setText("●  Database disconnected  ·  Check MySQL connection");
            lblDatabaseStatus.setForeground(UIUtils.COLOR_DANGER);
        }
    }

    private void handleLogout() {
        if (UIUtils.showConfirm(this, "Are you sure you want to log out?")) {
            authService.logout();
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
            this.dispose();
        }
    }

    private void handleExit() {
        if (UIUtils.showConfirm(this, "Are you sure you want to exit the Digital Library System?")) {
            System.exit(0);
        }
    }
}
