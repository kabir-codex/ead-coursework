package util; // Declares this class belongs to the utility package (helper classes)

import java.sql.Connection; // JDBC Connection interface
import java.sql.DriverManager; // Manages database connections
import java.sql.SQLException; // Handles SQL-related exceptions

/**
 * DatabaseConnection – Singleton Pattern
 * Provides a single shared JDBC connection to tourism_management_db.
 *
 * Change DB_URL / USER / PASS to match your local MySQL setup.
 */
public class DatabaseConnection { // Defines DatabaseConnection class

    // ── Configuration ─────────────────────────────────────────────────────────

    private static final String DB_URL  = "jdbc:mysql://localhost:3306/tourism_management_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"; // Database URL
    private static final String DB_USER = "root"; // MySQL username
    private static final String DB_PASS = "";     // MySQL password (empty by default, should be updated if needed)

    // ── Singleton state ───────────────────────────────────────────────────────

    private static DatabaseConnection instance; // Holds single instance of this class
    private Connection connection; // Holds active JDBC connection

    // ── Private constructor ───────────────────────────────────────────────────

    private DatabaseConnection() { // Private constructor prevents external instantiation
        try {

            Class.forName("com.mysql.cj.jdbc.Driver"); // Loads MySQL JDBC driver

            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS); // Establishes DB connection

            System.out.println("[DB] Connected to tourism_management_db successfully."); // Success log

        } catch (ClassNotFoundException e) { // Driver not found error handling

            System.err.println("[DB] MySQL JDBC Driver not found: " + e.getMessage()); // Error log

            throw new RuntimeException("MySQL JDBC Driver not found.", e); // Stop application with runtime error

        } catch (SQLException e) { // Database connection failure handling

            System.err.println("[DB] Connection failed: " + e.getMessage()); // Error log

            throw new RuntimeException("Database connection failed.", e); // Stop application with runtime error
        }
    }

    // ── Public accessor ───────────────────────────────────────────────────────

    /**
     * Returns the single DatabaseConnection instance.
     * Thread-safe via synchronized keyword.
     */
    public static synchronized DatabaseConnection getInstance() { // Returns singleton instance safely

        if (instance == null || isConnectionClosed()) { // Create new instance if none exists or connection closed
            instance = new DatabaseConnection(); // Initialize new connection
        }

        return instance; // Return singleton instance
    }

    /** Returns the raw JDBC Connection object. */
    public Connection getConnection() { // Provides access to JDBC connection
        return connection; // Return active connection
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static boolean isConnectionClosed() { // Checks whether connection is valid or closed
        try {
            return instance.connection == null || instance.connection.isClosed(); // True if null or closed
        } catch (SQLException e) {
            return true; // Treat errors as closed connection
        }
    }

    /** Call this when the application exits. */
    public void closeConnection() { // Safely closes DB connection
        try {

            if (connection != null && !connection.isClosed()) { // Check if connection exists and open
                connection.close(); // Close connection
                System.out.println("[DB] Connection closed."); // Log closure
            }

        } catch (SQLException e) { // Handle closing errors
            System.err.println("[DB] Error closing connection: " + e.getMessage()); // Error log
        }
    }
} // End of DatabaseConnection class