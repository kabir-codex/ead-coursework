package dao; // Declares this class belongs to the DAO package (data access layer)

import model.User; // Imports User model class (represents system user entity)
import util.DatabaseConnection; // Imports singleton database connection utility

import java.sql.*; // Imports JDBC classes (Connection, PreparedStatement, ResultSet)

/**
 * UserDAO – Handles authentication and user lookup.
 * Responsible for database operations related to system users.
 */
public class UserDAO { // Defines UserDAO class

    private Connection getConnection() { // Helper method to get DB connection
        return DatabaseConnection.getInstance().getConnection(); // Returns shared database connection
    }

    /**
     * Authenticates a user by username and password.
     * @return User object if credentials match, null otherwise.
     */
    public User authenticate(String username, String password) throws SQLException { // Login authentication method

        String sql = "SELECT * FROM users WHERE username=? AND password=? AND is_active=1"; // SQL query for login check (only active users)

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) { // Creates prepared statement

            ps.setString(1, username); // Sets username parameter
            ps.setString(2, password); // Sets password parameter (NOTE: should be hashed in real systems)

            try (ResultSet rs = ps.executeQuery()) { // Executes query

                if (rs.next()) // If a matching user is found
                    return map(rs); // Convert DB row into User object
            }
        }

        return null; // Return null if authentication fails
    }

    public User getUserById(int id) throws SQLException { // Retrieves user by ID

        String sql = "SELECT * FROM users WHERE user_id=?"; // SQL query with parameter

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) { // Prepared statement

            ps.setInt(1, id); // Sets user ID parameter

            try (ResultSet rs = ps.executeQuery()) { // Executes query

                if (rs.next()) // If user exists
                    return map(rs); // Convert row to User object
            }
        }

        return null; // Return null if not found
    }

    // ── Private helper ────────────────────────────────────────────────────────

    private User map(ResultSet rs) throws SQLException { // Maps ResultSet row to User object

        User u = new User(); // Create new User object

        u.setUserId(rs.getInt("user_id")); // Set user ID
        u.setUsername(rs.getString("username")); // Set username
        u.setFullName(rs.getString("full_name")); // Set full name

        u.setRole(User.Role.valueOf(rs.getString("role"))); // Convert role string to enum

        u.setEmail(rs.getString("email")); // Set email address

        u.setActive(rs.getInt("is_active") == 1); // Convert 1/0 into boolean active flag

        return u; // Return populated User object
    }
} // End of UserDAO class