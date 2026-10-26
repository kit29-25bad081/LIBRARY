package com.digitallibrary.model;

import java.sql.Timestamp;

/**
 * Model class representing an E-book in the digital library catalog.
 */
public class Ebook {
    private int ebookId;
    private String title;
    private String author;
    private String category;
    private String isbn;
    private String publisher;
    private int publicationYear;
    private String ebookUrl;
    private boolean available;
    private Timestamp createdAt;

    public Ebook() {
        this.available = true;
    }

    public Ebook(int ebookId, String title, String author, String category, String isbn,
                 String publisher, int publicationYear, String ebookUrl, boolean available) {
        this.ebookId = ebookId;
        this.title = title;
        this.author = author;
        this.category = category;
        this.isbn = isbn;
        this.publisher = publisher;
        this.publicationYear = publicationYear;
        this.ebookUrl = ebookUrl;
        this.available = available;
    }

    public Ebook(int ebookId, String title, String author, String category, String isbn,
                 String publisher, int publicationYear, String ebookUrl, boolean available, Timestamp createdAt) {
        this.ebookId = ebookId;
        this.title = title;
        this.author = author;
        this.category = category;
        this.isbn = isbn;
        this.publisher = publisher;
        this.publicationYear = publicationYear;
        this.ebookUrl = ebookUrl;
        this.available = available;
        this.createdAt = createdAt;
    }

    public Ebook(String title, String author, String category, String isbn,
                 String publisher, int publicationYear, String ebookUrl) {
        this.title = title;
        this.author = author;
        this.category = category;
        this.isbn = isbn;
        this.publisher = publisher;
        this.publicationYear = publicationYear;
        this.ebookUrl = ebookUrl;
        this.available = true;
    }

    public int getEbookId() {
        return ebookId;
    }

    public void setEbookId(int ebookId) {
        this.ebookId = ebookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    public String getEbookUrl() {
        return ebookUrl;
    }

    public void setEbookUrl(String ebookUrl) {
        this.ebookUrl = ebookUrl;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return title + " by " + author + " [" + (available ? "Available" : "Issued") + "]";
    }
}
