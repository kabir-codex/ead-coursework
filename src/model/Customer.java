package model; // Declares this class belongs to the model package (entity/data layer)

/**
 * Model – Customer
 * Represents a customer record in the system.
 */
public class Customer { // Defines Customer class

    private int    customerId;      // Primary key (unique customer ID)
    private String fullName;        // Customer full name
    private String nicPassport;     // National ID or Passport number
    private String contactNumber;   // Phone number
    private String email;           // Email address
    private String address;         // Residential address
    private String nationality;     // Nationality of customer

    // ── Constructors ──────────────────────────────────────────────────────────

    public Customer() {} // Default constructor (needed for frameworks / DAO mapping)

    public Customer(String fullName, String nicPassport, String contactNumber,
                    String email, String address, String nationality) { // Parameterized constructor

        this.fullName      = fullName;      // Assign full name
        this.nicPassport   = nicPassport;   // Assign NIC/Passport
        this.contactNumber = contactNumber; // Assign contact number
        this.email         = email;         // Assign email
        this.address       = address;       // Assign address
        this.nationality   = nationality;   // Assign nationality
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────
    // Encapsulation: allows controlled access to private fields

    public int    getCustomerId()                    { return customerId; } // Returns customer ID
    public void   setCustomerId(int customerId)      { this.customerId = customerId; } // Sets customer ID

    public String getFullName()                      { return fullName; } // Returns full name
    public void   setFullName(String fullName)       { this.fullName = fullName; } // Sets full name

    public String getNicPassport()                   { return nicPassport; } // Returns NIC/Passport
    public void   setNicPassport(String nicPassport) { this.nicPassport = nicPassport; } // Sets NIC/Passport

    public String getContactNumber()                 { return contactNumber; } // Returns contact number
    public void   setContactNumber(String n)         { this.contactNumber = n; } // Sets contact number

    public String getEmail()                         { return email; } // Returns email
    public void   setEmail(String email)             { this.email = email; } // Sets email

    public String getAddress()                       { return address; } // Returns address
    public void   setAddress(String address)         { this.address = address; } // Sets address

    public String getNationality()                   { return nationality; } // Returns nationality
    public void   setNationality(String nationality) { this.nationality = nationality; } // Sets nationality

    @Override
    public String toString() { // Custom string representation for display purposes
        return fullName + " [" + nicPassport + "]"; // Format: John Doe [NIC123]
    }
} // End of Customer class