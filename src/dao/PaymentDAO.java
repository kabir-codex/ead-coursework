package dao; // Declares this class belongs to the DAO (Data Access Object) package

import model.Payment; // Imports the Payment model class (represents payment entity)
import util.DatabaseConnection; // Imports database connection singleton utility

import java.sql.*; // Imports JDBC classes (Connection, PreparedStatement, ResultSet, Statement)
import java.util.ArrayList; // Imports ArrayList for storing Payment objects
import java.util.List; // Imports List interface

/**
 * PaymentDAO – DAO Pattern for Payments.
 * Handles all database operations related to payments.
 */
public class PaymentDAO { // Defines PaymentDAO class

    private Connection getConnection() { // Helper method to get database connection
        return DatabaseConnection.getInstance().getConnection(); // Returns shared connection instance
    }

    // ── CREATE ────────────────────────────────────────────────────────────────

    public boolean addPayment(Payment payment) throws SQLException { // Inserts a new payment record into DB

        String sql = "INSERT INTO payments (booking_id, amount, payment_method, payment_date, notes) " + // First part of INSERT query
                     "VALUES (?, ?, ?, ?, ?)"; // Placeholder parameters for prepared statement

        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { // PreparedStatement with generated keys enabled

            ps.setInt(1, payment.getBookingId()); // Sets booking ID foreign key
            ps.setDouble(2, payment.getAmount()); // Sets payment amount
            ps.setString(3, payment.getPaymentMethod().name()); // Converts enum payment method to string
            ps.setDate(4, new java.sql.Date(payment.getPaymentDate().getTime())); // Converts util.Date to SQL Date
            ps.setString(5, payment.getNotes()); // Sets optional notes

            int rows = ps.executeUpdate(); // Executes INSERT statement and returns affected rows

            if (rows > 0) { // Checks if insert succeeded

                try (ResultSet keys = ps.getGeneratedKeys()) { // Retrieves auto-generated payment_id

                    if (keys.next()) // Moves to first generated key
                        payment.setPaymentId(keys.getInt(1)); // Stores generated ID in object
                }

                return true; // Insert successful
            }

            return false; // Insert failed
        }
    }

    // ── READ ──────────────────────────────────────────────────────────────────

    /**
     * Returns all payment records joined with customer name, ordered most-recent first.
     * Uses INNER JOIN so only bookings with payments appear.
     */
    public List<Payment> getAllPayments() throws SQLException { // Retrieves all payments with customer info

        List<Payment> list = new ArrayList<>(); // List to store payment results

        String sql = "SELECT p.*, c.full_name AS customer_name " + // Select payment fields + customer name alias
                     "FROM payments p " + // Payments table alias p
                     "JOIN bookings b ON p.booking_id = b.booking_id " + // Join bookings table
                     "JOIN customers c ON b.customer_id = c.customer_id " + // Join customers table
                     "ORDER BY p.payment_date DESC"; // Sort newest payments first

        try (Statement st = getConnection().createStatement(); // Creates SQL statement
             ResultSet rs = st.executeQuery(sql)) { // Executes query

            while (rs.next()) // Iterates through result rows
                list.add(map(rs)); // Maps each row to Payment object
        }

        return list; // Returns list of payments
    }

    /**
     * Returns ALL bookings with their payment info (if any).
     * Uses LEFT JOIN so bookings without payments still appear.
     */
    public List<Payment> getAllBookingsWithPaymentInfo() throws SQLException { // Retrieves bookings + optional payments

        List<Payment> list = new ArrayList<>(); // List to store results

        String sql = "SELECT " + // Start SELECT query
                     "  p.payment_id, b.booking_id, c.full_name AS customer_name, " + // Selected columns
                     "  COALESCE(p.amount, 0) AS amount, " + // Replace NULL amount with 0
                     "  p.payment_method, p.payment_date, p.notes " + // Payment fields
                     "FROM bookings b " + // Base table bookings
                     "JOIN customers c ON b.customer_id = c.customer_id " + // Join customers
                     "LEFT JOIN payments p ON b.booking_id = p.booking_id " + // LEFT JOIN includes unpaid bookings
                     "ORDER BY b.booking_id DESC, p.payment_date DESC"; // Sorting order

        try (Statement st = getConnection().createStatement(); // Statement object
             ResultSet rs = st.executeQuery(sql)) { // Execute query

            while (rs.next()) { // Loop through rows

                Payment p = new Payment(); // Create Payment object

                int pid = rs.getInt("payment_id"); // Get payment ID
                p.setPaymentId(rs.wasNull() ? 0 : pid); // If NULL, set 0 as placeholder

                p.setBookingId(rs.getInt("booking_id")); // Set booking ID
                p.setCustomerName(rs.getString("customer_name")); // Set customer name
                p.setAmount(rs.getDouble("amount")); // Set payment amount

                String method = rs.getString("payment_method"); // Get payment method string

                if (method != null) // Check if method is not null
                    p.setPaymentMethod(Payment.PaymentMethod.valueOf(method)); // Convert string to enum

                p.setPaymentDate(rs.getDate("payment_date")); // Set payment date
                p.setNotes(rs.getString("notes")); // Set notes

                list.add(p); // Add to list
            }
        }

        return list; // Return full list
    }

    public List<Payment> getPaymentsByBookingId(int bookingId) throws SQLException { // Get payments for specific booking

        List<Payment> list = new ArrayList<>(); // List for results

        String sql = "SELECT p.*, c.full_name AS customer_name " + // Select payment + customer name
                     "FROM payments p " + // Payments table
                     "JOIN bookings b ON p.booking_id = b.booking_id " + // Join bookings
                     "JOIN customers c ON b.customer_id = c.customer_id " + // Join customers
                     "WHERE p.booking_id = ? ORDER BY p.payment_date"; // Filter by booking ID

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) { // Prepared statement

            ps.setInt(1, bookingId); // Set booking ID parameter

            try (ResultSet rs = ps.executeQuery()) { // Execute query

                while (rs.next()) // Iterate rows
                    list.add(map(rs)); // Map each row to Payment object
            }
        }

        return list; // Return filtered list
    }

    public double getTotalPaidForBooking(int bookingId) throws SQLException { // Get total payment sum for booking

        String sql = "SELECT COALESCE(SUM(amount),0) FROM payments WHERE booking_id=?"; // Sum query with null safety

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) { // Prepared statement

            ps.setInt(1, bookingId); // Set booking ID

            try (ResultSet rs = ps.executeQuery()) { // Execute query

                if (rs.next()) // Move to result row
                    return rs.getDouble(1); // Return sum
            }
        }

        return 0; // Return 0 if no payments
    }

    /**
     * Permanently deletes a payment record by its payment_id.
     */
    public boolean deletePayment(int paymentId) throws SQLException { // Delete payment by ID

        String sql = "DELETE FROM payments WHERE payment_id = ?"; // DELETE SQL query

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) { // Prepared statement

            ps.setInt(1, paymentId); // Set payment ID

            return ps.executeUpdate() > 0; // Return true if row deleted
        }
    }

    // ── Private helper ────────────────────────────────────────────────────────

    private Payment map(ResultSet rs) throws SQLException { // Converts ResultSet row to Payment object

        Payment p = new Payment(); // Create Payment object

        p.setPaymentId(rs.getInt("payment_id")); // Set payment ID
        p.setBookingId(rs.getInt("booking_id")); // Set booking ID
        p.setAmount(rs.getDouble("amount")); // Set amount

        p.setPaymentMethod(Payment.PaymentMethod.valueOf(rs.getString("payment_method"))); // Convert string to enum

        p.setPaymentDate(rs.getDate("payment_date")); // Set payment date
        p.setNotes(rs.getString("notes")); // Set notes

        try { // Try to set customer name (may not exist in all queries)
            p.setCustomerName(rs.getString("customer_name")); // Set customer name if available
        } catch (SQLException ignored) { // Ignore if column not present
        }

        return p; // Return populated Payment object
    }
} // End of PaymentDAO class