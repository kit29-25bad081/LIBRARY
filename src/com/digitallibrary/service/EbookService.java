package com.digitallibrary.service;

import com.digitallibrary.dao.EbookDAO;
import com.digitallibrary.model.Ebook;

import java.time.Year;
import java.util.List;

/**
 * Service class handling business logic and validations for E-books.
 */
public class EbookService {

    private final EbookDAO ebookDAO;

    public EbookService() {
        this.ebookDAO = new EbookDAO();
    }

    /**
     * Validates and registers a new e-book into the catalog.
     * @param ebook Ebook object
     * @throws IllegalArgumentException on validation failure
     * @return true if added
     */
    public boolean registerEbook(Ebook ebook) {
        validateEbookData(ebook);

        // Check ISBN uniqueness
        Ebook existing = ebookDAO.findByIsbn(ebook.getIsbn().trim());
        if (existing != null) {
            throw new IllegalArgumentException("An e-book with ISBN '" + ebook.getIsbn() + "' already exists.");
        }

        return ebookDAO.addEbook(ebook);
    }

    /**
     * Validates and updates an existing e-book.
     * @param ebook Ebook object
     * @throws IllegalArgumentException on validation failure
     * @return true if updated
     */
    public boolean updateEbook(Ebook ebook) {
        if (ebook.getEbookId() <= 0) {
            throw new IllegalArgumentException("Invalid e-book ID.");
        }
        validateEbookData(ebook);

        // Check ISBN uniqueness (excluding this ebook)
        Ebook existing = ebookDAO.findByIsbn(ebook.getIsbn().trim());
        if (existing != null && existing.getEbookId() != ebook.getEbookId()) {
            throw new IllegalArgumentException("Another e-book already exists with ISBN '" + ebook.getIsbn() + "'.");
        }

        return ebookDAO.updateEbook(ebook);
    }

    /**
     * Deletes an e-book from the catalog.
     * Checks if it is currently issued before deleting.
     * @param ebookId e-book ID
     * @throws IllegalStateException if e-book is currently issued
     * @return true if deleted
     */
    public boolean deleteEbook(int ebookId) {
        if (ebookDAO.hasActiveIssue(ebookId)) {
            throw new IllegalStateException("Cannot delete e-book! It is currently issued to a user and has not been returned yet.");
        }
        return ebookDAO.deleteEbook(ebookId);
    }

    public Ebook getEbookById(int ebookId) {
        return ebookDAO.findById(ebookId);
    }

    public List<Ebook> getAllEbooks() {
        return ebookDAO.getAllEbooks();
    }

    public List<Ebook> searchEbooks(String title, String author, String category, String isbn, boolean onlyAvailable) {
        return ebookDAO.searchEbooks(title, author, category, isbn, onlyAvailable);
    }

    public int getTotalEbooksCount() {
        return ebookDAO.getTotalEbooksCount();
    }

    public int getAvailableEbooksCount() {
        return ebookDAO.getAvailableEbooksCount();
    }

    public int getIssuedEbooksCount() {
        return ebookDAO.getIssuedEbooksCount();
    }

    private void validateEbookData(Ebook ebook) {
        if (ebook == null) {
            throw new IllegalArgumentException("E-book details cannot be null.");
        }
        if (ebook.getTitle() == null || ebook.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("E-book Title is required.");
        }
        if (ebook.getAuthor() == null || ebook.getAuthor().trim().isEmpty()) {
            throw new IllegalArgumentException("E-book Author is required.");
        }
        if (ebook.getCategory() == null || ebook.getCategory().trim().isEmpty()) {
            throw new IllegalArgumentException("E-book Category is required.");
        }
        if (ebook.getIsbn() == null || ebook.getIsbn().trim().isEmpty()) {
            throw new IllegalArgumentException("E-book ISBN is required.");
        }
        if (ebook.getPublisher() == null || ebook.getPublisher().trim().isEmpty()) {
            throw new IllegalArgumentException("E-book Publisher is required.");
        }
        int currentYear = Year.now().getValue();
        if (ebook.getPublicationYear() < 1400 || ebook.getPublicationYear() > currentYear + 1) {
            throw new IllegalArgumentException("Invalid Publication Year: must be between 1400 and " + (currentYear + 1));
        }
    }
}
