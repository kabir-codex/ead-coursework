package dao; // Declares this class belongs to the dao package (Data Access Layer)

import model.Package; // Imports the Package model class (represents tour package entity)
import util.DatabaseConnection; // Imports singleton database connection utility

import java.sql.*; // Imports JDBC classes (Connection, Statement, PreparedStatement, ResultSet)
import java.util.ArrayList; // Imports ArrayList for storing collections of Package objects
import java.util.List; // Imports List interface

/**
 * PackageDAO – DAO Pattern for Tour Packages.
 * This class handles all CRUD operations for the packages table in the database.
 */
public class PackageDAO { // Defines the PackageDAO class

    private Connection getConnection() { // Helper method to get DB connection
        return DatabaseConnection.getInstance().getConnection(); // Returns a shared connection instance
    }

    // ── CREATE ────────────────────────────────────────────────────────────────

    public boolean addPackage(Package pkg) throws SQLException { // Inserts a new package into database

        String sql = "INSERT INTO packages (package_name, destination, duration_days, price, description, status) " + // First part of INSERT query
                     "VALUES (?, ?, ?, ?, ?, ?)"; // Placeholders for prepared statement parameters

        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { // Creates PreparedStatement and enables auto-generated keys retrieval

            ps.setString(1, pkg.getPackageName()); // Sets package_name value
            ps.setString(2, pkg.getDestination()); // Sets destination value
            ps.setInt(3, pkg.getDurationDays()); // Sets duration in days
            ps.setDouble(4, pkg.getPrice()); // Sets package price
            ps.setString(5, pkg.getDescription()); // Sets package description
            ps.setString(6, pkg.getStatus().name()); // Converts enum status to String and sets it

            int rows = ps.executeUpdate(); // Executes INSERT query and returns number of affected rows

            if (rows > 0) { // Checks if insert was successful

                try (ResultSet keys = ps.getGeneratedKeys()) { // Retrieves auto-generated primary key

                    if (keys.next()) // Moves to first generated key
                        pkg.setPackageId(keys.getInt(1)); // Assigns generated ID back to object
                }

                return true; // Insert successful
            }

            return false; // Insert failed
        }
    }

    // ── READ ──────────────────────────────────────────────────────────────────

    public List<Package> getAllPackages() throws SQLException { // Retrieves all packages

        List<Package> list = new ArrayList<>(); // Creates list to store packages

        String sql = "SELECT * FROM packages ORDER BY package_name"; // SQL query to get all packages sorted by name

        try (Statement st = getConnection().createStatement(); // Creates SQL statement
             ResultSet rs = st.executeQuery(sql)) { // Executes query and stores result set

            while (rs.next()) // Iterates through each row
                list.add(map(rs)); // Converts row into Package object and adds to list
        }

        return list; // Returns list of all packages
    }

    public List<Package> getActivePackages() throws SQLException { // Retrieves only active packages

        List<Package> list = new ArrayList<>(); // List for active packages

        String sql = "SELECT * FROM packages WHERE status='ACTIVE' ORDER BY package_name"; // Query for active packages only

        try (Statement st = getConnection().createStatement(); // Creates statement
             ResultSet rs = st.executeQuery(sql)) { // Executes query

            while (rs.next()) // Iterates through results
                list.add(map(rs)); // Maps each row to Package object
        }

        return list; // Returns active packages list
    }

    public Package getPackageById(int id) throws SQLException { // Retrieves a package by ID

        String sql = "SELECT * FROM packages WHERE package_id = ?"; // Query with parameter placeholder

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) { // Creates prepared statement

            ps.setInt(1, id); // Sets package ID parameter

            try (ResultSet rs = ps.executeQuery()) { // Executes query

                if (rs.next()) // If record exists
                    return map(rs); // Convert row to Package object and return it
            }
        }

        return null; // Return null if no package found
    }

    public List<Package> searchPackages(String keyword) throws SQLException { // Searches packages by keyword

        List<Package> list = new ArrayList<>(); // List for search results

        String sql = "SELECT * FROM packages WHERE package_name LIKE ? OR destination LIKE ? ORDER BY package_name"; // Search query

        String p = "%" + keyword + "%"; // Wildcard pattern for LIKE search

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) { // Prepared statement

            ps.setString(1, p); // Sets search for package_name
            ps.setString(2, p); // Sets search for destination

            try (ResultSet rs = ps.executeQuery()) { // Executes query

                while (rs.next()) // Iterates results
                    list.add(map(rs)); // Maps each row into Package object
            }
        }

        return list; // Returns matching packages
    }

    public int getTotalPackages() throws SQLException { // Gets count of active packages

        String sql = "SELECT COUNT(*) FROM packages WHERE status='ACTIVE'"; // Count query for active packages only

        try (Statement st = getConnection().createStatement(); // Statement object
             ResultSet rs = st.executeQuery(sql)) { // Execute query

            if (rs.next()) // Move to first row
                return rs.getInt(1); // Return count value
        }

        return 0; // Return 0 if query fails or no data
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    public boolean updatePackage(Package pkg) throws SQLException { // Updates package details

        String sql = "UPDATE packages SET package_name=?, destination=?, duration_days=?, " + // First part of update query
                     "price=?, description=?, status=? WHERE package_id=?"; // Remaining update conditions

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) { // Prepared statement for update

            ps.setString(1, pkg.getPackageName()); // Set package name
            ps.setString(2, pkg.getDestination()); // Set destination
            ps.setInt(3, pkg.getDurationDays()); // Set duration
            ps.setDouble(4, pkg.getPrice()); // Set price
            ps.setString(5, pkg.getDescription()); // Set description
            ps.setString(6, pkg.getStatus().name()); // Convert enum status to string
            ps.setInt(7, pkg.getPackageId()); // Set WHERE condition (package ID)

            return ps.executeUpdate() > 0; // Execute update and return true if rows affected
        }
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    public boolean deletePackage(int id) throws SQLException { // Deletes package by ID

        String sql = "DELETE FROM packages WHERE package_id = ?"; // Delete query

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) { // Prepared statement

            ps.setInt(1, id); // Set package ID

            return ps.executeUpdate() > 0; // Return true if delete was successful
        }
    }

    // ── Private helper ────────────────────────────────────────────────────────

    private Package map(ResultSet rs) throws SQLException { // Converts ResultSet row into Package object

        Package p = new Package(); // Create new Package object

        p.setPackageId(rs.getInt("package_id")); // Set package ID from DB
        p.setPackageName(rs.getString("package_name")); // Set package name
        p.setDestination(rs.getString("destination")); // Set destination
        p.setDurationDays(rs.getInt("duration_days")); // Set duration days
        p.setPrice(rs.getDouble("price")); // Set price
        p.setDescription(rs.getString("description")); // Set description

        p.setStatus(Package.Status.valueOf(rs.getString("status"))); // Convert DB string to enum value

        return p; // Return fully populated Package object
    }
} // End of PackageDAO class