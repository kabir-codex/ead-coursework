package dao; // Declares that this class belongs to the 'dao' package

import model.Customer; // Imports the Customer model class
import util.DatabaseConnection; // Imports the DatabaseConnection utility class

import java.sql.*; // Imports all SQL-related classes (Connection, Statement, ResultSet, etc.)
import java.util.ArrayList; // Imports ArrayList collection class
import java.util.List; // Imports List interface

/**
 * CustomerDAO – DAO Pattern
 * Handles all CRUD database operations for the Customer entity.
 */
public class CustomerDAO { // Defines the CustomerDAO class

    private Connection getConnection() { // Private helper method to obtain a database connection
        return DatabaseConnection.getInstance().getConnection(); // Returns the database connection from the singleton DatabaseConnection class
    }

    // ── CREATE ────────────────────────────────────────────────────────────────

    /**
     * Inserts a new customer and sets the generated ID back on the object.
     * @return true on success
     */
    public boolean addCustomer(Customer customer) throws SQLException { // Method to add a new customer to the database
        String sql = "INSERT INTO customers (full_name, nic_passport, contact_number, email, address, nationality) " + // SQL INSERT statement (part 1)
                     "VALUES (?, ?, ?, ?, ?, ?)"; // SQL INSERT statement with placeholders for values

        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { // Creates PreparedStatement and enables retrieval of generated keys

            ps.setString(1, customer.getFullName()); // Sets full_name parameter from customer object
            ps.setString(2, customer.getNicPassport()); // Sets nic_passport parameter
            ps.setString(3, customer.getContactNumber()); // Sets contact_number parameter
            ps.setString(4, customer.getEmail()); // Sets email parameter
            ps.setString(5, customer.getAddress()); // Sets address parameter
            ps.setString(6, customer.getNationality()); // Sets nationality parameter

            int rows = ps.executeUpdate(); // Executes INSERT query and stores number of affected rows

            if (rows > 0) { // Checks if at least one row was inserted successfully

                try (ResultSet keys = ps.getGeneratedKeys()) { // Retrieves auto-generated primary key(s)

                    if (keys.next()) // Moves to the first generated key record
                        customer.setCustomerId(keys.getInt(1)); // Sets generated customer ID back into customer object
                }

                return true; // Returns true because insertion was successful
            }

            return false; // Returns false if no rows were inserted
        }
    }

    // ── READ ──────────────────────────────────────────────────────────────────

    /** Returns all customers ordered by name. */
    public List<Customer> getAllCustomers() throws SQLException { // Retrieves all customers from the database

        List<Customer> list = new ArrayList<>(); // Creates an empty list to store customers

        String sql = "SELECT * FROM customers ORDER BY full_name"; // SQL query to fetch all customers ordered by name

        try (Statement st = getConnection().createStatement(); // Creates a Statement object
             ResultSet rs = st.executeQuery(sql)) { // Executes query and stores results in ResultSet

            while (rs.next()) // Loops through every row in the ResultSet
                list.add(map(rs)); // Converts current row into Customer object and adds it to list
        }

        return list; // Returns list of customers
    }

    /** Finds a customer by primary key. */
    public Customer getCustomerById(int id) throws SQLException { // Retrieves a customer using customer ID

        String sql = "SELECT * FROM customers WHERE customer_id = ?"; // SQL query with placeholder

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) { // Creates PreparedStatement

            ps.setInt(1, id); // Sets customer ID parameter

            try (ResultSet rs = ps.executeQuery()) { // Executes SELECT query

                if (rs.next()) // Checks if a matching record exists
                    return map(rs); // Converts row into Customer object and returns it
            }
        }

        return null; // Returns null if no customer was found
    }

    /**
     * Searches customers by name, NIC/passport, email, or contact.
     * @param keyword search string
     */
    public List<Customer> searchCustomers(String keyword) throws SQLException { // Searches customers using a keyword

        List<Customer> list = new ArrayList<>(); // Creates list to store matching customers

        String sql = "SELECT * FROM customers WHERE " + // Beginning of SQL search query
                     "full_name LIKE ? OR nic_passport LIKE ? OR email LIKE ? OR contact_number LIKE ? " + // Search conditions
                     "ORDER BY full_name"; // Orders results by name

        String pattern = "%" + keyword + "%"; // Creates wildcard search pattern

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) { // Creates PreparedStatement

            ps.setString(1, pattern); // Sets search pattern for full_name
            ps.setString(2, pattern); // Sets search pattern for nic_passport
            ps.setString(3, pattern); // Sets search pattern for email
            ps.setString(4, pattern); // Sets search pattern for contact_number

            try (ResultSet rs = ps.executeQuery()) { // Executes search query

                while (rs.next()) // Loops through matching records
                    list.add(map(rs)); // Converts row to Customer object and adds to list
            }
        }

        return list; // Returns matching customers
    }

    /** Returns total customer count. */
    public int getTotalCustomers() throws SQLException { // Retrieves total number of customers

        String sql = "SELECT COUNT(*) FROM customers"; // SQL query to count customers

        try (Statement st = getConnection().createStatement(); // Creates Statement object
             ResultSet rs = st.executeQuery(sql)) { // Executes count query

            if (rs.next()) // Moves to first result row
                return rs.getInt(1); // Returns count value from first column
        }

        return 0; // Returns 0 if count could not be retrieved
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    public boolean updateCustomer(Customer customer) throws SQLException { // Updates an existing customer's details

        String sql = "UPDATE customers SET full_name=?, nic_passport=?, contact_number=?, " + // First part of UPDATE query
                     "email=?, address=?, nationality=? WHERE customer_id=?"; // Remaining UPDATE query

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) { // Creates PreparedStatement

            ps.setString(1, customer.getFullName()); // Sets updated full name
            ps.setString(2, customer.getNicPassport()); // Sets updated NIC/passport
            ps.setString(3, customer.getContactNumber()); // Sets updated contact number
            ps.setString(4, customer.getEmail()); // Sets updated email
            ps.setString(5, customer.getAddress()); // Sets updated address
            ps.setString(6, customer.getNationality()); // Sets updated nationality
            ps.setInt(7, customer.getCustomerId()); // Sets customer ID used in WHERE clause

            return ps.executeUpdate() > 0; // Executes update and returns true if rows were affected
        }
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    public boolean deleteCustomer(int id) throws SQLException { // Deletes a customer by ID

        String sql = "DELETE FROM customers WHERE customer_id = ?"; // SQL DELETE statement

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) { // Creates PreparedStatement

            ps.setInt(1, id); // Sets customer ID parameter

            return ps.executeUpdate() > 0; // Executes delete and returns true if a row was removed
        }
    }

    // ── Private helper ────────────────────────────────────────────────────────

    private Customer map(ResultSet rs) throws SQLException { // Converts a ResultSet row into a Customer object

        Customer c = new Customer(); // Creates a new Customer object

        c.setCustomerId(rs.getInt("customer_id")); // Sets customer ID from database column
        c.setFullName(rs.getString("full_name")); // Sets full name from database column
        c.setNicPassport(rs.getString("nic_passport")); // Sets NIC/passport from database column
        c.setContactNumber(rs.getString("contact_number")); // Sets contact number from database column
        c.setEmail(rs.getString("email")); // Sets email from database column
        c.setAddress(rs.getString("address")); // Sets address from database column
        c.setNationality(rs.getString("nationality")); // Sets nationality from database column

        return c; // Returns populated Customer object
    }
} // End of CustomerDAO class