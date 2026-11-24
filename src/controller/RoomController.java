package controller; // Declares this file belongs to the "controller" package

import dao.RoomDAO; // Imports the RoomDAO class, used to talk to the database for Room data
import exception.InvalidBookingException; // Imports a custom exception class used for booking/validation errors
import model.Room; // Imports the Room model class representing a hotel room
import util.ValidationUtil; // Imports a utility class containing helper methods for validating input data

import java.sql.SQLException; // Imports SQLException, used to handle database-related errors
import java.util.List; // Imports the List interface, used to hold collections of Room objects

/**
 * RoomController – MVC Controller for Room module.
 */
public class RoomController { // Declares the RoomController class

    private final RoomDAO roomDAO; // Declares a private, immutable reference to a RoomDAO object (handles DB operations)

    public RoomController() { // Constructor for RoomController, runs when a new instance is created
        this.roomDAO = new RoomDAO(); // Initializes the roomDAO field with a new RoomDAO instance
    } // End of constructor

    // ── ADD ───────────────────────────────────────────────────────────────────

    public boolean addRoom(String roomNumber, Room.RoomType roomType, String capacityStr, // Begins method signature for adding a new room, takes room number, room type enum, and capacity as a string
                           String priceStr, Room.Availability availability) // Continues method signature, takes price as a string and availability enum
            throws InvalidBookingException, SQLException { // Declares that this method can throw a validation exception or a SQL exception

        validateRoomFields(roomNumber, capacityStr, priceStr); // Calls a private method to validate all the input fields before proceeding

        int    capacity = Integer.parseInt(capacityStr.trim()); // Trims whitespace from capacityStr and converts it to an integer
        double price    = Double.parseDouble(priceStr.trim()); // Trims whitespace from priceStr and converts it to a double

        Room room = new Room(roomNumber.trim(), roomType, capacity, price, availability); // Creates a new Room object, trimming whitespace from the room number and passing the other fields
        return roomDAO.addRoom(room); // Calls the DAO to insert the new room into the database and returns whether it succeeded
    } // End of addRoom method

    // ── UPDATE ────────────────────────────────────────────────────────────────

    public boolean updateRoom(int roomId, String roomNumber, Room.RoomType roomType, // Begins method signature for updating an existing room, takes the room's ID, number, and type
                              String capacityStr, String priceStr, Room.Availability availability) // Continues method signature, takes capacity, price (both as strings), and availability
            throws InvalidBookingException, SQLException { // Declares that this method can throw a validation exception or a SQL exception

        validateRoomFields(roomNumber, capacityStr, priceStr); // Validates the input fields before proceeding with the update

        int    capacity = Integer.parseInt(capacityStr.trim()); // Trims whitespace from capacityStr and converts it to an integer
        double price    = Double.parseDouble(priceStr.trim()); // Trims whitespace from priceStr and converts it to a double

        Room room = new Room(roomNumber.trim(), roomType, capacity, price, availability); // Creates a new Room object, trimming whitespace from the room number and passing the other fields
        room.setRoomId(roomId); // Sets the room's ID on the newly created object so the DAO knows which record to update
        return roomDAO.updateRoom(room); // Calls the DAO to update the room record in the database and returns whether it succeeded
    } // End of updateRoom method

    // ── DELETE ────────────────────────────────────────────────────────────────

    public boolean deleteRoom(int roomId) throws SQLException { // Declares a method to delete a room by its ID, can throw an SQLException
        return roomDAO.deleteRoom(roomId); // Calls the DAO to delete the room from the database and returns whether it succeeded
    } // End of deleteRoom method

    // ── READ ──────────────────────────────────────────────────────────────────

    public List<Room> getAllRooms() throws SQLException { // Declares a method to retrieve all rooms, can throw an SQLException
        return roomDAO.getAllRooms(); // Calls the DAO to fetch all rooms from the database and returns the list
    } // End of getAllRooms method

    public List<Room> getAvailableRooms() throws SQLException { // Declares a method to retrieve only available rooms, can throw an SQLException
        return roomDAO.getAvailableRooms(); // Calls the DAO to fetch available rooms from the database and returns the list
    } // End of getAvailableRooms method

    public Room getRoomById(int id) throws SQLException { // Declares a method to retrieve a single room by its ID, can throw an SQLException
        return roomDAO.getRoomById(id); // Calls the DAO to fetch the room matching the given ID and returns it
    } // End of getRoomById method

    public List<Room> searchRooms(String keyword) throws SQLException { // Declares a method to search rooms by a keyword, can throw an SQLException
        return roomDAO.searchRooms(keyword); // Calls the DAO to search for rooms matching the keyword and returns the list
    } // End of searchRooms method

    public int getTotalAvailableRooms() throws SQLException { // Declares a method to get the total count of available rooms, can throw an SQLException
        return roomDAO.getTotalAvailableRooms(); // Calls the DAO to get the total number of available rooms and returns it
    } // End of getTotalAvailableRooms method

    // ── Validation ────────────────────────────────────────────────────────────

    private void validateRoomFields(String roomNumber, String capacityStr, String priceStr) // Declares a private helper method to validate room input fields, takes room number, capacity, and price as strings
            throws InvalidBookingException { // Declares that this method can throw a validation exception

        if (ValidationUtil.isNullOrEmpty(roomNumber)) // Checks if the room number is null or empty
            throw InvalidBookingException.emptyField("Room Number"); // Throws an exception indicating the room number field is empty

        if (!ValidationUtil.isPositiveInteger(capacityStr)) // Checks if capacityStr is NOT a valid positive integer
            throw new InvalidBookingException("Capacity must be a positive integer.", // Throws a new exception with a message about invalid capacity
                    InvalidBookingException.CODE_EMPTY_FIELD); // Passes an error code indicating an empty/invalid field

        try { // Begins a try block to safely parse the price string
            double price = Double.parseDouble(priceStr.trim()); // Trims whitespace from priceStr and parses it into a double
            if (!ValidationUtil.isPositiveAmount(price)) // Checks if the parsed price is NOT a positive amount
                throw InvalidBookingException.negativeAmount(); // Throws an exception indicating the amount is negative or invalid
        } catch (NumberFormatException e) { // Catches the case where priceStr cannot be parsed as a number
            throw new InvalidBookingException("Price per night must be a valid number.", // Throws a new exception with a message about invalid price format
                    InvalidBookingException.CODE_NEGATIVE_AMOUNT, e); // Passes an error code and the original exception as the cause
        } // End of try-catch block
    } // End of validateRoomFields method
} // End of RoomController class