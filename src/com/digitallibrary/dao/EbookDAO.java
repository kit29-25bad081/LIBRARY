package com.digitallibrary.dao;

import com.digitallibrary.database.DBConnection;
import com.digitallibrary.model.Ebook;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Ebook entities.
 * Handles database operations using JDBC PreparedStatement.
 */
public class EbookDAO {

    /**
     * Adds a new e-book to the catalog.
     * @param ebook Ebook object
     * @return true if added, false otherwise
     */
    public boolean addEbook(Ebook ebook) {
        String sql = "INSERT INTO ebooks (title, author, category, isbn, publisher, publication_year, ebook_url, available) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, ebook.getTitle());
            stmt.setString(2, ebook.getAuthor());
            stmt.setString(3, ebook.getCategory());
            stmt.setString(4, ebook.getIsbn());
            stmt.setString(5, ebook.getPublisher());
            stmt.setInt(6, ebook.getPublicationYear());
            stmt.setString(7, ebook.getEbookUrl());
            stmt.setBoolean(8, ebook.isAvailable());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        ebook.setEbookId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding ebook: " + e.getMessage());
        }
        return false;
    }

    /**
     * Updates an existing e-book in the catalog.
     * @param ebook Ebook object with updated values
     * @return true if updated, false otherwise
     */
    public boolean updateEbook(Ebook ebook) {
        String sql = "UPDATE ebooks SET title = ?, author = ?, category = ?, isbn = ?, publisher = ?, " +
                     "publication_year = ?, ebook_url = ?, available = ? WHERE ebook_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ebook.getTitle());
            stmt.setString(2, ebook.getAuthor());
            stmt.setString(3, ebook.getCategory());
            stmt.setString(4, ebook.getIsbn());
            stmt.setString(5, ebook.getPublisher());
            stmt.setInt(6, ebook.getPublicationYear());
            stmt.setString(7, ebook.getEbookUrl());
            stmt.setBoolean(8, ebook.isAvailable());
            stmt.setInt(9, ebook.getEbookId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating ebook: " + e.getMessage());
        }
        return false;
    }

    /**
     * Deletes an e-book by ID.
     * @param ebookId e-book ID
     * @return true if deleted, false otherwise
     */
    public boolean deleteEbook(int ebookId) {
        String sql = "DELETE FROM ebooks WHERE ebook_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ebookId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting ebook: " + e.getMessage());
        }
        return false;
    }

    /**
     * Finds an e-book by its ID.
     * @param ebookId e-book ID
     * @return Ebook object or null
     */
    public Ebook findById(int ebookId) {
        String sql = "SELECT ebook_id, title, author, category, isbn, publisher, publication_year, ebook_url, available, created_at " +
                     "FROM ebooks WHERE ebook_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ebookId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractEbookFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding ebook by ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Finds an e-book by its ISBN.
     * @param isbn ISBN number
     * @return Ebook object or null
     */
    public Ebook findByIsbn(String isbn) {
        String sql = "SELECT ebook_id, title, author, category, isbn, publisher, publication_year, ebook_url, available, created_at " +
                     "FROM ebooks WHERE isbn = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, isbn);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractEbookFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding ebook by ISBN: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves all e-books ordered by title.
     * @return list of Ebook objects
     */
    public List<Ebook> getAllEbooks() {
        List<Ebook> list = new ArrayList<>();
        String sql = "SELECT ebook_id, title, author, category, isbn, publisher, publication_year, ebook_url, available, created_at " +
                     "FROM ebooks ORDER BY ebook_id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(extractEbookFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all ebooks: " + e.getMessage());
        }
        return list;
    }

    /**
     * Searches e-books dynamically using PreparedStatement with filtering for Title, Author, Category, ISBN, and Availability.
     * @param title title search keyword
     * @param author author search keyword
     * @param category category search keyword
     * @param isbn isbn search keyword
     * @param onlyAvailable filter for available e-books only
     * @return list of matching Ebook objects
     */
    public List<Ebook> searchEbooks(String title, String author, String category, String isbn, boolean onlyAvailable) {
        List<Ebook> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT ebook_id, title, author, category, isbn, publisher, publication_year, ebook_url, available, created_at FROM ebooks WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (title != null && !title.trim().isEmpty()) {
            sql.append("AND title LIKE ? ");
            params.add("%" + title.trim() + "%");
        }
        if (author != null && !author.trim().isEmpty()) {
            sql.append("AND author LIKE ? ");
            params.add("%" + author.trim() + "%");
        }
        if (category != null && !category.trim().isEmpty() && !category.equalsIgnoreCase("All Categories")) {
            sql.append("AND category LIKE ? ");
            params.add("%" + category.trim() + "%");
        }
        if (isbn != null && !isbn.trim().isEmpty()) {
            sql.append("AND isbn LIKE ? ");
            params.add("%" + isbn.trim() + "%");
        }
        if (onlyAvailable) {
            sql.append("AND available = TRUE ");
        }

        sql.append("ORDER BY ebook_id ASC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(extractEbookFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching ebooks: " + e.getMessage());
        }
        return list;
    }

    /**
     * Updates an e-book's availability status within a given transaction Connection.
     * @param conn database connection participating in the transaction
     * @param ebookId e-book ID
     * @param available new availability flag
     * @return true if updated
     * @throws SQLException on database error
     */
    public boolean updateAvailability(Connection conn, int ebookId, boolean available) throws SQLException {
        String sql = "UPDATE ebooks SET available = ? WHERE ebook_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, available);
            stmt.setInt(2, ebookId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Returns total e-book count.
     */
    public int getTotalEbooksCount() {
        String sql = "SELECT COUNT(*) FROM ebooks";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting total ebooks count: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Returns total available e-book count.
     */
    public int getAvailableEbooksCount() {
        String sql = "SELECT COUNT(*) FROM ebooks WHERE available = TRUE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting available ebooks count: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Returns total issued e-book count.
     */
    public int getIssuedEbooksCount() {
        String sql = "SELECT COUNT(*) FROM ebooks WHERE available = FALSE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting issued ebooks count: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Checks if an e-book has an active issue record.
     */
    public boolean hasActiveIssue(int ebookId) {
        String sql = "SELECT COUNT(*) FROM issue_records WHERE ebook_id = ? AND status = 'ISSUED'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ebookId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking active issue for ebook: " + e.getMessage());
        }
        return false;
    }

    private Ebook extractEbookFromResultSet(ResultSet rs) throws SQLException {
        return new Ebook(
                rs.getInt("ebook_id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("category"),
                rs.getString("isbn"),
                rs.getString("publisher"),
                rs.getInt("publication_year"),
                rs.getString("ebook_url"),
                rs.getBoolean("available"),
                rs.getTimestamp("created_at")
        );
    }
}
