package dao; // Declares this file belongs to the "dao" package

import model.Booking; // Imports the Booking model class representing a booking record
import util.DatabaseConnection; // Imports a utility class that manages the database connection

import java.sql.*; // Imports all classes from java.sql (Connection, PreparedStatement, ResultSet, Statement, SQLException, Types, etc.)
import java.util.ArrayList; // Imports ArrayList, used to build dynamic lists of Booking objects
import java.util.List; // Imports the List interface, used to hold collections of Booking objects

/**
 * BookingDAO – DAO Pattern for Bookings (main transaction entity).
 */
public class BookingDAO { // Declares the BookingDAO class

    private Connection getConnection() { // Declares a private helper method that returns a database connection
        return DatabaseConnection.getInstance().getConnection(); // Gets the singleton DatabaseConnection instance and returns its active Connection object
    } // End of getConnection method

    // ── CREATE ────────────────────────────────────────────────────────────────

    public boolean addBooking(Booking booking) throws SQLException { // Declares a method to insert a new booking, can throw an SQLException
        String sql = "INSERT INTO bookings (customer_id, package_id, room_id, booking_date, " + // Begins building the SQL INSERT statement, listing the columns to insert into
                     "checkin_date, checkout_date, total_amount, status) VALUES (?,?,?,?,?,?,?,?)"; // Continues the SQL string, listing the remaining columns and 8 placeholder parameters
        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { // Opens a PreparedStatement for the SQL, requesting that generated keys (e.g. auto-increment ID) be returned, auto-closed after use
            ps.setInt(1, booking.getCustomerId()); // Sets the 1st placeholder to the booking's customer ID
            if (booking.getPackageId() != null) // Checks if the booking has an associated package ID (it's optional/nullable)
                ps.setInt(2, booking.getPackageId()); // If present, sets the 2nd placeholder to the package ID
            else // If the package ID is null
                ps.setNull(2, Types.INTEGER); // Sets the 2nd placeholder explicitly to SQL NULL of type INTEGER
            ps.setInt(3, booking.getRoomId()); // Sets the 3rd placeholder to the booking's room ID
            ps.setDate(4, new java.sql.Date(booking.getBookingDate().getTime())); // Converts the booking's date to a java.sql.Date and sets it as the 4th placeholder
            ps.setDate(5, new java.sql.Date(booking.getCheckinDate().getTime())); // Converts the check-in date to a java.sql.Date and sets it as the 5th placeholder
            ps.setDate(6, new java.sql.Date(booking.getCheckoutDate().getTime())); // Converts the check-out date to a java.sql.Date and sets it as the 6th placeholder
            ps.setDouble(7, booking.getTotalAmount()); // Sets the 7th placeholder to the booking's total amount
            ps.setString(8, booking.getStatus().name()); // Sets the 8th placeholder to the string name of the booking's status enum
            int rows = ps.executeUpdate(); // Executes the INSERT statement and stores the number of rows affected
            if (rows > 0) { // Checks if at least one row was inserted (success)
                try (ResultSet keys = ps.getGeneratedKeys()) { // Retrieves the auto-generated keys from the insert, auto-closed after use
                    if (keys.next()) booking.setBookingId(keys.getInt(1)); // If a generated key exists, sets it as the booking's ID on the in-memory object
                } // End of try block for generated keys
                return true; // Returns true to indicate the insert succeeded
            } // End of if block for successful insert
            return false; // Returns false if no rows were affected (insert failed)
        } // End of try block, PreparedStatement is automatically closed here
    } // End of addBooking method

    // ── READ ──────────────────────────────────────────────────────────────────

    /** All bookings with customer name, room number and package name via JOIN. */
    public List<Booking> getAllBookings() throws SQLException { // Declares a method to retrieve all bookings with joined data, can throw an SQLException
        List<Booking> list = new ArrayList<>(); // Creates an empty list to hold the resulting Booking objects
        String sql = "SELECT b.*, c.full_name AS customer_name, r.room_number, p.package_name " + // Begins the SQL SELECT statement, selecting all booking columns plus joined customer/room/package fields
                     "FROM bookings b " + // Specifies the bookings table aliased as "b"
                     "JOIN customers c ON b.customer_id = c.customer_id " + // Joins the customers table on matching customer_id
                     "JOIN rooms r     ON b.room_id     = r.room_id " + // Joins the rooms table on matching room_id
                     "LEFT JOIN packages p ON b.package_id = p.package_id " + // Left-joins the packages table on matching package_id (left join since package_id can be null)
                     "ORDER BY b.booking_date DESC"; // Orders the results by booking date, most recent first
        try (Statement st = getConnection().createStatement(); // Creates a plain Statement (no parameters needed) for executing the query, auto-closed after use
             ResultSet rs = st.executeQuery(sql)) { // Executes the query and stores the resulting ResultSet, also auto-closed after use
            while (rs.next()) list.add(mapWithJoin(rs)); // Loops through each row in the result set, mapping it to a Booking object and adding it to the list
        } // End of try block, Statement and ResultSet are automatically closed here
        return list; // Returns the populated list of bookings
    } // End of getAllBookings method

    public Booking getBookingById(int id) throws SQLException { // Declares a method to retrieve a single booking by its ID, can throw an SQLException
        String sql = "SELECT b.*, c.full_name AS customer_name, r.room_number, p.package_name " + // Begins the SQL SELECT statement, same joined structure as getAllBookings
                     "FROM bookings b " + // Specifies the bookings table aliased as "b"
                     "JOIN customers c ON b.customer_id = c.customer_id " + // Joins the customers table on matching customer_id
                     "JOIN rooms r     ON b.room_id     = r.room_id " + // Joins the rooms table on matching room_id
                     "LEFT JOIN packages p ON b.package_id = p.package_id " + // Left-joins the packages table on matching package_id
                     "WHERE b.booking_id = ?"; // Filters results to only the booking matching the given ID placeholder
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) { // Opens a PreparedStatement for the SQL, auto-closed after use
            ps.setInt(1, id); // Sets the 1st placeholder to the requested booking ID
            try (ResultSet rs = ps.executeQuery()) { // Executes the query and stores the result set, auto-closed after use
                if (rs.next()) return mapWithJoin(rs); // If a row was found, maps it to a Booking object and returns it immediately
            } // End of try block for ResultSet
        } // End of try block for PreparedStatement
        return null; // Returns null if no booking was found with the given ID
    } // End of getBookingById method

    /** Returns the 10 most recent bookings for the dashboard. */
    public List<Booking> getRecentBookings(int limit) throws SQLException { // Declares a method to retrieve a limited number of the most recent bookings, can throw an SQLException
        List<Booking> list = new ArrayList<>(); // Creates an empty list to hold the resulting Booking objects
        String sql = "SELECT b.*, c.full_name AS customer_name, r.room_number, p.package_name " + // Begins the SQL SELECT statement, same joined structure as before
                     "FROM bookings b " + // Specifies the bookings table aliased as "b"
                     "JOIN customers c ON b.customer_id = c.customer_id " + // Joins the customers table on matching customer_id
                     "JOIN rooms r     ON b.room_id     = r.room_id " + // Joins the rooms table on matching room_id
                     "LEFT JOIN packages p ON b.package_id = p.package_id " + // Left-joins the packages table on matching package_id
                     "ORDER BY b.booking_date DESC LIMIT ?"; // Orders by booking date descending and limits the number of rows returned via a placeholder
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) { // Opens a PreparedStatement for the SQL, auto-closed after use
            ps.setInt(1, limit); // Sets the 1st placeholder to the maximum number of rows to return
            try (ResultSet rs = ps.executeQuery()) { // Executes the query and stores the result set, auto-closed after use
                while (rs.next()) list.add(mapWithJoin(rs)); // Loops through each row, mapping it to a Booking object and adding it to the list
            } // End of try block for ResultSet
        } // End of try block for PreparedStatement
        return list; // Returns the populated list of recent bookings
    } // End of getRecentBookings method

    public int getTotalBookings() throws SQLException { // Declares a method to count the total number of bookings, can throw an SQLException
        String sql = "SELECT COUNT(*) FROM bookings"; // Defines a SQL statement that counts all rows in the bookings table
        try (Statement st = getConnection().createStatement(); // Creates a plain Statement for executing the count query, auto-closed after use
             ResultSet rs = st.executeQuery(sql)) { // Executes the query and stores the result set, auto-closed after use
            if (rs.next()) return rs.getInt(1); // If a result row exists, returns the count value from the first column
        } // End of try block, Statement and ResultSet are automatically closed here
        return 0; // Returns 0 if no result was found (fallback)
    } // End of getTotalBookings method

    public double getTotalRevenue() throws SQLException { // Declares a method to calculate total revenue from all payments, can throw an SQLException
        String sql = "SELECT COALESCE(SUM(amount),0) FROM payments"; // Defines a SQL statement summing all payment amounts, defaulting to 0 if there are none
        try (Statement st = getConnection().createStatement(); // Creates a plain Statement for executing the sum query, auto-closed after use
             ResultSet rs = st.executeQuery(sql)) { // Executes the query and stores the result set, auto-closed after use
            if (rs.next()) return rs.getDouble(1); // If a result row exists, returns the sum value from the first column
        } // End of try block, Statement and ResultSet are automatically closed here
        return 0; // Returns 0 if no result was found (fallback)
    } // End of getTotalRevenue method

    /**
     * Checks whether a given room is already booked for a date range (overlap check).
     * Excludes a specific booking ID (used when editing an existing booking).
     */
    public boolean isRoomBooked(int roomId, java.util.Date checkin, java.util.Date checkout, // Begins method signature to check if a room is booked, takes the room ID and check-in/check-out dates
                                int excludeBookingId) throws SQLException { // Continues method signature, takes a booking ID to exclude from the check, can throw an SQLException
        String sql = "SELECT COUNT(*) FROM bookings " + // Begins SQL statement counting bookings that match the overlap conditions
                     "WHERE room_id = ? AND booking_id != ? AND status NOT IN ('CANCELLED') " + // Filters by room ID, excludes the given booking ID, and excludes cancelled bookings
                     "AND NOT (checkout_date <= ? OR checkin_date >= ?)"; // Adds a date-range overlap condition: excludes bookings that end before or start after the requested range
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) { // Opens a PreparedStatement for the SQL, auto-closed after use
            ps.setInt(1, roomId); // Sets the 1st placeholder to the room ID being checked
            ps.setInt(2, excludeBookingId); // Sets the 2nd placeholder to the booking ID to exclude from the comparison
            ps.setDate(3, new java.sql.Date(checkin.getTime())); // Converts the checkin date to a java.sql.Date and sets it as the 3rd placeholder
            ps.setDate(4, new java.sql.Date(checkout.getTime())); // Converts the checkout date to a java.sql.Date and sets it as the 4th placeholder
            try (ResultSet rs = ps.executeQuery()) { // Executes the query and stores the result set, auto-closed after use
                if (rs.next()) return rs.getInt(1) > 0; // If a result row exists, returns true if the count of overlapping bookings is greater than zero
            } // End of try block for ResultSet
        } // End of try block for PreparedStatement
        return false; // Returns false if no result was found (fallback, meaning room is not booked)
    } // End of isRoomBooked method

    // ── UPDATE ────────────────────────────────────────────────────────────────

    public boolean updateBooking(Booking booking) throws SQLException { // Declares a method to update an existing booking record, can throw an SQLException
        String sql = "UPDATE bookings SET customer_id=?, package_id=?, room_id=?, booking_date=?, " + // Begins the SQL UPDATE statement, setting several columns via placeholders
                     "checkin_date=?, checkout_date=?, total_amount=?, status=? WHERE booking_id=?"; // Continues the SQL, setting remaining columns and filtering by booking_id
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) { // Opens a PreparedStatement for the SQL, auto-closed after use
            ps.setInt(1, booking.getCustomerId()); // Sets the 1st placeholder to the booking's customer ID
            if (booking.getPackageId() != null) ps.setInt(2, booking.getPackageId()); // If the package ID is present, sets the 2nd placeholder to it
            else ps.setNull(2, Types.INTEGER); // Otherwise sets the 2nd placeholder to SQL NULL of type INTEGER
            ps.setInt(3, booking.getRoomId()); // Sets the 3rd placeholder to the booking's room ID
            ps.setDate(4, new java.sql.Date(booking.getBookingDate().getTime())); // Converts the booking date to a java.sql.Date and sets it as the 4th placeholder
            ps.setDate(5, new java.sql.Date(booking.getCheckinDate().getTime())); // Converts the check-in date to a java.sql.Date and sets it as the 5th placeholder
            ps.setDate(6, new java.sql.Date(booking.getCheckoutDate().getTime())); // Converts the check-out date to a java.sql.Date and sets it as the 6th placeholder
            ps.setDouble(7, booking.getTotalAmount()); // Sets the 7th placeholder to the booking's total amount
            ps.setString(8, booking.getStatus().name()); // Sets the 8th placeholder to the string name of the booking's status enum
            ps.setInt(9, booking.getBookingId()); // Sets the 9th placeholder to the booking's ID, used in the WHERE clause to identify which row to update
            return ps.executeUpdate() > 0; // Executes the update and returns true if at least one row was affected
        } // End of try block, PreparedStatement is automatically closed here
    } // End of updateBooking method

    public boolean updateBookingStatus(int bookingId, Booking.Status status) throws SQLException { // Declares a method to update only the status of a booking, can throw an SQLException
        String sql = "UPDATE bookings SET status=? WHERE booking_id=?"; // Defines a SQL statement to update the status column for a specific booking ID
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) { // Opens a PreparedStatement for the SQL, auto-closed after use
            ps.setString(1, status.name()); // Sets the 1st placeholder to the string name of the new status enum
            ps.setInt(2, bookingId); // Sets the 2nd placeholder to the ID of the booking to update
            return ps.executeUpdate() > 0; // Executes the update and returns true if at least one row was affected
        } // End of try block, PreparedStatement is automatically closed here
    } // End of updateBookingStatus method

    // ── DELETE ────────────────────────────────────────────────────────────────

    public boolean deleteBooking(int id) throws SQLException { // Declares a method to delete a booking by its ID, can throw an SQLException
        String sql = "DELETE FROM bookings WHERE booking_id = ?"; // Defines a SQL statement to delete the booking row matching the given ID
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) { // Opens a PreparedStatement for the SQL, auto-closed after use
            ps.setInt(1, id); // Sets the 1st placeholder to the booking ID to delete
            return ps.executeUpdate() > 0; // Executes the delete and returns true if at least one row was affected
        } // End of try block, PreparedStatement is automatically closed here
    } // End of deleteBooking method

    // ── Revenue chart data ─────────────────────────────────────────────────────

    /** Returns monthly revenue for the current year (12 rows). */
    public double[] getMonthlyRevenue() throws SQLException { // Declares a method to get revenue totals broken down by month, can throw an SQLException
        double[] revenue = new double[12]; // Creates an array of 12 doubles (one slot per month), all initialized to 0
        String sql = "SELECT MONTH(payment_date) AS mon, SUM(amount) AS total " + // Begins SQL statement that extracts the month from payment_date and sums amounts
                     "FROM payments WHERE YEAR(payment_date) = 2026 GROUP BY mon"; // Filters payments to the year 2026 and groups the sums by month
        try (Statement st = getConnection().createStatement(); // Creates a plain Statement for executing the query, auto-closed after use
             ResultSet rs = st.executeQuery(sql)) { // Executes the query and stores the result set, auto-closed after use
            while (rs.next()) { // Loops through each row in the result (one row per month that has payments)
                int month = rs.getInt("mon"); // Retrieves the month number (1–12) from the "mon" column
                revenue[month - 1] = rs.getDouble("total"); // Stores the total revenue for that month in the array, adjusting for zero-based indexing
            } // End of while loop
        } // End of try block, Statement and ResultSet are automatically closed here
        return revenue; // Returns the array of monthly revenue totals
    } // End of getMonthlyRevenue method

    // ── Private helper ────────────────────────────────────────────────────────

    private Booking mapWithJoin(ResultSet rs) throws SQLException { // Declares a private helper method that converts a ResultSet row (with joined columns) into a Booking object, can throw an SQLException
        Booking b = new Booking(); // Creates a new, empty Booking object
        b.setBookingId(rs.getInt("booking_id")); // Sets the booking ID from the "booking_id" column
        b.setCustomerId(rs.getInt("customer_id")); // Sets the customer ID from the "customer_id" column
        int pkgId = rs.getInt("package_id"); // Retrieves the package_id column value into a local variable
        b.setPackageId(rs.wasNull() ? null : pkgId); // Sets the package ID to null if the column was actually SQL NULL, otherwise uses the retrieved value
        b.setRoomId(rs.getInt("room_id")); // Sets the room ID from the "room_id" column
        b.setBookingDate(rs.getDate("booking_date")); // Sets the booking date from the "booking_date" column
        b.setCheckinDate(rs.getDate("checkin_date")); // Sets the check-in date from the "checkin_date" column
        b.setCheckoutDate(rs.getDate("checkout_date")); // Sets the check-out date from the "checkout_date" column
        b.setTotalAmount(rs.getDouble("total_amount")); // Sets the total amount from the "total_amount" column
        b.setStatus(Booking.Status.valueOf(rs.getString("status"))); // Converts the "status" column string into the Booking.Status enum and sets it
        b.setCustomerName(rs.getString("customer_name")); // Sets the customer's name from the joined "customer_name" column
        b.setRoomNumber(rs.getString("room_number")); // Sets the room number from the joined "room_number" column
        b.setPackageName(rs.getString("package_name")); // Sets the package name from the joined "package_name" column (may be null if no package)
        return b; // Returns the fully populated Booking object
    } // End of mapWithJoin method
} // End of BookingDAO class