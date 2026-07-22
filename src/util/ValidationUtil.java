package util; // Declares this class belongs to the utility package (helper functions)

import java.util.Date; // Imports Date class for date validation
import java.util.regex.Pattern; // Imports regex Pattern class for input validation

/**
 * ValidationUtil
 * Centralised static validation helpers used across all controllers.
 */
public class ValidationUtil { // Utility class containing only static validation methods

    // ── Regex patterns ────────────────────────────────────────────────────────

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"); // Email validation pattern

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^[0-9+\\-\\s]{7,15}$"); // Phone number validation pattern (digits, +, -, space)

    private static final Pattern NAME_PATTERN =
            Pattern.compile("^[A-Za-z\\s.'-]{2,100}$"); // Name validation pattern (letters + spaces + punctuation)

    // ── Prevent instantiation ─────────────────────────────────────────────────

    private ValidationUtil() {} // Private constructor prevents creating instances of this utility class

    // ── String checks ─────────────────────────────────────────────────────────

    /** Returns true if str is null or blank. */
    public static boolean isNullOrEmpty(String str) { // Checks if string is null or empty
        return str == null || str.trim().isEmpty(); // Return true if invalid string
    }

    /** Validates a person's full name (letters, spaces, hyphens, apostrophes). */
    public static boolean isValidName(String name) { // Validates name format
        return !isNullOrEmpty(name) && NAME_PATTERN.matcher(name.trim()).matches(); // Must not be empty and match regex
    }

    /** Validates e-mail address format. */
    public static boolean isValidEmail(String email) { // Validates email format
        if (isNullOrEmpty(email)) return true; // Email is optional → empty is allowed

        return EMAIL_PATTERN.matcher(email.trim()).matches(); // Check against regex pattern
    }

    /** Validates phone number (digits, +, -, spaces, 7–15 chars). */
    public static boolean isValidPhone(String phone) { // Validates phone format
        return !isNullOrEmpty(phone) && PHONE_PATTERN.matcher(phone.trim()).matches(); // Must not be empty and match pattern
    }

    // ── Numeric checks ────────────────────────────────────────────────────────

    /** Returns true if amount > 0. */
    public static boolean isPositiveAmount(double amount) { // Checks positive monetary value
        return amount > 0; // Must be greater than zero
    }

    /** Returns true if value is a parseable positive integer string. */
    public static boolean isPositiveInteger(String value) { // Validates integer string input
        try {
            return Integer.parseInt(value.trim()) > 0; // Parse and check positive
        } catch (NumberFormatException e) {
            return false; // Invalid number format returns false
        }
    }

    // ── Date checks ───────────────────────────────────────────────────────────

    /** Returns true if checkOut is strictly after checkIn. */
    public static boolean isValidDateRange(Date checkIn, Date checkOut) { // Validates date range
        if (checkIn == null || checkOut == null) return false; // Null check
        return checkOut.after(checkIn); // Checkout must be after checkin
    }

    /** Returns true if date is today or in the future. */
    public static boolean isFutureOrToday(Date date) { // Checks if date is today or future
        if (date == null) return false; // Null check

        Date today = new Date(); // Current system date/time

        // strip time component (ignore hours/minutes/seconds)
        return !date.before(stripTime(today)); // Compare only date portion
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    @SuppressWarnings("deprecation") // Suppresses warning for old Date constructor usage
    private static Date stripTime(Date d) { // Removes time portion from date
        Date stripped = new Date(d.getYear(), d.getMonth(), d.getDate()); // Create date with only year/month/day
        return stripped; // Return normalized date
    }
} // End of ValidationUtil class