package com.digitallibrary.service;

import com.digitallibrary.dao.EbookDAO;
import com.digitallibrary.dao.IssueDAO;
import com.digitallibrary.dao.UserDAO;
import com.digitallibrary.database.DBConnection;
import com.digitallibrary.model.Ebook;
import com.digitallibrary.model.IssueRecord;
import com.digitallibrary.model.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Service class handling issue and return transactions.
 * Enforces atomic database transactions with commit and rollback.
 */
public class IssueService {

    private final IssueDAO issueDAO;
    private final UserDAO userDAO;
    private final EbookDAO ebookDAO;

    public IssueService() {
        this.issueDAO = new IssueDAO();
        this.userDAO = new UserDAO();
        this.ebookDAO = new EbookDAO();
    }

    /**
     * Issues an e-book to a user using an atomic JDBC transaction.
     *
     * Rules enforced:
     * 1. Check user exists
     * 2. Check e-book exists
     * 3. Check e-book is available
     * 4. Issue Date = today
     * 5. Due Date = today + 14 days
     * 6. Transaction: Insert issue record & update e-book available = false
     *
     * @param userId user ID
     * @param ebookId e-book ID
     * @return created IssueRecord
     * @throws IllegalArgumentException or IllegalStateException or SQLException on failure
     */
    public IssueRecord issueBook(int userId, int ebookId) throws SQLException {
        // 1. Check user exists
        User user = userDAO.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User ID " + userId + " does not exist.");
        }

        // 2. Check e-book exists
        Ebook ebook = ebookDAO.findById(ebookId);
        if (ebook == null) {
            throw new IllegalArgumentException("E-book ID " + ebookId + " does not exist.");
        }

        // 3. Check e-book availability
        if (!ebook.isAvailable()) {
            throw new IllegalStateException("E-book '" + ebook.getTitle() + "' is currently issued and unavailable.");
        }

        LocalDate issueDate = LocalDate.now();
        LocalDate dueDate = issueDate.plusDays(14); // Exactly 14 days after issue date

        IssueRecord record = new IssueRecord(userId, ebookId, issueDate, dueDate);

        // Execute JDBC Transaction
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Begin transaction

            // Step A: Insert issue record
            boolean recordInserted = issueDAO.insertIssue(conn, record);
            if (!recordInserted) {
                throw new SQLException("Failed to create issue record in database.");
            }

            // Step B: Set e-book availability to FALSE
            boolean ebookUpdated = ebookDAO.updateAvailability(conn, ebookId, false);
            if (!ebookUpdated) {
                throw new SQLException("Failed to update e-book availability status.");
            }

            // Commit transaction
            conn.commit();

            // Populate joined fields for caller
            record.setUserName(user.getName());
            record.setUserEmail(user.getEmail());
            record.setEbookTitle(ebook.getTitle());
            record.setEbookIsbn(ebook.getIsbn());

            return record;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback transaction on failure
                } catch (SQLException ex) {
                    System.err.println("Transaction rollback failed: " + ex.getMessage());
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    System.err.println("Error resetting connection: " + ex.getMessage());
                }
            }
        }
    }

    /**
     * Returns an issued e-book using an atomic JDBC transaction.
     *
     * Rules enforced:
     * 1. Check issue record exists
     * 2. Check status is not already 'RETURNED'
     * 3. Set return date = today
     * 4. Change status = RETURNED
     * 5. Set e-book availability = TRUE
     *
     * @param issueId issue transaction ID
     * @return updated IssueRecord
     * @throws IllegalArgumentException or IllegalStateException or SQLException on failure
     */
    public IssueRecord returnBook(int issueId) throws SQLException {
        // 1. Check issue record exists
        IssueRecord record = issueDAO.findById(issueId);
        if (record == null) {
            throw new IllegalArgumentException("Issue record ID " + issueId + " not found.");
        }

        // 2. Check if already returned
        if ("RETURNED".equalsIgnoreCase(record.getStatus())) {
            throw new IllegalStateException("Issue record ID " + issueId + " is ALREADY RETURNED on " + record.getReturnDate() + ".");
        }

        LocalDate returnDate = LocalDate.now();

        // Execute JDBC Transaction
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Begin transaction

            // Step A: Update issue record return_date and status = RETURNED
            boolean returnUpdated = issueDAO.returnIssue(conn, issueId, returnDate);
            if (!returnUpdated) {
                throw new SQLException("Failed to update issue record return status.");
            }

            // Step B: Set e-book availability to TRUE
            boolean ebookUpdated = ebookDAO.updateAvailability(conn, record.getEbookId(), true);
            if (!ebookUpdated) {
                throw new SQLException("Failed to update e-book availability status.");
            }

            // Commit transaction
            conn.commit();

            record.setReturnDate(returnDate);
            record.setStatus("RETURNED");

            return record;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback transaction on failure
                } catch (SQLException ex) {
                    System.err.println("Transaction rollback failed: " + ex.getMessage());
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    System.err.println("Error resetting connection: " + ex.getMessage());
                }
            }
        }
    }

    /**
     * Finds an issue record by ID.
     */
    public IssueRecord getIssueRecordById(int issueId) {
        return issueDAO.findById(issueId);
    }

    /**
     * Retrieves issue history filtered by status ('ALL', 'ISSUED', 'RETURNED').
     */
    public List<IssueRecord> getIssueHistory(String filter) {
        return issueDAO.getIssueRecords(filter);
    }
}
