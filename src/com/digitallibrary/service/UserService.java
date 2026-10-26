package com.digitallibrary.service;

import com.digitallibrary.dao.UserDAO;
import com.digitallibrary.model.User;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Service class handling business logic and validations for Users.
 */
public class UserService {

    private final UserDAO userDAO;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public UserService() {
        this.userDAO = new UserDAO();
    }

    /**
     * Validates and registers a new user.
     * @param user User object
     * @throws IllegalArgumentException if validation fails or email already exists
     * @return true if added
     */
    public boolean registerUser(User user) {
        validateUserData(user);

        // Check unique email
        User existing = userDAO.findByEmail(user.getEmail());
        if (existing != null) {
            throw new IllegalArgumentException("A user with email '" + user.getEmail() + "' already exists.");
        }

        return userDAO.addUser(user);
    }

    /**
     * Validates and updates an existing user.
     * @param user User object
     * @throws IllegalArgumentException if validation fails
     * @return true if updated
     */
    public boolean updateUser(User user) {
        if (user.getUserId() <= 0) {
            throw new IllegalArgumentException("Invalid user ID.");
        }
        validateUserData(user);

        // Check unique email (if changed to an email that belongs to another user)
        User existing = userDAO.findByEmail(user.getEmail());
        if (existing != null && existing.getUserId() != user.getUserId()) {
            throw new IllegalArgumentException("Another user already has the email '" + user.getEmail() + "'.");
        }

        return userDAO.updateUser(user);
    }

    /**
     * Deletes a user after checking active borrowing records.
     * @param userId user ID
     * @throws IllegalStateException if user currently has unreturned books
     * @return true if deleted
     */
    public boolean deleteUser(int userId) {
        if (userDAO.hasActiveIssues(userId)) {
            throw new IllegalStateException("Cannot delete user! This user has active issued e-books that have not been returned yet.");
        }
        return userDAO.deleteUser(userId);
    }

    public User getUserById(int userId) {
        return userDAO.findById(userId);
    }

    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    public List<User> searchUsers(String query) {
        if (query == null || query.trim().isEmpty()) {
            return userDAO.getAllUsers();
        }
        return userDAO.searchUsers(query.trim());
    }

    public int getTotalUsersCount() {
        return userDAO.getTotalUserCount();
    }

    private void validateUserData(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User details cannot be null.");
        }
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("User name is required and cannot be empty.");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("User email is required.");
        }
        if (!EMAIL_PATTERN.matcher(user.getEmail().trim()).matches()) {
            throw new IllegalArgumentException("Invalid email format (e.g. name@example.com).");
        }
        if (user.getPhone() == null || user.getPhone().trim().isEmpty()) {
            throw new IllegalArgumentException("User phone number is required.");
        }
        if (user.getPhone().trim().length() < 7) {
            throw new IllegalArgumentException("Phone number must have at least 7 digits.");
        }
    }
}
