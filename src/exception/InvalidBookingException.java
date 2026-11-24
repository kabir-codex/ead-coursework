package exception; // Declares this class belongs to the exception package

/**
 * InvalidBookingException – User-Defined Exception
 *
 * Thrown when business-rule violations are detected during the
 * booking workflow (date conflicts, duplicate rooms, bad amounts, etc.)
 */
public class InvalidBookingException extends Exception { // Custom checked exception class extending Exception

    // ── Error codes (optional; useful for i18n / logging) ─────────────────────

    public static final int CODE_DATE_INVALID        = 1001; // Error code for invalid date format or value
    public static final int CODE_CHECKOUT_BEFORE_IN  = 1002; // Error code when checkout date is before check-in
    public static final int CODE_ROOM_NOT_AVAILABLE  = 1003; // Error code when room is not available
    public static final int CODE_NEGATIVE_AMOUNT     = 1004; // Error code for negative payment amount
    public static final int CODE_EMPTY_FIELD         = 1005; // Error code for missing required field
    public static final int CODE_INVALID_EMAIL       = 1006; // Error code for invalid email format
    public static final int CODE_INVALID_PHONE       = 1007; // Error code for invalid phone number format
    public static final int CODE_DUPLICATE_BOOKING   = 1008; // Error code for duplicate booking conflict

    private final int errorCode; // Stores specific error code for this exception instance

    // ── Constructors ──────────────────────────────────────────────────────────

    /** Simple message-only constructor. */
    public InvalidBookingException(String message) { // Constructor with only error message
        super(message); // Pass message to parent Exception class
        this.errorCode = 0; // Default error code (0 means unspecified)
    }

    /** Message + error code. */
    public InvalidBookingException(String message, int errorCode) { // Constructor with message and error code
        super(message); // Set exception message
        this.errorCode = errorCode; // Set specific error code
    }

    /** Message + cause (wrapping another exception). */
    public InvalidBookingException(String message, Throwable cause) { // Constructor with message and underlying cause
        super(message, cause); // Pass both message and cause to Exception
        this.errorCode = 0; // Default error code
    }

    /** Full constructor: message + code + cause. */
    public InvalidBookingException(String message, int errorCode, Throwable cause) { // Full constructor with all details
        super(message, cause); // Pass message and root cause
        this.errorCode = errorCode; // Set error code
    }

    // ── Accessors ─────────────────────────────────────────────────────────────

    public int getErrorCode() { // Getter for error code
        return errorCode; // Returns stored error code
    }

    @Override
    public String toString() { // Custom string representation of exception
        return "InvalidBookingException[code=" + errorCode + "]: " + getMessage(); // Formats error code + message
    }

    // ── Static factory helpers ────────────────────────────────────────────────
    // These methods make it easier to throw common booking-related exceptions

    public static InvalidBookingException checkoutBeforeCheckin() { // Factory for date validation error
        return new InvalidBookingException(
            "Check-out date must be after check-in date.", CODE_CHECKOUT_BEFORE_IN); // Create exception with message + code
    }

    public static InvalidBookingException roomNotAvailable(String roomNumber) { // Factory for unavailable room
        return new InvalidBookingException(
            "Room " + roomNumber + " is not available for the selected dates.", CODE_ROOM_NOT_AVAILABLE); // Include room number in message
    }

    public static InvalidBookingException negativeAmount() { // Factory for invalid payment amount
        return new InvalidBookingException(
            "Payment amount must be greater than zero.", CODE_NEGATIVE_AMOUNT); // Message + error code
    }

    public static InvalidBookingException emptyField(String fieldName) { // Factory for missing field validation
        return new InvalidBookingException(
            "Required field '" + fieldName + "' cannot be empty.", CODE_EMPTY_FIELD); // Include field name in message
    }

    public static InvalidBookingException invalidEmail() { // Factory for invalid email format
        return new InvalidBookingException(
            "The e-mail address format is invalid.", CODE_INVALID_EMAIL); // Email validation error
    }

    public static InvalidBookingException invalidPhone() { // Factory for invalid phone format
        return new InvalidBookingException(
            "The phone number format is invalid (7–15 digits).", CODE_INVALID_PHONE); // Phone validation error
    }

    public static InvalidBookingException duplicateBooking() { // Factory for duplicate booking error
        return new InvalidBookingException(
            "A booking already exists for this room on the selected dates.", CODE_DUPLICATE_BOOKING); // Duplicate booking message
    }
} // End of InvalidBookingException class