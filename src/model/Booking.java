package model; // Declares this class belongs to the model package (data layer / entity classes)

import java.util.Date; // Imports Java Date class for handling booking dates

/**
 * Model – Booking  (main transaction entity)
 * Represents a booking record in the system.
 */
public class Booking { // Defines Booking class

    public enum Status { CONFIRMED, PENDING, CANCELLED, COMPLETED } // Enum representing booking lifecycle states

    private int      bookingId;   // Primary key of booking
    private int      customerId;  // Foreign key referencing customer
    private Integer  packageId;   // Nullable foreign key (can be null if no package booked)
    private int      roomId;      // Foreign key referencing room

    private Date     bookingDate; // Date when booking was made
    private Date     checkinDate; // Check-in date for booking
    private Date     checkoutDate;// Check-out date for booking

    private double   totalAmount; // Total cost of booking
    private Status   status;      // Current booking status (CONFIRMED, etc.)

    // ── Transient display fields (populated via JOIN) ─────────────────────────
    // These are not stored directly in booking table but filled from joins

    private String customerName; // Customer name (from customers table join)
    private String packageName;  // Package name (from packages table join)
    private String roomNumber;  // Room number (from rooms table join)

    // ── Constructors ──────────────────────────────────────────────────────────

    public Booking() {} // Default no-argument constructor

    public Booking(int customerId, Integer packageId, int roomId,
                   Date bookingDate, Date checkinDate, Date checkoutDate,
                   double totalAmount, Status status) { // Parameterized constructor

        this.customerId   = customerId;   // Assign customer ID
        this.packageId    = packageId;    // Assign package ID (nullable)
        this.roomId       = roomId;       // Assign room ID
        this.bookingDate  = bookingDate;  // Assign booking date
        this.checkinDate  = checkinDate;  // Assign check-in date
        this.checkoutDate = checkoutDate; // Assign check-out date
        this.totalAmount  = totalAmount;  // Assign total amount
        this.status       = status;       // Assign booking status
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────
    // Standard accessors and mutators for encapsulation

    public int     getBookingId()                     { return bookingId; } // Returns booking ID
    public void    setBookingId(int bookingId)        { this.bookingId = bookingId; } // Sets booking ID

    public int     getCustomerId()                    { return customerId; } // Returns customer ID
    public void    setCustomerId(int customerId)      { this.customerId = customerId; } // Sets customer ID

    public Integer getPackageId()                     { return packageId; } // Returns package ID (nullable)
    public void    setPackageId(Integer packageId)    { this.packageId = packageId; } // Sets package ID

    public int     getRoomId()                        { return roomId; } // Returns room ID
    public void    setRoomId(int roomId)              { this.roomId = roomId; } // Sets room ID

    public Date    getBookingDate()                   { return bookingDate; } // Returns booking date
    public void    setBookingDate(Date bookingDate)   { this.bookingDate = bookingDate; } // Sets booking date

    public Date    getCheckinDate()                   { return checkinDate; } // Returns check-in date
    public void    setCheckinDate(Date checkinDate)   { this.checkinDate = checkinDate; } // Sets check-in date

    public Date    getCheckoutDate()                  { return checkoutDate; } // Returns check-out date
    public void    setCheckoutDate(Date checkoutDate) { this.checkoutDate = checkoutDate; } // Sets check-out date

    public double  getTotalAmount()                  { return totalAmount; } // Returns total amount
    public void    setTotalAmount(double totalAmount){ this.totalAmount = totalAmount; } // Sets total amount

    public Status  getStatus()                       { return status; } // Returns booking status
    public void    setStatus(Status status)          { this.status = status; } // Sets booking status

    public String  getCustomerName()                 { return customerName; } // Returns customer name (UI field)
    public void    setCustomerName(String n)         { this.customerName = n; } // Sets customer name

    public String  getPackageName()                  { return packageName; } // Returns package name
    public void    setPackageName(String n)          { this.packageName = n; } // Sets package name

    public String  getRoomNumber()                   { return roomNumber; } // Returns room number
    public void    setRoomNumber(String n)           { this.roomNumber = n; } // Sets room number

    /** Convenience: number of nights booked. */ // Helper method documentation
    public int getNights() { // Calculates number of nights between check-in and check-out

        if (checkinDate == null || checkoutDate == null) return 0; // If dates missing return 0

        long diff = checkoutDate.getTime() - checkinDate.getTime(); // Difference in milliseconds between dates

        return (int)(diff / (1000L * 60 * 60 * 24)); // Convert milliseconds to full days (nights)
    }

    @Override
    public String toString() { // Custom string representation of Booking object
        return "BK-" + bookingId + " [" + status + "]"; // Format: BK-123 [CONFIRMED]
    }
} // End of Booking class