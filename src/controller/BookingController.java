package controller; // Declares this file belongs to the 'controller' package (MVC Controller layer)

import dao.BookingDAO;           // Imports the BookingDAO class for database operations on bookings
import dao.RoomDAO;              // Imports the RoomDAO class for database operations on rooms
import exception.InvalidBookingException; // Imports the custom exception thrown when booking validation fails
import model.Booking;            // Imports the Booking model class (represents a booking record)
import model.Room;               // Imports the Room model class (represents a room record)
import util.ValidationUtil;      // Imports the ValidationUtil helper class for input validation

import java.sql.SQLException;    // Imports SQLException to handle database-level errors
import java.util.Date;           // Imports Date for check-in and check-out date handling
import java.util.List;           // Imports List to return collections of bookings

/**
 * BookingController – MVC Controller for the Booking module (main transaction).
 */
public class BookingController { // Declares the public BookingController class

    private final BookingDAO bookingDAO; // Private field to hold the BookingDAO instance (handles booking DB ops)
    private final RoomDAO    roomDAO;    // Private field to hold the RoomDAO instance (handles room DB ops)

    public BookingController() {         // Default constructor — initializes both DAO objects
        this.bookingDAO = new BookingDAO(); // Creates a new BookingDAO instance and assigns it to the field
        this.roomDAO    = new RoomDAO();    // Creates a new RoomDAO instance and assigns it to the field
    }

    // ── CREATE BOOKING ────────────────────────────────────────────────────────

    /**
     * Creates a new booking after full validation.
     *
     * @param customerId   selected customer ID
     * @param packageId    optional tour package ID (pass null if none)
     * @param roomId       selected room ID
     * @param checkinDate  check-in date
     * @param checkoutDate checkout date
     * @param totalAmount  pre-calculated total
     * @return true on success
     */
    public boolean createBooking(int customerId, Integer packageId, int roomId, // Method signature: takes customer, package, and room IDs
                                 Date checkinDate, Date checkoutDate, double totalAmount) // Takes check-in/out dates and total cost
            throws InvalidBookingException, SQLException { // Declares checked exceptions this method can throw

        // ── Validation ────────────────────────────────────────────────────────
        if (!ValidationUtil.isValidDateRange(checkinDate, checkoutDate)) // Checks if check-out date is after check-in date
            throw InvalidBookingException.checkoutBeforeCheckin(); // Throws a specific exception if the date range is invalid

        if (!ValidationUtil.isPositiveAmount(totalAmount)) // Checks if the total amount is a positive number
            throw InvalidBookingException.negativeAmount(); // Throws a specific exception if the amount is zero or negative

        // ── Duplicate room check ───────────────────────────────────────────────
        if (bookingDAO.isRoomBooked(roomId, checkinDate, checkoutDate, -1)) // Checks if the room is already booked for the given dates (-1 means no existing booking to exclude)
            throw InvalidBookingException.roomNotAvailable(String.valueOf(roomId)); // Throws exception if the room is already taken

        // ── Create booking ────────────────────────────────────────────────────
        Booking booking = new Booking(customerId, packageId, roomId,  // Creates a new Booking object with customer, package, and room IDs
                                      new Date(), checkinDate, checkoutDate, // Sets booking date to now, and stores check-in/out dates
                                      totalAmount, Booking.Status.CONFIRMED); // Sets the total amount and marks booking status as CONFIRMED
        boolean result = bookingDAO.addBooking(booking); // Saves the booking to the database and stores success/failure result

        // ── Mark room as BOOKED ───────────────────────────────────────────────
        if (result) { // Only update room availability if the booking was successfully saved
            roomDAO.updateAvailability(roomId, Room.Availability.BOOKED); // Updates the room's status to BOOKED in the database
        }
        return result; // Returns true if booking was created successfully, false otherwise
    }

    // ── UPDATE BOOKING ────────────────────────────────────────────────────────

    public boolean updateBooking(Booking booking) throws InvalidBookingException, SQLException { // Method to update an existing booking; throws validation and DB exceptions
        if (!ValidationUtil.isValidDateRange(booking.getCheckinDate(), booking.getCheckoutDate())) // Validates that the updated dates are a valid range
            throw InvalidBookingException.checkoutBeforeCheckin(); // Throws exception if updated checkout is before check-in

        if (bookingDAO.isRoomBooked(booking.getRoomId(), // Checks if the room is already booked for the new dates
                booking.getCheckinDate(), booking.getCheckoutDate(), booking.getBookingId())) // Passes the current booking's own ID to exclude it from the conflict check
            throw InvalidBookingException.roomNotAvailable(String.valueOf(booking.getRoomId())); // Throws exception if another booking conflicts with the updated dates

        return bookingDAO.updateBooking(booking); // Calls DAO to update the booking record in the database and returns success/failure
    }

    // ── CANCEL BOOKING ────────────────────────────────────────────────────────

    public boolean cancelBooking(int bookingId) throws SQLException { // Method to cancel a booking by its ID; throws SQLException on DB error
        Booking booking = bookingDAO.getBookingById(bookingId); // Fetches the booking record from the database using the given ID
        if (booking != null) { // Proceeds only if a booking with that ID actually exists
            boolean result = bookingDAO.updateBookingStatus(bookingId, Booking.Status.CANCELLED); // Updates the booking's status to CANCELLED in the database
            if (result) { // Only release the room if the status update was successful
                // Release the room
                roomDAO.updateAvailability(booking.getRoomId(), Room.Availability.AVAILABLE); // Sets the room's availability back to AVAILABLE so it can be re-booked
            }
            return result; // Returns true if cancellation succeeded, false otherwise
        }
        return false; // Returns false if no booking was found with the given ID
    }

    // ── STATUS UPDATE ─────────────────────────────────────────────────────────

    public boolean updateStatus(int bookingId, Booking.Status status) throws SQLException { // Method to change a booking's status to any given Status enum value
        return bookingDAO.updateBookingStatus(bookingId, status); // Delegates the status update to the DAO and returns the result
    }

    /**
     * Permanently deletes a booking record from the database.
     * Call cancelBooking() first so the room is released back to AVAILABLE.
     *
     * @param bookingId the booking_id to delete
     * @return true if deleted successfully
     * @throws SQLException on database error
     */
    public boolean deleteBooking(int bookingId) throws SQLException { // Method to permanently delete a booking record by its ID
        return bookingDAO.deleteBooking(bookingId); // Delegates the deletion to the DAO and returns true if successful
    }

    public List<Booking> getAllBookings() throws SQLException { // Method to retrieve all booking records from the database
        return bookingDAO.getAllBookings(); // Calls DAO to fetch and return all bookings as a List
    }

    public Booking getBookingById(int id) throws SQLException { // Method to fetch a single booking by its ID
        return bookingDAO.getBookingById(id); // Calls DAO to query and return the matching Booking object
    }

    public List<Booking> getRecentBookings(int limit) throws SQLException { // Method to retrieve a limited number of the most recent bookings
        return bookingDAO.getRecentBookings(limit); // Calls DAO to fetch the most recent 'limit' bookings and returns the list
    }

    public int getTotalBookings() throws SQLException { // Method to get the total count of all bookings in the system
        return bookingDAO.getTotalBookings(); // Calls DAO to query and return the total number of bookings
    }

    public double getTotalRevenue() throws SQLException { // Method to get the sum of all booking amounts (total revenue)
        return bookingDAO.getTotalRevenue(); // Calls DAO to compute and return the total revenue as a double
    }

    public double[] getMonthlyRevenue() throws SQLException { // Method to get revenue broken down by month (returns an array of 12 doubles)
        return bookingDAO.getMonthlyRevenue(); // Calls DAO to compute and return the monthly revenue array
    }

    // ── Amount calculator ─────────────────────────────────────────────────────

    /**
     * Calculates the total booking cost: (nights × room price) + package price.
     */
    public double calculateTotal(int roomId, Integer packageId, // Method to calculate the total cost given a room, optional package, and dates
                                 Date checkinDate, Date checkoutDate) throws SQLException { // Takes check-in/out dates; throws SQLException if DB lookup fails
        Room room = roomDAO.getRoomById(roomId); // Fetches the Room object from the database using the given room ID
        if (room == null) return 0; // If no room is found, return 0 to avoid a NullPointerException

        long diffMs = checkoutDate.getTime() - checkinDate.getTime(); // Calculates the time difference between check-out and check-in in milliseconds
        int nights  = (int)(diffMs / (1000L * 60 * 60 * 24)); // Converts milliseconds to full days (number of nights stayed)
        if (nights <= 0) nights = 1; // Ensures at least 1 night is charged even if dates are the same or invalid

        double total = nights * room.getPricePerNight(); // Calculates base cost: number of nights multiplied by the room's nightly price

        if (packageId != null) { // If a tour package was selected (packageId is not null)
            dao.PackageDAO pkgDAO = new dao.PackageDAO(); // Creates a new PackageDAO instance to query package data
            model.Package pkg = pkgDAO.getPackageById(packageId); // Fetches the Package object from the database using the given package ID
            if (pkg != null) total += pkg.getPrice(); // Adds the package price to the total if the package was found
        }
        return total; // Returns the final calculated total amount
    }
}