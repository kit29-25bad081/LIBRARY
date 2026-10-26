package com.digitallibrary.dao;

import com.digitallibrary.database.DBConnection;
import com.digitallibrary.model.IssueRecord;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for IssueRecord entities.
 * Handles database operations for issuing and returning e-books.
 */
public class IssueDAO {

    /**
     * Inserts an issue record using an active transaction connection.
     * @param conn active transaction Connection
     * @param record IssueRecord with details
     * @return true if inserted
     * @throws SQLException on database error
     */
    public boolean insertIssue(Connection conn, IssueRecord record) throws SQLException {
        String sql = "INSERT INTO issue_records (user_id, ebook_id, issue_date, due_date, status) " +
                     "VALUES (?, ?, ?, ?, 'ISSUED')";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, record.getUserId());
            stmt.setInt(2, record.getEbookId());
            stmt.setDate(3, Date.valueOf(record.getIssueDate()));
            stmt.setDate(4, Date.valueOf(record.getDueDate()));

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        record.setIssueId(rs.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Updates an issue record as returned using an active transaction connection.
     * @param conn active transaction Connection
     * @param issueId issue transaction ID
     * @param returnDate date of return
     * @return true if updated
     * @throws SQLException on database error
     */
    public boolean returnIssue(Connection conn, int issueId, LocalDate returnDate) throws SQLException {
        String sql = "UPDATE issue_records SET return_date = ?, status = 'RETURNED' WHERE issue_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(returnDate));
            stmt.setInt(2, issueId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Finds an issue record by ID with joined user and book information.
     * @param issueId issue ID
     * @return IssueRecord or null
     */
    public IssueRecord findById(int issueId) {
        String sql = "SELECT ir.issue_id, ir.user_id, ir.ebook_id, ir.issue_date, ir.due_date, ir.return_date, ir.status, " +
                     "u.name AS user_name, u.email AS user_email, b.title AS ebook_title, b.isbn AS ebook_isbn " +
                     "FROM issue_records ir " +
                     "JOIN users u ON ir.user_id = u.user_id " +
                     "JOIN ebooks b ON ir.ebook_id = b.ebook_id " +
                     "WHERE ir.issue_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, issueId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractIssueRecordWithJoins(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding issue record by ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Finds an active issue record for a specific e-book.
     * @param ebookId e-book ID
     * @return IssueRecord or null
     */
    public IssueRecord findActiveIssueByBookId(int ebookId) {
        String sql = "SELECT ir.issue_id, ir.user_id, ir.ebook_id, ir.issue_date, ir.due_date, ir.return_date, ir.status, " +
                     "u.name AS user_name, u.email AS user_email, b.title AS ebook_title, b.isbn AS ebook_isbn " +
                     "FROM issue_records ir " +
                     "JOIN users u ON ir.user_id = u.user_id " +
                     "JOIN ebooks b ON ir.ebook_id = b.ebook_id " +
                     "WHERE ir.ebook_id = ? AND ir.status = 'ISSUED' " +
                     "ORDER BY ir.issue_id DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ebookId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractIssueRecordWithJoins(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding active issue for ebook: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves issue history filtered by status ('ALL', 'ISSUED', 'RETURNED').
     * @param filter status filter
     * @return list of IssueRecord objects
     */
    public List<IssueRecord> getIssueRecords(String filter) {
        List<IssueRecord> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT ir.issue_id, ir.user_id, ir.ebook_id, ir.issue_date, ir.due_date, ir.return_date, ir.status, " +
                "u.name AS user_name, u.email AS user_email, b.title AS ebook_title, b.isbn AS ebook_isbn " +
                "FROM issue_records ir " +
                "JOIN users u ON ir.user_id = u.user_id " +
                "JOIN ebooks b ON ir.ebook_id = b.ebook_id "
        );

        if ("ISSUED".equalsIgnoreCase(filter)) {
            sql.append("WHERE ir.status = 'ISSUED' ");
        } else if ("RETURNED".equalsIgnoreCase(filter)) {
            sql.append("WHERE ir.status = 'RETURNED' ");
        }

        sql.append("ORDER BY ir.issue_id DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString());
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(extractIssueRecordWithJoins(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting issue records: " + e.getMessage());
        }
        return list;
    }

    private IssueRecord extractIssueRecordWithJoins(ResultSet rs) throws SQLException {
        Date issueSqlDate = rs.getDate("issue_date");
        Date dueSqlDate = rs.getDate("due_date");
        Date returnSqlDate = rs.getDate("return_date");

        IssueRecord rec = new IssueRecord(
                rs.getInt("issue_id"),
                rs.getInt("user_id"),
                rs.getInt("ebook_id"),
                issueSqlDate != null ? issueSqlDate.toLocalDate() : null,
                dueSqlDate != null ? dueSqlDate.toLocalDate() : null,
                returnSqlDate != null ? returnSqlDate.toLocalDate() : null,
                rs.getString("status")
        );
        rec.setUserName(rs.getString("user_name"));
        rec.setUserEmail(rs.getString("user_email"));
        rec.setEbookTitle(rs.getString("ebook_title"));
        rec.setEbookIsbn(rs.getString("ebook_isbn"));
        return rec;
    }
}
