package dao; // Declares this class belongs to the DAO package (data access layer)

import model.Room; // Imports Room model class (represents hotel room entity)
import util.DatabaseConnection; // Imports singleton database connection utility

import java.sql.*; // Imports JDBC classes (Connection, PreparedStatement, ResultSet, Statement)
import java.util.ArrayList; // Imports ArrayList for storing Room objects
import java.util.List; // Imports List interface

/**
 * RoomDAO – DAO Pattern for Hotel Rooms.
 * Handles all CRUD operations related to rooms in the database.
 */
public class RoomDAO { // Defines RoomDAO class

    private Connection getConnection() { // Helper method to get database connection
        return DatabaseConnection.getInstance().getConnection(); // Returns shared connection instance
    }

    // ── CREATE ────────────────────────────────────────────────────────────────

    public boolean addRoom(Room room) throws SQLException { // Inserts a new room into database

        String sql = "INSERT INTO rooms (room_number, room_type, capacity, price_per_night, availability) " + // INSERT query part 1
                     "VALUES (?, ?, ?, ?, ?)"; // Placeholder parameters

        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { // Prepared statement with generated keys enabled

            ps.setString(1, room.getRoomNumber()); // Sets room number
            ps.setString(2, room.getRoomType().name()); // Converts enum RoomType to string
            ps.setInt(3, room.getCapacity()); // Sets room capacity
            ps.setDouble(4, room.getPricePerNight()); // Sets price per night
            ps.setString(5, room.getAvailability().name()); // Converts enum Availability to string

            int rows = ps.executeUpdate(); // Executes INSERT query and returns affected rows

            if (rows > 0) { // Checks if insert succeeded

                try (ResultSet keys = ps.getGeneratedKeys()) { // Retrieves generated primary key

                    if (keys.next()) // Moves to first key row
                        room.setRoomId(keys.getInt(1)); // Sets generated room ID into object
                }

                return true; // Insert successful
            }

            return false; // Insert failed
        }
    }

    // ── READ ──────────────────────────────────────────────────────────────────

    public List<Room> getAllRooms() throws SQLException { // Retrieves all rooms

        List<Room> list = new ArrayList<>(); // List to store rooms

        String sql = "SELECT * FROM rooms ORDER BY room_number"; // Query all rooms sorted by room number

        try (Statement st = getConnection().createStatement(); // Create statement
             ResultSet rs = st.executeQuery(sql)) { // Execute query

            while (rs.next()) // Loop through rows
                list.add(map(rs)); // Convert each row to Room object
        }

        return list; // Return list of rooms
    }

    public List<Room> getAvailableRooms() throws SQLException { // Retrieves only available rooms

        List<Room> list = new ArrayList<>(); // List for available rooms

        String sql = "SELECT * FROM rooms WHERE availability='AVAILABLE' ORDER BY room_type, room_number"; // Filter available rooms

        try (Statement st = getConnection().createStatement(); // Create statement
             ResultSet rs = st.executeQuery(sql)) { // Execute query

            while (rs.next()) // Iterate results
                list.add(map(rs)); // Map each row to Room object
        }

        return list; // Return available rooms
    }

    public Room getRoomById(int id) throws SQLException { // Retrieves a room by ID

        String sql = "SELECT * FROM rooms WHERE room_id = ?"; // Query with parameter

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) { // Prepared statement

            ps.setInt(1, id); // Set room ID parameter

            try (ResultSet rs = ps.executeQuery()) { // Execute query

                if (rs.next()) // If record exists
                    return map(rs); // Convert row to Room object
            }
        }

        return null; // Return null if not found
    }

    public List<Room> searchRooms(String keyword) throws SQLException { // Searches rooms

        List<Room> list = new ArrayList<>(); // Search result list

        String sql = "SELECT * FROM rooms WHERE room_number LIKE ? OR room_type LIKE ? ORDER BY room_number"; // Search query

        String p = "%" + keyword + "%"; // Wildcard pattern for LIKE search

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) { // Prepared statement

            ps.setString(1, p); // Search room_number
            ps.setString(2, p); // Search room_type

            try (ResultSet rs = ps.executeQuery()) { // Execute query

                while (rs.next()) // Loop results
                    list.add(map(rs)); // Convert to Room object
            }
        }

        return list; // Return matching rooms
    }

    public int getTotalAvailableRooms() throws SQLException { // Counts available rooms

        String sql = "SELECT COUNT(*) FROM rooms WHERE availability='AVAILABLE'"; // Count query

        try (Statement st = getConnection().createStatement(); // Statement object
             ResultSet rs = st.executeQuery(sql)) { // Execute query

            if (rs.next()) // Move to first row
                return rs.getInt(1); // Return count
        }

        return 0; // Return 0 if no result
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    public boolean updateRoom(Room room) throws SQLException { // Updates full room details

        String sql = "UPDATE rooms SET room_number=?, room_type=?, capacity=?, " + // Update query part 1
                     "price_per_night=?, availability=? WHERE room_id=?"; // Update condition

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) { // Prepared statement

            ps.setString(1, room.getRoomNumber()); // Set room number
            ps.setString(2, room.getRoomType().name()); // Convert enum to string
            ps.setInt(3, room.getCapacity()); // Set capacity
            ps.setDouble(4, room.getPricePerNight()); // Set price
            ps.setString(5, room.getAvailability().name()); // Set availability
            ps.setInt(6, room.getRoomId()); // WHERE condition (room ID)

            return ps.executeUpdate() > 0; // Return true if update succeeded
        }
    }

    public boolean updateAvailability(int roomId, Room.Availability availability) throws SQLException { // Updates only availability

        String sql = "UPDATE rooms SET availability=? WHERE room_id=?"; // Update availability only

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) { // Prepared statement

            ps.setString(1, availability.name()); // Convert enum to string
            ps.setInt(2, roomId); // Set room ID

            return ps.executeUpdate() > 0; // Return true if update successful
        }
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    public boolean deleteRoom(int id) throws SQLException { // Deletes a room

        String sql = "DELETE FROM rooms WHERE room_id = ?"; // DELETE query

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) { // Prepared statement

            ps.setInt(1, id); // Set room ID

            return ps.executeUpdate() > 0; // Return true if deletion succeeded
        }
    }

    // ── Private helper ────────────────────────────────────────────────────────

    private Room map(ResultSet rs) throws SQLException { // Converts DB row into Room object

        Room r = new Room(); // Create Room object

        r.setRoomId(rs.getInt("room_id")); // Set room ID
        r.setRoomNumber(rs.getString("room_number")); // Set room number

        r.setRoomType(Room.RoomType.valueOf(rs.getString("room_type"))); // Convert string to enum RoomType

        r.setCapacity(rs.getInt("capacity")); // Set capacity
        r.setPricePerNight(rs.getDouble("price_per_night")); // Set price per night

        r.setAvailability(Room.Availability.valueOf(rs.getString("availability"))); // Convert string to enum Availability

        return r; // Return populated Room object
    }
} // End of RoomDAO class