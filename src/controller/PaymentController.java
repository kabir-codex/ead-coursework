package controller; // Declares this file belongs to the "controller" package
import dao.PaymentDAO; // Imports the PaymentDAO class, used to talk to the database for Payment data
import exception.InvalidBookingException; // Imports a custom exception class used for booking/validation errors
import model.Payment; // Imports the Payment model class representing a payment record
import util.ValidationUtil; // Imports a utility class containing helper methods for validating input data
import java.sql.SQLException; // Imports SQLException, used to handle database-related errors
import java.util.Date; // Imports the Date class, used to represent the payment date
import java.util.List; // Imports the List interface, used to hold collections of Payment objects
/**
 * PaymentController – MVC Controller for Payment module.
 */
public class PaymentController { // Declares the PaymentController class
    private final PaymentDAO paymentDAO; // Declares a private, immutable reference to a PaymentDAO object (handles DB operations)
    public PaymentController() { // Constructor for PaymentController, runs when a new instance is created
        this.paymentDAO = new PaymentDAO(); // Initializes the paymentDAO field with a new PaymentDAO instance
    } // End of constructor
    // ── RECORD PAYMENT ────────────────────────────────────────────────────────
    public boolean recordPayment(int bookingId, String amountStr, // Begins method signature for recording a payment, takes the booking ID and amount as a string
                                 Payment.PaymentMethod method, Date paymentDate, String notes) // Continues method signature, takes the payment method enum, payment date, and notes
            throws InvalidBookingException, SQLException { // Declares that this method can throw a validation exception or a SQL exception
        if (ValidationUtil.isNullOrEmpty(amountStr)) // Checks if the amount string is null or empty
            throw InvalidBookingException.emptyField("Amount"); // Throws an exception indicating the amount field is empty
        double amount; // Declares a variable to hold the parsed payment amount
        try { // Begins a try block to safely parse the amount string
            amount = Double.parseDouble(amountStr.trim()); // Trims whitespace from amountStr and parses it into a double, assigning it to amount
        } catch (NumberFormatException e) { // Catches the case where amountStr cannot be parsed as a number
            throw new InvalidBookingException("Amount must be a valid number.", // Throws a new exception with a message about invalid amount format
                    InvalidBookingException.CODE_NEGATIVE_AMOUNT, e); // Passes an error code and the original exception as the cause
        } // End of try-catch block
        if (!ValidationUtil.isPositiveAmount(amount)) // Checks if the parsed amount is NOT a positive value
            throw InvalidBookingException.negativeAmount(); // Throws an exception indicating the amount is negative or invalid
        if (paymentDate == null) // Checks if the payment date was not provided
            throw InvalidBookingException.emptyField("Payment Date"); // Throws an exception indicating the payment date field is empty
        Payment payment = new Payment(bookingId, amount, method, paymentDate, // Creates a new Payment object using the booking ID, amount, method, and date
                                      notes == null ? "" : notes.trim()); // Passes notes, using an empty string if notes is null, otherwise trimming whitespace
        return paymentDAO.addPayment(payment); // Calls the DAO to insert the new payment into the database and returns whether it succeeded
    } // End of recordPayment method
    // ── READ ──────────────────────────────────────────────────────────────────
    public List<Payment> getAllPayments() throws SQLException { // Declares a method to retrieve all payments, can throw an SQLException
        return paymentDAO.getAllPayments(); // Calls the DAO to fetch all payments from the database and returns the list
    } // End of getAllPayments method
    /**
     * Returns ALL bookings with their payment info (if any).
     * Bookings with no payments still appear, with empty payment fields.
     * Used by PaymentPanel to show all customers who have bookings.
     */
    public List<Payment> getAllBookingsWithPaymentInfo() throws SQLException { // Declares a method to retrieve all bookings along with any associated payment info, can throw an SQLException
        return paymentDAO.getAllBookingsWithPaymentInfo(); // Calls the DAO to fetch all bookings with payment info and returns the list
    } // End of getAllBookingsWithPaymentInfo method
    public List<Payment> getPaymentsByBookingId(int bookingId) throws SQLException { // Declares a method to retrieve all payments for a specific booking, can throw an SQLException
        return paymentDAO.getPaymentsByBookingId(bookingId); // Calls the DAO to fetch payments matching the given booking ID and returns the list
    } // End of getPaymentsByBookingId method
    public double getTotalPaidForBooking(int bookingId) throws SQLException { // Declares a method to get the total amount paid for a booking, can throw an SQLException
        return paymentDAO.getTotalPaidForBooking(bookingId); // Calls the DAO to sum up all payments for the booking and returns the total
    } // End of getTotalPaidForBooking method
    public double getBalanceDue(int bookingId, double totalAmount) throws SQLException { // Declares a method to calculate the remaining balance due for a booking, can throw an SQLException
        double paid = paymentDAO.getTotalPaidForBooking(bookingId); // Retrieves the total amount already paid for this booking
        return Math.max(0, totalAmount - paid); // Returns the difference between total amount and paid amount, but never less than 0
    } // End of getBalanceDue method
    /**
     * Permanently deletes a payment record by its payment_id.
     *
     * @param paymentId the payment_id to delete
     * @return true if deleted
     * @throws SQLException on database error
     */
    public boolean deletePayment(int paymentId) throws SQLException { // Declares a method to delete a payment by its ID, can throw an SQLException
        return paymentDAO.deletePayment(paymentId); // Calls the DAO to delete the payment from the database and returns whether it succeeded
    } // End of deletePayment method
} // End of PaymentController class