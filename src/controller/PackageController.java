package controller; // Declares this file belongs to the "controller" package

import dao.PackageDAO; // Imports the PackageDAO class, used to talk to the database for Package data
import exception.InvalidBookingException; // Imports a custom exception class used for booking/validation errors
import model.Package; // Imports the Package model class representing a tour package
import util.ValidationUtil; // Imports a utility class containing helper methods for validating input data

import java.sql.SQLException; // Imports SQLException, used to handle database-related errors
import java.util.List; // Imports the List interface, used to hold collections of Package objects

/**
 * PackageController – MVC Controller for Tour Package module.
 */
public class PackageController { // Declares the PackageController class

    private final PackageDAO packageDAO; // Declares a private, immutable reference to a PackageDAO object (handles DB operations)

    public PackageController() { // Constructor for PackageController, runs when a new instance is created
        this.packageDAO = new PackageDAO(); // Initializes the packageDAO field with a new PackageDAO instance
    } // End of constructor

    // ── ADD ───────────────────────────────────────────────────────────────────

    public boolean addPackage(String packageName, String destination, String durationStr, // Begins method signature for adding a new package, takes package name, destination, and duration as a string
                              String priceStr, String description, Package.Status status) // Continues method signature, takes price as a string, description, and status enum
            throws InvalidBookingException, SQLException { // Declares that this method can throw a validation exception or a SQL exception

        validatePackageFields(packageName, destination, durationStr, priceStr); // Calls a private method to validate all the input fields before proceeding

        int    duration = Integer.parseInt(durationStr.trim()); // Trims whitespace from durationStr and converts it to an integer
        double price    = Double.parseDouble(priceStr.trim()); // Trims whitespace from priceStr and converts it to a double

        Package pkg = new Package(packageName.trim(), destination.trim(), // Creates a new Package object, trimming whitespace from name and destination
                                  duration, price, description.trim(), status); // Continues constructing the Package, passing duration, price, trimmed description, and status
        return packageDAO.addPackage(pkg); // Calls the DAO to insert the new package into the database and returns whether it succeeded
    } // End of addPackage method

    // ── UPDATE ────────────────────────────────────────────────────────────────

    public boolean updatePackage(int packageId, String packageName, String destination, // Begins method signature for updating an existing package, takes the package's ID, name, and destination
                                 String durationStr, String priceStr, String description, // Continues method signature, takes duration, price (both as strings), and description
                                 Package.Status status) // Continues method signature, takes the status enum
            throws InvalidBookingException, SQLException { // Declares that this method can throw a validation exception or a SQL exception

        validatePackageFields(packageName, destination, durationStr, priceStr); // Validates the input fields before proceeding with the update

        int    duration = Integer.parseInt(durationStr.trim()); // Trims whitespace from durationStr and converts it to an integer
        double price    = Double.parseDouble(priceStr.trim()); // Trims whitespace from priceStr and converts it to a double

        Package pkg = new Package(packageName.trim(), destination.trim(), // Creates a new Package object, trimming whitespace from name and destination
                                  duration, price, description.trim(), status); // Continues constructing the Package, passing duration, price, trimmed description, and status
        pkg.setPackageId(packageId); // Sets the package's ID on the newly created object so the DAO knows which record to update
        return packageDAO.updatePackage(pkg); // Calls the DAO to update the package record in the database and returns whether it succeeded
    } // End of updatePackage method

    // ── DELETE ────────────────────────────────────────────────────────────────

    public boolean deletePackage(int packageId) throws SQLException { // Declares a method to delete a package by its ID, can throw an SQLException
        return packageDAO.deletePackage(packageId); // Calls the DAO to delete the package from the database and returns whether it succeeded
    } // End of deletePackage method

    // ── READ ──────────────────────────────────────────────────────────────────

    public List<Package> getAllPackages() throws SQLException { // Declares a method to retrieve all packages, can throw an SQLException
        return packageDAO.getAllPackages(); // Calls the DAO to fetch all packages from the database and returns the list
    } // End of getAllPackages method

    public List<Package> getActivePackages() throws SQLException { // Declares a method to retrieve only active packages, can throw an SQLException
        return packageDAO.getActivePackages(); // Calls the DAO to fetch active packages from the database and returns the list
    } // End of getActivePackages method

    public Package getPackageById(int id) throws SQLException { // Declares a method to retrieve a single package by its ID, can throw an SQLException
        return packageDAO.getPackageById(id); // Calls the DAO to fetch the package matching the given ID and returns it
    } // End of getPackageById method

    public List<Package> searchPackages(String keyword) throws SQLException { // Declares a method to search packages by a keyword, can throw an SQLException
        return packageDAO.searchPackages(keyword); // Calls the DAO to search for packages matching the keyword and returns the list
    } // End of searchPackages method

    public int getTotalPackages() throws SQLException { // Declares a method to get the total count of packages, can throw an SQLException
        return packageDAO.getTotalPackages(); // Calls the DAO to get the total number of packages and returns it
    } // End of getTotalPackages method

    // ── Validation ────────────────────────────────────────────────────────────

    private void validatePackageFields(String packageName, String destination, // Declares a private helper method to validate package input fields, takes name and destination
                                       String durationStr, String priceStr) // Continues method signature, takes duration and price as strings
            throws InvalidBookingException { // Declares that this method can throw a validation exception

        if (ValidationUtil.isNullOrEmpty(packageName)) // Checks if the package name is null or empty
            throw InvalidBookingException.emptyField("Package Name"); // Throws an exception indicating the package name field is empty

        if (ValidationUtil.isNullOrEmpty(destination)) // Checks if the destination is null or empty
            throw InvalidBookingException.emptyField("Destination"); // Throws an exception indicating the destination field is empty

        if (!ValidationUtil.isPositiveInteger(durationStr)) // Checks if durationStr is NOT a valid positive integer
            throw new InvalidBookingException("Duration must be a positive number of days.", // Throws a new exception with a message about invalid duration
                    InvalidBookingException.CODE_EMPTY_FIELD); // Passes an error code indicating an empty/invalid field

        try { // Begins a try block to safely parse the price string
            double price = Double.parseDouble(priceStr.trim()); // Trims whitespace from priceStr and parses it into a double
            if (!ValidationUtil.isPositiveAmount(price)) // Checks if the parsed price is NOT a positive amount
                throw InvalidBookingException.negativeAmount(); // Throws an exception indicating the amount is negative or invalid
        } catch (NumberFormatException e) { // Catches the case where priceStr cannot be parsed as a number
            throw new InvalidBookingException("Price must be a valid positive number.", // Throws a new exception with a message about invalid price format
                    InvalidBookingException.CODE_NEGATIVE_AMOUNT, e); // Passes an error code and the original exception as the cause
        } // End of try-catch block
    } // End of validatePackageFields method
} // End of PackageController class