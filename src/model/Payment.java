package model; // Declares this class belongs to the model package (entity/data layer)

import java.util.Date; // Imports Date class for handling payment date

/**
 * Model – Payment
 * Represents a payment transaction in the system.
 */
public class Payment { // Defines Payment model class

    public enum PaymentMethod { CASH, CARD, BANK_TRANSFER, ONLINE } // Enum for supported payment methods

    private int           paymentId;      // Primary key (unique payment ID)
    private int           bookingId;      // Foreign key referencing booking
    private double        amount;         // Payment amount
    private PaymentMethod paymentMethod;  // Method used for payment
    private Date          paymentDate;    // Date when payment was made
    private String        notes;          // Optional notes about payment

    // Transient
    private String customerName;          // Not stored in payments table (comes from JOIN)

    // ── Constructors ──────────────────────────────────────────────────────────

    public Payment() {} // Default constructor

    public Payment(int bookingId, double amount, PaymentMethod paymentMethod,
                   Date paymentDate, String notes) { // Parameterized constructor

        this.bookingId     = bookingId;     // Assign booking ID
        this.amount        = amount;        // Assign payment amount
        this.paymentMethod = paymentMethod; // Assign payment method
        this.paymentDate   = paymentDate;   // Assign payment date
        this.notes         = notes;         // Assign notes
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────
    // Encapsulation: controlled access to private fields

    public int getPaymentId() { return paymentId; } // Returns payment ID
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; } // Sets payment ID

    public int getBookingId() { return bookingId; } // Returns booking ID
    public void setBookingId(int bookingId) { this.bookingId = bookingId; } // Sets booking ID

    public double getAmount() { return amount; } // Returns payment amount
    public void setAmount(double amount) { this.amount = amount; } // Sets payment amount

    public PaymentMethod getPaymentMethod() { return paymentMethod; } // Returns payment method
    public void setPaymentMethod(PaymentMethod m) { this.paymentMethod = m; } // Sets payment method

    public Date getPaymentDate() { return paymentDate; } // Returns payment date
    public void setPaymentDate(Date paymentDate) { this.paymentDate = paymentDate; } // Sets payment date

    public String getNotes() { return notes; } // Returns notes
    public void setNotes(String notes) { this.notes = notes; } // Sets notes

    public String getCustomerName() { return customerName; } // Returns customer name (from JOIN, not DB column)
    public void setCustomerName(String n) { this.customerName = n; } // Sets customer name

    @Override
    public String toString() { // Custom string representation for UI/debugging
        return "PAY-" + paymentId + " LKR " + amount + " [" + paymentMethod + "]"; // Example: PAY-10 LKR 5000 [CASH]
    }
} // End of Payment class