package controller; // Declares this file belongs to the 'controller' package (MVC Controller layer)

import dao.CustomerDAO;                  // Imports the CustomerDAO class for all customer-related database operations
import exception.InvalidBookingException; // Imports the custom exception used when input validation fails
import model.Customer;                   // Imports the Customer model class (represents a customer record)
import util.ValidationUtil;              // Imports the ValidationUtil helper class for validating input fields

import java.sql.SQLException;            // Imports SQLException to handle errors thrown by database operations
import java.util.List;                   // Imports List to return collections of Customer objects

/**
 * CustomerController – MVC Controller for Customer module.
 * Bridges the View (Swing forms) and the Model (DAO + Customer).
 */
public class CustomerController { // Declares the public CustomerController class

    private final CustomerDAO customerDAO; // Private field to hold the CustomerDAO instance for DB operations

    public CustomerController() {              // Default constructor — initializes the DAO
        this.customerDAO = new CustomerDAO();  // Creates a new CustomerDAO instance and assigns it to the field
    }

    // ── ADD ───────────────────────────────────────────────────────────────────

    /**
     * Validates and adds a new customer.
     * @throws InvalidBookingException on validation failure
     * @throws SQLException            on database error
     */
    public boolean addCustomer(String fullName, String nicPassport, String contactNumber, // Method to add a new customer; accepts name, NIC/passport, and contact number
                               String email, String address, String nationality)          // Also accepts email, address, and nationality
            throws InvalidBookingException, SQLException { // Declares the exceptions this method can throw

        validateCustomerFields(fullName, nicPassport, contactNumber, email); // Runs validation on the required fields before proceeding

        Customer customer = new Customer(fullName.trim(), nicPassport.trim(),      // Creates a new Customer object, trimming whitespace from name and NIC/passport
                                         contactNumber.trim(), email.trim(),       // Trims whitespace from contact number and email
                                         address.trim(), nationality.trim());      // Trims whitespace from address and nationality
        return customerDAO.addCustomer(customer); // Saves the new Customer to the database and returns true if successful
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    public boolean updateCustomer(int customerId, String fullName, String nicPassport, // Method to update an existing customer; takes the customer's ID and new name/NIC
                                  String contactNumber, String email, String address,  // Also takes updated contact number, email, and address
                                  String nationality)                                  // Also takes updated nationality
            throws InvalidBookingException, SQLException { // Declares the exceptions this method can throw

        validateCustomerFields(fullName, nicPassport, contactNumber, email); // Validates the updated fields before applying changes

        Customer customer = new Customer(fullName.trim(), nicPassport.trim(),      // Creates a new Customer object with trimmed updated values for name and NIC
                                         contactNumber.trim(), email.trim(),       // Trims updated contact number and email
                                         address.trim(), nationality.trim());      // Trims updated address and nationality
        customer.setCustomerId(customerId); // Sets the customer ID on the object so the DAO knows which record to update
        return customerDAO.updateCustomer(customer); // Sends the updated Customer to the DAO to persist changes and returns success/failure
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    public boolean deleteCustomer(int customerId) throws SQLException { // Method to delete a customer record by their ID; throws SQLException on DB error
        return customerDAO.deleteCustomer(customerId); // Delegates the deletion to the DAO and returns true if the record was deleted
    }

    // ── READ ──────────────────────────────────────────────────────────────────

    public List<Customer> getAllCustomers() throws SQLException { // Method to retrieve every customer record from the database
        return customerDAO.getAllCustomers(); // Calls the DAO to fetch all customers and returns them as a List
    }

    public Customer getCustomerById(int id) throws SQLException { // Method to fetch a single customer by their unique ID
        return customerDAO.getCustomerById(id); // Calls the DAO to query and return the matching Customer object
    }

    public List<Customer> searchCustomers(String keyword) throws SQLException { // Method to search for customers matching a given keyword (e.g. name or NIC)
        return customerDAO.searchCustomers(keyword); // Delegates the search query to the DAO and returns the matching results as a List
    }

    public int getTotalCustomers() throws SQLException { // Method to get the total count of all customers in the database
        return customerDAO.getTotalCustomers(); // Calls the DAO to query and return the total number of customer records
    }

    // ── Validation ────────────────────────────────────────────────────────────

    private void validateCustomerFields(String fullName, String nicPassport,  // Private helper method to validate the core customer fields
                                        String contactNumber, String email)   // Takes name, NIC/passport, phone, and email as inputs
            throws InvalidBookingException { // Throws InvalidBookingException if any field fails validation

        if (!ValidationUtil.isValidName(fullName))           // Checks if the full name is non-empty and contains valid characters
            throw InvalidBookingException.emptyField("Full Name"); // Throws a specific exception indicating the Full Name field is invalid/empty

        if (ValidationUtil.isNullOrEmpty(nicPassport))       // Checks if the NIC/Passport field is null or blank
            throw InvalidBookingException.emptyField("NIC / Passport"); // Throws a specific exception indicating the NIC/Passport field is missing

        if (!ValidationUtil.isValidPhone(contactNumber))     // Checks if the contact number matches a valid phone number format
            throw InvalidBookingException.invalidPhone();    // Throws a specific exception indicating the phone number is invalid

        if (!ValidationUtil.isValidEmail(email))             // Checks if the email address matches a valid email format
            throw InvalidBookingException.invalidEmail();    // Throws a specific exception indicating the email address is invalid
    }
}