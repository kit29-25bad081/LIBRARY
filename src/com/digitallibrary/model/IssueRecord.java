package com.digitallibrary.model;

import java.time.LocalDate;

/**
 * Model class representing an e-book issue/borrowing transaction record.
 */
public class IssueRecord {
    private int issueId;
    private int userId;
    private int ebookId;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private String status; // 'ISSUED', 'RETURNED'

    // Joined auxiliary fields for convenient UI display
    private String userName;
    private String ebookTitle;
    private String userEmail;
    private String ebookIsbn;

    public IssueRecord() {
    }

    public IssueRecord(int issueId, int userId, int ebookId, LocalDate issueDate, LocalDate dueDate, LocalDate returnDate, String status) {
        this.issueId = issueId;
        this.userId = userId;
        this.ebookId = ebookId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = status;
    }

    public IssueRecord(int userId, int ebookId, LocalDate issueDate, LocalDate dueDate) {
        this.userId = userId;
        this.ebookId = ebookId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.status = "ISSUED";
    }

    public int getIssueId() {
        return issueId;
    }

    public void setIssueId(int issueId) {
        this.issueId = issueId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getEbookId() {
        return ebookId;
    }

    public void setEbookId(int ebookId) {
        this.ebookId = ebookId;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEbookTitle() {
        return ebookTitle;
    }

    public void setEbookTitle(String ebookTitle) {
        this.ebookTitle = ebookTitle;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getEbookIsbn() {
        return ebookIsbn;
    }

    public void setEbookIsbn(String ebookIsbn) {
        this.ebookIsbn = ebookIsbn;
    }

    @Override
    public String toString() {
        return "IssueRecord{" +
                "issueId=" + issueId +
                ", userId=" + userId +
                ", ebookId=" + ebookId +
                ", issueDate=" + issueDate +
                ", dueDate=" + dueDate +
                ", returnDate=" + returnDate +
                ", status='" + status + '\'' +
                '}';
    }
}
