package model; // Declares this class belongs to the model package (entity/data layer)

/**
 * Model – Tour Package
 * Represents a travel/tour package offered by the system.
 */
public class Package { // Defines Package model class

    public enum Status { ACTIVE, INACTIVE } // Enum representing whether package is available or not

    private int    packageId;      // Primary key (unique package ID)
    private String packageName;    // Name of the tour package
    private String destination;    // Destination location of the package
    private int    durationDays;   // Duration of tour in days
    private double price;          // Price of the package
    private String description;    // Detailed description of package
    private Status status;         // Current status (ACTIVE or INACTIVE)

    // ── Constructors ──────────────────────────────────────────────────────────

    public Package() {} // Default constructor (required for DAO mapping / frameworks)

    public Package(String packageName, String destination, int durationDays,
                   double price, String description, Status status) { // Parameterized constructor

        this.packageName  = packageName;  // Assign package name
        this.destination  = destination;  // Assign destination
        this.durationDays = durationDays; // Assign duration in days
        this.price        = price;        // Assign price
        this.description  = description;  // Assign description
        this.status       = status;       // Assign status (ACTIVE/INACTIVE)
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────
    // Encapsulation: controlled access to private fields

    public int    getPackageId()                      { return packageId; } // Returns package ID
    public void   setPackageId(int packageId)        { this.packageId = packageId; } // Sets package ID

    public String getPackageName()                    { return packageName; } // Returns package name
    public void   setPackageName(String packageName)  { this.packageName = packageName; } // Sets package name

    public String getDestination()                    { return destination; } // Returns destination
    public void   setDestination(String destination)  { this.destination = destination; } // Sets destination

    public int    getDurationDays()                   { return durationDays; } // Returns duration in days
    public void   setDurationDays(int durationDays)   { this.durationDays = durationDays; } // Sets duration in days

    public double getPrice()                          { return price; } // Returns price
    public void   setPrice(double price)              { this.price = price; } // Sets price

    public String getDescription()                    { return description; } // Returns description
    public void   setDescription(String description)  { this.description = description; } // Sets description

    public Status getStatus()                         { return status; } // Returns package status
    public void   setStatus(Status status)            { this.status = status; } // Sets package status

    @Override
    public String toString() { // Custom string representation for UI/display
        return packageName + " – " + destination; // Example: "Honeymoon – Maldives"
    }
} // End of Package class