package com.digitallibrary.gui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * UI Utilities and design system styling for Java Swing frames.
 * Provides a clean, modern, cohesive academic desktop theme.
 */
public class UIUtils {

    // Palette
    public static final Color COLOR_PRIMARY = new Color(37, 99, 235);
    public static final Color COLOR_PRIMARY_HOVER = new Color(29, 78, 216);
    public static final Color COLOR_ACCENT = new Color(124, 58, 237);
    public static final Color COLOR_SECONDARY_BORDER = new Color(203, 213, 225);
    public static final Color COLOR_LIGHT_BUTTON = new Color(241, 245, 249);
    public static final Color COLOR_LIGHT_BUTTON_TEXT = new Color(30, 41, 59);
    public static final Color COLOR_DISABLED_BUTTON = new Color(226, 232, 240);
    public static final Color COLOR_DISABLED_TEXT = new Color(148, 163, 184);
    public static final Color COLOR_BG = new Color(248, 250, 252);
    public static final Color COLOR_CARD_BG = Color.WHITE;
    public static final Color COLOR_TEXT_MAIN = new Color(15, 23, 42);      // Slate 900
    public static final Color COLOR_TEXT_MUTED = new Color(100, 116, 139);  // Slate 500
    public static final Color COLOR_BORDER = new Color(226, 232, 240);      // Slate 200
    public static final Color COLOR_SUCCESS = new Color(22, 163, 74);
    public static final Color COLOR_DANGER = new Color(220, 38, 38);
    public static final Color COLOR_WARNING = new Color(245, 158, 11);

    // Fonts
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 25);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_SUBHEADER = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);

    /**
     * Creates a styled JButton with custom colors and mouse hover effects.
     */
    public static JButton createStyledButton(String text, Color bg, Color fg) {
        JButton button = new JButton(text);
        button.setFont(FONT_BOLD);
        button.setBackground(bg);
        Color visibleForeground = relativeLuminance(bg) > 0.4 && !hasReadableContrast(bg, fg)
                ? contrastingForeground(bg) : fg;
        Color normalBorderColor = bg.equals(Color.WHITE) ? COLOR_SECONDARY_BORDER : bg;
        button.setForeground(visibleForeground);
        button.setFocusPainted(true);
        button.setFocusTraversalKeysEnabled(true);
        button.setBorder(new CompoundBorder(new LineBorder(normalBorderColor, 1, true),
                new EmptyBorder(8, 16, 8, 16)));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setPreferredSize(new Dimension(130, 40));
        button.setRolloverEnabled(true);
        button.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (button.isEnabled()) {
                    button.setBorder(new CompoundBorder(new LineBorder(COLOR_PRIMARY_HOVER, 2, true),
                            new EmptyBorder(7, 15, 7, 15)));
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                button.setBorder(new CompoundBorder(new LineBorder(normalBorderColor, 1, true),
                        new EmptyBorder(8, 16, 8, 16)));
            }
        });

        Color hoverColor = bg.equals(COLOR_PRIMARY) ? COLOR_PRIMARY_HOVER :
                bg.equals(COLOR_DANGER) ? new Color(185, 28, 28) :
                        bg.equals(COLOR_SUCCESS) ? new Color(21, 128, 61) :
                                bg.equals(COLOR_ACCENT) ? new Color(109, 40, 217) :
                                        bg.equals(Color.WHITE) ? new Color(241, 245, 249) : bg.darker();

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(hoverColor);
                    button.setForeground(visibleForeground);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(bg);
                    button.setForeground(visibleForeground);
                }
            }
        });
        button.addPropertyChangeListener("enabled", e -> {
            if (button.isEnabled()) {
                button.setBackground(bg);
                button.setForeground(visibleForeground);
                button.setBorder(new CompoundBorder(new LineBorder(normalBorderColor, 1, true),
                        new EmptyBorder(8, 16, 8, 16)));
            } else {
                button.setBackground(COLOR_DISABLED_BUTTON);
                button.setForeground(COLOR_DISABLED_TEXT);
                button.setBorder(new CompoundBorder(new LineBorder(COLOR_SECONDARY_BORDER, 1, true),
                        new EmptyBorder(8, 16, 8, 16)));
            }
        });
        button.setToolTipText(text);

        return button;
    }

    /**
     * Creates a primary action button.
     */
    public static JButton createPrimaryButton(String text) {
        return createStyledButton(text, COLOR_PRIMARY, Color.WHITE);
    }

    /**
     * Creates a secondary action button.
     */
    public static JButton createSecondaryButton(String text) {
        JButton button = createStyledButton(text, Color.WHITE, new Color(30, 58, 138));
        button.setBorder(new CompoundBorder(new LineBorder(COLOR_SECONDARY_BORDER, 1, true),
                new EmptyBorder(8, 16, 8, 16)));
        return button;
    }

    /**
     * Creates a danger action button (e.g. Delete, Exit).
     */
    public static JButton createDangerButton(String text) {
        return createStyledButton(text, COLOR_DANGER, Color.WHITE);
    }

    /**
     * Creates a success action button.
     */
    public static JButton createSuccessButton(String text) {
        return createStyledButton(text, COLOR_SUCCESS, Color.WHITE);
    }

    public static JButton createLightButton(String text) {
        return createStyledButton(text, COLOR_LIGHT_BUTTON, COLOR_LIGHT_BUTTON_TEXT);
    }

    private static boolean hasReadableContrast(Color background, Color foreground) {
        double high = Math.max(relativeLuminance(background), relativeLuminance(foreground));
        double low = Math.min(relativeLuminance(background), relativeLuminance(foreground));
        return (high + 0.05) / (low + 0.05) >= 4.5;
    }

    private static Color contrastingForeground(Color background) {
        return relativeLuminance(background) > 0.179 ? COLOR_LIGHT_BUTTON_TEXT : Color.WHITE;
    }

    private static double relativeLuminance(Color color) {
        double red = color.getRed() / 255.0;
        double green = color.getGreen() / 255.0;
        double blue = color.getBlue() / 255.0;
        red = red <= 0.04045 ? red / 12.92 : Math.pow((red + 0.055) / 1.055, 2.4);
        green = green <= 0.04045 ? green / 12.92 : Math.pow((green + 0.055) / 1.055, 2.4);
        blue = blue <= 0.04045 ? blue / 12.92 : Math.pow((blue + 0.055) / 1.055, 2.4);
        return 0.2126 * red + 0.7152 * green + 0.0722 * blue;
    }

    /**
     * Styles a JTextField with padding and clean borders.
     */
    public static void styleTextField(JTextField textField) {
        textField.setFont(FONT_REGULAR);
        textField.setForeground(COLOR_TEXT_MAIN);
        textField.setBackground(Color.WHITE);
        textField.setCaretColor(COLOR_PRIMARY);
        textField.setBorder(new CompoundBorder(new LineBorder(COLOR_BORDER, 1, true), new EmptyBorder(8, 11, 8, 11)));
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                textField.setBorder(new CompoundBorder(new LineBorder(COLOR_PRIMARY_HOVER, 1, true),
                        new EmptyBorder(8, 11, 8, 11)));
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                textField.setBorder(new CompoundBorder(new LineBorder(COLOR_BORDER, 1, true),
                        new EmptyBorder(8, 11, 8, 11)));
            }
        });
    }

    public static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(FONT_REGULAR);
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(COLOR_TEXT_MAIN);
        comboBox.setBorder(new LineBorder(COLOR_BORDER, 1, true));
        comboBox.setFocusable(true);
    }

    /**
     * Styles a JTable with professional colors, row heights, and header aesthetics.
     */
    public static void styleTable(JTable table) {
        table.setFont(FONT_REGULAR);
        table.setRowHeight(38);
        table.setGridColor(new Color(241, 245, 249));
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(COLOR_TEXT_MAIN);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);

        // Header Styling
        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setBackground(new Color(239, 244, 251));
        header.setForeground(COLOR_TEXT_MUTED);
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(header.getWidth(), 42));

        // Default cell padding
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, col);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(Color.WHITE);
                    } else {
                        c.setBackground(new Color(248, 250, 252));
                    }
                }
                return c;
            }
        };
        table.setDefaultRenderer(Object.class, renderer);
    }

    public static TableCellRenderer createStatusRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean selected,
                                                          boolean focus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, selected, focus, row, column);
                String status = value == null ? "" : value.toString();
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setFont(FONT_BOLD);
                if (!selected) {
                    if ("AVAILABLE".equalsIgnoreCase(status) || "RETURNED".equalsIgnoreCase(status)) {
                        label.setForeground(new Color(21, 128, 61));
                        label.setBackground(new Color(220, 252, 231));
                    } else if ("OVERDUE".equalsIgnoreCase(status)) {
                        label.setForeground(COLOR_DANGER);
                        label.setBackground(new Color(254, 226, 226));
                    } else {
                        label.setForeground(new Color(180, 83, 9));
                        label.setBackground(new Color(254, 243, 199));
                    }
                }
                label.setOpaque(true);
                return label;
            }
        };
    }

    /**
     * Creates a header banner panel for top of windows.
     */
    public static JPanel createHeaderBanner(String title, String subtitle) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new CompoundBorder(new LineBorder(COLOR_BORDER, 1),
                new EmptyBorder(18, 24, 18, 24)));

        JPanel copy = new JPanel();
        copy.setOpaque(false);
        copy.setLayout(new BoxLayout(copy, BoxLayout.Y_AXIS));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(COLOR_TEXT_MAIN);

        copy.add(titleLabel);

        if (subtitle != null && !subtitle.isEmpty()) {
            JLabel subLabel = new JLabel(subtitle);
            subLabel.setFont(FONT_REGULAR);
            subLabel.setForeground(COLOR_TEXT_MUTED);
            subLabel.setBorder(new EmptyBorder(4, 0, 0, 0));
            copy.add(subLabel);
        }

        headerPanel.add(copy, BorderLayout.WEST);
        return headerPanel;
    }

    /**
     * Creates a styled card panel with background and border.
     */
    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(COLOR_CARD_BG);
        panel.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));
        return panel;
    }

    // Message Dialog helpers
    public static void showSuccess(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showWarning(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    public static boolean showConfirm(Component parent, String message) {
        int result = JOptionPane.showConfirmDialog(parent, message, "Confirm Action", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return result == JOptionPane.YES_OPTION;
    }
}
