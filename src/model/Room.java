package model; // Declares this class belongs to the model package (entity/data layer)

/**
 * Model – Hotel Room
 * Represents a room entity in the hotel system.
 */
public class Room { // Defines Room class

    public enum RoomType { SINGLE, DOUBLE, SUITE, DELUXE } // Enum representing types of rooms available

    public enum Availability { AVAILABLE, BOOKED, MAINTENANCE } // Enum representing current room status

    private int          roomId;        // Primary key (unique room ID)
    private String       roomNumber;    // Room identifier/number shown to users
    private RoomType     roomType;      // Type/category of room
    private int          capacity;      // Maximum number of guests allowed
    private double       pricePerNight; // Cost per night for the room
    private Availability availability;  // Current availability status

    // ── Constructors ──────────────────────────────────────────────────────────

    public Room() {} // Default constructor (required for DAO mapping)

    public Room(String roomNumber, RoomType roomType, int capacity,
                double pricePerNight, Availability availability) { // Parameterized constructor

        this.roomNumber    = roomNumber;    // Assign room number
        this.roomType      = roomType;      // Assign room type
        this.capacity      = capacity;      // Assign capacity
        this.pricePerNight = pricePerNight; // Assign price per night
        this.availability  = availability;  // Assign availability status
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────
    // Encapsulation: controlled access to private fields

    public int getRoomId() { return roomId; } // Returns room ID
    public void setRoomId(int roomId) { this.roomId = roomId; } // Sets room ID

    public String getRoomNumber() { return roomNumber; } // Returns room number
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; } // Sets room number

    public RoomType getRoomType() { return roomType; } // Returns room type
    public void setRoomType(RoomType roomType) { this.roomType = roomType; } // Sets room type

    public int getCapacity() { return capacity; } // Returns room capacity
    public void setCapacity(int capacity) { this.capacity = capacity; } // Sets room capacity

    public double getPricePerNight() { return pricePerNight; } // Returns price per night
    public void setPricePerNight(double p) { this.pricePerNight = p; } // Sets price per night

    public Availability getAvailability() { return availability; } // Returns availability status
    public void setAvailability(Availability a) { this.availability = a; } // Sets availability status

    @Override
    public String toString() { // Custom string representation for UI/debugging
        return "Room " + roomNumber + " (" + roomType + ") – LKR " + pricePerNight + "/night"; // Example display format
    }
} // End of Room class