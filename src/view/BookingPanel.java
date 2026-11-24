/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package view; // This class belongs to the "view" layer (MVC pattern)

import controller.BookingController;   // Handles booking business logic + DB operations
import controller.CustomerController;  // Handles customer-related DB operations
import controller.PackageController;   // Handles tour package operations
import controller.RoomController;      // Handles room operations
import exception.InvalidBookingException; // Custom exception for invalid bookings
import model.*; // Imports all model classes (Booking, Customer, Room, etc.)

import javax.swing.*; // Swing UI components
import javax.swing.table.DefaultTableModel; // Table data model
import javax.swing.table.DefaultTableCellRenderer; // Custom cell rendering
import java.awt.*; // For colors, fonts, layouts
import java.sql.SQLException; // Database error handling
import java.text.DecimalFormat; // Formatting currency
import java.text.SimpleDateFormat; // Formatting dates
import java.util.Date; // Date handling
import java.util.List; // List collections

/**
 * BookingPanel is a Swing UI panel responsible for:
 * - Creating bookings
 * - Viewing bookings
 * - Updating status
 * - Cancelling/deleting bookings
 */

public class BookingPanel extends javax.swing.JPanel implements MainFrame.Refreshable { // Declares BookingPanel — extends JPanel and implements Refreshable for auto-reload

     // ================= CONTROLLERS =================

    // Handles booking-related database operations (create, update, delete, fetch)
    private final BookingController bookingCtrl = new BookingController(); // Creates controller instance to handle business logic and database operations

    // Handles customer data retrieval
    private final CustomerController customerCtrl = new CustomerController(); // Creates controller instance to handle business logic and database operations

    // Handles package data retrieval
    private final PackageController packageCtrl = new PackageController(); // Creates controller instance to handle business logic and database operations

    // Handles room data retrieval
    private final RoomController roomCtrl = new RoomController(); // Creates controller instance to handle business logic and database operations

    // ================= UI STATE =================

    // Table model that stores booking rows shown in JTable
    private DefaultTableModel tableModel; // Stores the table data model managing all rows and columns in the JTable

    // Stores currently selected booking ID (-1 means nothing selected)
    private int selectedBookingId = -1; // Tracks the database ID of the currently selected record (-1 = nothing selected)

    // Flag to prevent combo-box listener from triggering during programmatic updates
    private boolean loadingFromTable = false; // Flag set to true during programmatic combo updates to prevent listener loops

    // In-memory lists that map combo-box indices → actual objects
    private List<Customer> customers;      // all customers
    private List<model.Package> packages;  // all packages
    private List<Room> rooms;              // all rooms

    // ================= CONSTRUCTOR =================
    
    public BookingPanel() { // No-arg constructor — called when this panel is first created by MainFrame
        
        initComponents();        // Auto-generated UI builder (NetBeans GUI)
        setupTable();            // Configure JTable appearance + model
        setupCustomerComboListener(); // Attach listener to customer combo box
        loadCombos();            // Load customers, packages, rooms
        loadTable();             // Load booking table data
    } // Closes this code block (end of method, class, or inner class)
    
    private void setupTable() {   // Configures JTable appearance: columns, fonts, colours, row height, selection listener
// Defines a method that configures the JTable (structure, design, behavior)

    tableModel = new DefaultTableModel(   // Creates the DefaultTableModel that holds all table rows and columns
    // Creates a new DefaultTableModel and assigns it to tableModel

        new String[]{   // Creates an array of values representing one table row of data
        // Starts a String array that defines column headers

            "ID","Customer","Room","Package",   // Column headers for the recent bookings table on the dashboard
            // Column 1: Booking ID
            // Column 2: Customer name
            // Column 3: Room info
            // Column 4: Package info

            "Check-In","Check-Out","Amount (LKR)","Status"   // Continuation of booking table column headers: Check-In, Check-Out, Amount, Status
            // Column 5: Check-in date
            // Column 6: Check-out date
            // Column 7: Total amount in LKR
            // Column 8: Booking status

        },   // Closes the data array and separates it from the column names array
        // Ends the column header array

        0   // Initial row count of 0 — table starts empty before data is loaded
        // Initial number of rows in table = 0 (empty table)

    ) {   // Opens the anonymous DefaultTableModel subclass body
    // Starts anonymous subclass of DefaultTableModel

        @Override   // Signals this method overrides a method from the parent class or interface
        // Indicates we are overriding a method from parent class

        public boolean isCellEditable(int r, int c) {   // Overrides parent — always returns false so table cells cannot be edited
        // Method that decides if a cell can be edited
        // r = row index, c = column index

            return false;   // Returns false — condition not met or validation failed
            // Prevents editing of any table cell

        }   // Closes this code block (end of method, class, or inner class)
        // Ends isCellEditable method

    };   // Closes this anonymous class or array definition
    // Ends DefaultTableModel object creation

    tblBookings.setModel(tableModel);   // Connects the data model to this table so rows appear in the UI
    // Assigns the table model to the JTable (connects data to UI)

    tblBookings.setRowHeight(28);   // Sets the height of each table row in pixels
    // Sets each row height to 28 pixels for better spacing

    tblBookings.setShowGrid(false);   // Hides grid lines between cells for a cleaner modern table look
    // Removes grid lines for cleaner UI appearance

    tblBookings.getTableHeader().setFont(   // Sets the font typeface, style, and size for this component
        new Font("Segoe UI", Font.BOLD, 12)   // Creates a font with specified name, style (BOLD/PLAIN) and size
        // Creates font: Segoe UI, bold, size 12 for table header
    );   // Closes the chained method call or group definition

    tblBookings.getTableHeader().setBackground(   // Sets the background fill colour of this component
        new Color(0x1A, 0x73, 0xE8)   // Creates a custom colour using RGB values
        // Sets header background color (blue shade)
    );   // Closes the chained method call or group definition

    tblBookings.getTableHeader().setForeground(Color.WHITE);   // Sets the text (foreground) colour of this component
    // Sets header text color to white

    tblBookings.setSelectionBackground(   // Sets the highlight colour shown on the selected table row
        new Color(0xBB, 0xDE, 0xFF)   // Creates a custom colour using RGB values
        // Sets highlight color when row is selected
    );   // Closes the chained method call or group definition

    tblBookings.getColumnModel().getColumn(7).setCellRenderer(   // Applies a custom cell renderer to this table column for styled display
    // Gets column index 7 (Status column) and assigns custom renderer

        new DefaultTableCellRenderer() {   // Creates a custom cell renderer to colour-code the Status column
        // Creates custom renderer class for table cells

            @Override   // Signals this method overrides a method from the parent class or interface
            // Overrides default rendering method

            public Component getTableCellRendererComponent(   // Returns the styled cell component for this specific table cell
                JTable t, Object val, boolean sel, boolean foc, int r, int c   // Parameters passed to the renderer: table, value, selected, focused, row, column
                // t = JTable reference
                // val = value in cell
                // sel = whether cell is selected
                // foc = whether cell has focus
                // r = row index
                // c = column index
            ) {   // Opens the anonymous DefaultTableModel subclass body

                JLabel lbl = (JLabel) super.getTableCellRendererComponent(   // Casts the value to the required type
                    t, val, sel, foc, r, c   // Passes all renderer parameters to the parent class for standard rendering first
                    // Calls default renderer and returns a JLabel
                );   // Closes the chained method call or group definition

                String v = val == null ? "" : val.toString();   // Converts this StringBuilder or object to a plain String
                // Converts cell value into string safely (avoids null errors)

                switch (v) {   // Checks the value and runs the matching case block
                // Checks booking status value

                    case "CONFIRMED":   // Handles the "CONFIRMED" case
                        lbl.setForeground(new Color(0x1B, 0x87, 0x3A));   // Sets the text (foreground) colour of this component
                        // Green color for confirmed bookings
                        break;   // Exits the loop or switch statement immediately

                    case "PENDING":   // Handles the "PENDING" case
                        lbl.setForeground(new Color(0xFF, 0x8F, 0x00));   // Sets the text (foreground) colour of this component
                        // Orange color for pending bookings
                        break;   // Exits the loop or switch statement immediately

                    case "CANCELLED":   // Handles the "CANCELLED" case
                        lbl.setForeground(new Color(0xC6, 0x28, 0x28));   // Sets the text (foreground) colour of this component
                        // Red color for cancelled bookings
                        break;   // Exits the loop or switch statement immediately

                    case "COMPLETED":   // Handles the "COMPLETED" case
                        lbl.setForeground(new Color(0x1A, 0x73, 0xE8));   // Sets the text (foreground) colour of this component
                        // Blue color for completed bookings
                        break;   // Exits the loop or switch statement immediately

                    default:   // Default case — handles any unrecognised status value
                        lbl.setForeground(Color.GRAY);   // Sets the text (foreground) colour of this component
                        // Default color for unknown status
                }   // Closes this code block (end of method, class, or inner class)

                lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));   // Sets the font typeface, style, and size for this component
                // Sets font style for status text

                return lbl;   // Returns the value label reference for later updates
                // Returns final styled label to JTable
            }   // Closes this code block (end of method, class, or inner class)
        }   // Closes this code block (end of method, class, or inner class)
    );   // Closes the chained method call or group definition

    tblBookings.getSelectionModel().addListSelectionListener(e -> {   // Registers a listener that fires when a table row is selected
    // Adds listener that detects when user selects a row in table

        if (!e.getValueIsAdjusting() && tblBookings.getSelectedRow() >= 0) {   // Ensures a table row is actually selected before proceeding
        // Ensures selection is final and valid

            populateFormFromTable();   // Calls populateFormFromTable() to load all booking details into the form
            // Loads selected row data into form fields
        }   // Closes this code block (end of method, class, or inner class)
    });   // Closes the anonymous listener class and the addListener call
} // Closes this code block (end of method, class, or inner class)
    
    private void setupCustomerComboListener() {   // Attaches ActionListener to customer combo to load bookings when customer changes
    // Creates a method that attaches a listener to the customer combo box

    cmbCustomer.addActionListener(new java.awt.event.ActionListener() {   // Registers a listener that fires when this button or field is activated
    // Adds an ActionListener to detect when the selected customer changes

        public void actionPerformed(java.awt.event.ActionEvent evt) {   // Defines the actionPerformed() method
        // Method that runs when user selects a different item in combo box

            cmbCustomerActionPerformed(evt);   // Delegates to cmbCustomerActionPerformed to handle this action event
            // Calls the actual handler method for customer selection

        }   // Closes this code block (end of method, class, or inner class)
        // Ends actionPerformed method

    });   // Closes the anonymous listener class and the addListener call
    // Ends ActionListener and attaches it to cmbCustomer
}   // Closes this code block (end of method, class, or inner class)
// Ends setupCustomerComboListener method

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked") // Tells compiler to suppress unchecked type cast warnings
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() { // NetBeans auto-generated method — creates and configures all UI components

        titlePanel = new javax.swing.JPanel(); // Creates new JPanel component named "titlePanel"
        lblTitle = new javax.swing.JLabel(); // Creates new JLabel component named "lblTitle"
        lblSubtitle = new javax.swing.JLabel(); // Creates new JLabel component named "lblSubtitle"
        formPanel = new javax.swing.JPanel(); // Creates new JPanel component named "formPanel"
        lblBookingId = new javax.swing.JLabel(); // Creates new JLabel component named "lblBookingId"
        txtBookingId = new javax.swing.JTextField(); // Creates new JTextField component named "txtBookingId"
        lblCustomer = new javax.swing.JLabel(); // Creates new JLabel component named "lblCustomer"
        cmbCustomer = new javax.swing.JComboBox<>(); // Creates new JComboBox component named "cmbCustomer"
        lblPackage = new javax.swing.JLabel(); // Creates new JLabel component named "lblPackage"
        cmbPackage = new javax.swing.JComboBox<>(); // Creates new JComboBox component named "cmbPackage"
        lblRoom = new javax.swing.JLabel(); // Creates new JLabel component named "lblRoom"
        cmbRoom = new javax.swing.JComboBox<>(); // Creates new JComboBox component named "cmbRoom"
        lblCheckin = new javax.swing.JLabel(); // Creates new JLabel component named "lblCheckin"
        spnCheckin = new javax.swing.JSpinner( // Creates new JSpinner component named "spnCheckin"
            new javax.swing.SpinnerDateModel(new java.util.Date(), null, null, // Creates a DateModel so this spinner accepts and displays Date values
                java.util.Calendar.DAY_OF_MONTH)); // Closes the SpinnerDateModel constructor — DAY_OF_MONTH is the spinner step unit
        spnCheckin.setEditor(new javax.swing.JSpinner.DateEditor(spnCheckin, "dd/MM/yyyy")); // Sets date editor so spinner displays dates in dd/MM/yyyy format
        lblCheckout = new javax.swing.JLabel(); // Creates new JLabel component named "lblCheckout"
        spnCheckout = new javax.swing.JSpinner( // Creates new JSpinner component named "spnCheckout"
            new javax.swing.SpinnerDateModel(new java.util.Date(), null, null, // Creates a DateModel so this spinner accepts and displays Date values
                java.util.Calendar.DAY_OF_MONTH)); // Closes the SpinnerDateModel constructor — DAY_OF_MONTH is the spinner step unit
        spnCheckout.setEditor(new javax.swing.JSpinner.DateEditor(spnCheckout, "dd/MM/yyyy")); // Sets date editor so spinner displays dates in dd/MM/yyyy format
        btnCalculate = new javax.swing.JButton(); // Creates new JButton component named "btnCalculate"
        lblTotal = new javax.swing.JLabel(); // Creates new JLabel component named "lblTotal"
        txtTotal = new javax.swing.JTextField(); // Creates new JTextField component named "txtTotal"
        lblStatus = new javax.swing.JLabel(); // Creates new JLabel component named "lblStatus"
        cmbStatus = new javax.swing.JComboBox<>(); // Creates new JComboBox component named "cmbStatus"
        btnPanel = new javax.swing.JPanel(); // Creates new JPanel component named "btnPanel"
        btnCreate = new javax.swing.JButton(); // Creates new JButton component named "btnCreate"
        btnUpdateStatus = new javax.swing.JButton(); // Creates new JButton component named "btnUpdateStatus"
        btnCancelBooking = new javax.swing.JButton(); // Creates new JButton component named "btnCancelBooking"
        btnClear = new javax.swing.JButton(); // Creates new JButton component named "btnClear"
        btnDelete = new javax.swing.JButton(); // Creates new JButton component named "btnDelete"
        tablePanel = new javax.swing.JPanel(); // Creates new JPanel component named "tablePanel"
        jScrollPane1 = new javax.swing.JScrollPane(); // Creates new JScrollPane component named "jScrollPane1"
        tblBookings = new javax.swing.JTable(); // Creates new JTable component named "tblBookings"

        titlePanel.setBackground(new java.awt.Color(13, 71, 161)); // Sets the background fill colour of this component

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(255, 255, 255)); // Sets the text (foreground) colour of this component
        lblTitle.setText("Booking Management"); // Sets visible text to: "Booking Management"

        lblSubtitle.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblSubtitle.setForeground(new java.awt.Color(179, 229, 252)); // Sets the text (foreground) colour of this component
        lblSubtitle.setText("Create and manage hotel & tour bookings - Main Transaction Module"); // Sets visible text to: "Create and manage hotel & tour bookings - Main Transaction Module"

        javax.swing.GroupLayout titlePanelLayout = new javax.swing.GroupLayout(titlePanel); // Creates a GroupLayout manager for precise component positioning
        titlePanel.setLayout(titlePanelLayout); // Sets the layout manager controlling how child components are positioned
        titlePanelLayout.setHorizontalGroup( // Applies horizontal layout rules to this panel
            titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING) // Groups components side-by-side (parallel / same row)
            .addGroup(titlePanelLayout.createSequentialGroup() // Nests a sub-group inside this layout group for more complex arrangement
                .addGap(20, 20, 20) // Inserts a 20px gap between components
                .addGroup(titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING) // Groups components side-by-side (parallel / same row)
                    .addComponent(lblTitle) // Places the lblTitle into this layout group
                    .addComponent(lblSubtitle)) // Places the lblSubtitle into this layout group
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)) // Adds standard margins at the edge of the container
        ); // Closes the layout group or method call
        titlePanelLayout.setVerticalGroup( // Applies vertical layout rules to this panel
            titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING) // Groups components side-by-side (parallel / same row)
            .addGroup(titlePanelLayout.createSequentialGroup() // Nests a sub-group inside this layout group for more complex arrangement
                .addGap(12, 12, 12) // Inserts a 12px gap between components
                .addComponent(lblTitle) // Places the lblTitle into this layout group
                .addGap(4, 4, 4) // Inserts a 4px gap between components
                .addComponent(lblSubtitle) // Places the lblSubtitle into this layout group
                .addGap(12, 12, 12)) // Inserts a 12px gap between components
        ); // Closes the layout group or method call

        formPanel.setBackground(new java.awt.Color(255, 255, 255)); // Sets the background fill colour of this component
        formPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Booking Details")); // Applies a visual border or padding around this component

        lblBookingId.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblBookingId.setForeground(new java.awt.Color(95, 107, 124)); // Sets the text (foreground) colour of this component
        lblBookingId.setText("Booking ID"); // Sets visible text to: "Booking ID"

        txtBookingId.setEditable(false); // Makes this text field read-only — user cannot type in it
        txtBookingId.setBackground(new java.awt.Color(240, 244, 248)); // Sets the background fill colour of this component
        txtBookingId.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtBookingId.addActionListener(this::txtBookingIdActionPerformed); // Registers a listener that fires when this button or field is activated

        lblCustomer.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblCustomer.setForeground(new java.awt.Color(95, 107, 124)); // Sets the text (foreground) colour of this component
        lblCustomer.setText("Customer *"); // Sets visible text to: "Customer *"

        cmbCustomer.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cmbCustomer.addActionListener(this::cmbCustomerActionPerformed); // Registers a listener that fires when this button or field is activated

        lblPackage.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblPackage.setForeground(new java.awt.Color(95, 107, 124)); // Sets the text (foreground) colour of this component
        lblPackage.setText("Tour Package (optional)"); // Sets visible text to: "Tour Package (optional)"

        cmbPackage.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cmbPackage.addActionListener(this::cmbPackageActionPerformed); // Registers a listener that fires when this button or field is activated

        lblRoom.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblRoom.setForeground(new java.awt.Color(95, 107, 124)); // Sets the text (foreground) colour of this component
        lblRoom.setText("Room *"); // Sets visible text to: "Room *"

        cmbRoom.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cmbRoom.addActionListener(this::cmbRoomActionPerformed); // Registers a listener that fires when this button or field is activated

        lblCheckin.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblCheckin.setForeground(new java.awt.Color(95, 107, 124)); // Sets the text (foreground) colour of this component
        lblCheckin.setText("Check-In Date *"); // Sets visible text to: "Check-In Date *"

        spnCheckin.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        spnCheckin.addChangeListener(this::spnCheckinStateChanged); // Registers a listener that fires when the spinner value changes

        lblCheckout.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblCheckout.setForeground(new java.awt.Color(95, 107, 124)); // Sets the text (foreground) colour of this component
        lblCheckout.setText("Check-Out Date *"); // Sets visible text to: "Check-Out Date *"

        spnCheckout.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        spnCheckout.addChangeListener(this::spnCheckoutStateChanged); // Registers a listener that fires when the spinner value changes

        btnCalculate.setBackground(new java.awt.Color(0, 150, 136)); // Sets the background fill colour of this component
        btnCalculate.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnCalculate.setForeground(new java.awt.Color(255, 255, 255)); // Sets the text (foreground) colour of this component
        btnCalculate.setText("Calculate Total"); // Sets visible text to: "Calculate Total"
        btnCalculate.setOpaque(true); // Makes this component paint its own background colour
        btnCalculate.addActionListener(this::btnCalculateActionPerformed); // Registers a listener that fires when this button or field is activated

        lblTotal.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblTotal.setForeground(new java.awt.Color(95, 107, 124)); // Sets the text (foreground) colour of this component
        lblTotal.setText("Total Amount (LKR)"); // Sets visible text to: "Total Amount (LKR)"

        txtTotal.setEditable(false); // Makes this text field read-only — user cannot type in it
        txtTotal.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        txtTotal.setForeground(new java.awt.Color(46, 125, 50)); // Sets the text (foreground) colour of this component
        txtTotal.setText("0.00"); // Sets visible text to: "0.00"
        txtTotal.addActionListener(this::txtTotalActionPerformed); // Registers a listener that fires when this button or field is activated

        lblStatus.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblStatus.setForeground(new java.awt.Color(95, 107, 124)); // Sets the text (foreground) colour of this component
        lblStatus.setText("Booking Status"); // Sets visible text to: "Booking Status"

        cmbStatus.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "CONFIRMED", "PENDING", "CANCELLED", "COMPLETED" })); // Connects the data model to this table so rows appear in the UI
        cmbStatus.addActionListener(this::cmbStatusActionPerformed); // Registers a listener that fires when this button or field is activated

        btnPanel.setOpaque(false); // Makes this component transparent so background shows through

        btnCreate.setBackground(new java.awt.Color(26, 115, 232)); // Sets the background fill colour of this component
        btnCreate.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnCreate.setForeground(new java.awt.Color(255, 255, 255)); // Sets the text (foreground) colour of this component
        btnCreate.setText("Create"); // Sets visible text to: "Create"
        btnCreate.setOpaque(true); // Makes this component paint its own background colour
        btnCreate.addActionListener(this::btnCreateActionPerformed); // Registers a listener that fires when this button or field is activated

        btnUpdateStatus.setBackground(new java.awt.Color(0, 150, 136)); // Sets the background fill colour of this component
        btnUpdateStatus.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnUpdateStatus.setForeground(new java.awt.Color(255, 255, 255)); // Sets the text (foreground) colour of this component
        btnUpdateStatus.setText("Update Status"); // Sets visible text to: "Update Status"
        btnUpdateStatus.setOpaque(true); // Makes this component paint its own background colour
        btnUpdateStatus.addActionListener(this::btnUpdateStatusActionPerformed); // Registers a listener that fires when this button or field is activated

        btnCancelBooking.setBackground(new java.awt.Color(255, 0, 255)); // Sets the background fill colour of this component
        btnCancelBooking.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnCancelBooking.setForeground(new java.awt.Color(255, 255, 255)); // Sets the text (foreground) colour of this component
        btnCancelBooking.setText("Cancel Booking"); // Sets visible text to: "Cancel Booking"
        btnCancelBooking.setOpaque(true); // Makes this component paint its own background colour
        btnCancelBooking.addActionListener(this::btnCancelBookingActionPerformed); // Registers a listener that fires when this button or field is activated

        btnClear.setBackground(new java.awt.Color(95, 107, 124)); // Sets the background fill colour of this component
        btnClear.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnClear.setForeground(new java.awt.Color(255, 255, 255)); // Sets the text (foreground) colour of this component
        btnClear.setText("Clear"); // Sets visible text to: "Clear"
        btnClear.setOpaque(true); // Makes this component paint its own background colour
        btnClear.addActionListener(this::btnClearActionPerformed); // Registers a listener that fires when this button or field is activated

        javax.swing.GroupLayout btnPanelLayout = new javax.swing.GroupLayout(btnPanel); // Creates a GroupLayout manager for precise component positioning
        btnPanel.setLayout(btnPanelLayout); // Sets the layout manager controlling how child components are positioned
        btnPanelLayout.setHorizontalGroup( // Applies horizontal layout rules to this panel
            btnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING) // Groups components side-by-side (parallel / same row)
            .addGroup(btnPanelLayout.createSequentialGroup() // Nests a sub-group inside this layout group for more complex arrangement
                .addComponent(btnCreate) // Places the btnCreate into this layout group
                .addGap(6, 6, 6) // Inserts a 6px gap between components
                .addComponent(btnUpdateStatus) // Places the btnUpdateStatus into this layout group
                .addGap(6, 6, 6) // Inserts a 6px gap between components
                .addComponent(btnCancelBooking) // Places the btnCancelBooking into this layout group
                .addGap(6, 6, 6) // Inserts a 6px gap between components
                .addComponent(btnClear) // Places the btnClear into this layout group
                .addContainerGap(18, Short.MAX_VALUE)) // Adds standard container margin gaps at the edges of the panel
        ); // Closes the layout group or method call
        btnPanelLayout.setVerticalGroup( // Applies vertical layout rules to this panel
            btnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE) // Groups components side-by-side (parallel / same row)
            .addComponent(btnCreate) // Places the btnCreate into this layout group
            .addComponent(btnUpdateStatus) // Places the btnUpdateStatus into this layout group
            .addComponent(btnCancelBooking) // Places the btnCancelBooking into this layout group
            .addComponent(btnClear) // Places the btnClear into this layout group
        ); // Closes the layout group or method call

        btnDelete.setBackground(new java.awt.Color(198, 40, 40)); // Sets the background fill colour of this component
        btnDelete.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnDelete.setForeground(new java.awt.Color(255, 255, 255)); // Sets the text (foreground) colour of this component
        btnDelete.setText("Delete"); // Sets visible text to: "Delete"
        btnDelete.setOpaque(true); // Makes this component paint its own background colour
        btnDelete.addActionListener(this::btnDeleteActionPerformed); // Registers a listener that fires when this button or field is activated

        javax.swing.GroupLayout formPanelLayout = new javax.swing.GroupLayout(formPanel); // Creates a GroupLayout manager for precise component positioning
        formPanel.setLayout(formPanelLayout); // Sets the layout manager controlling how child components are positioned
        formPanelLayout.setHorizontalGroup( // Applies horizontal layout rules to this panel
            formPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING) // Groups components side-by-side (parallel / same row)
            .addGroup(formPanelLayout.createSequentialGroup() // Nests a sub-group inside this layout group for more complex arrangement
                .addGap(10, 10, 10) // Inserts a 10px gap between components
                .addGroup(formPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING) // Groups components side-by-side (parallel / same row)
                    .addGroup(formPanelLayout.createSequentialGroup() // Nests a sub-group inside this layout group for more complex arrangement
                        .addComponent(btnDelete) // Places the btnDelete into this layout group
                        .addGap(0, 0, Short.MAX_VALUE)) // Inserts a 0px gap between components
                    .addGroup(formPanelLayout.createSequentialGroup() // Nests a sub-group inside this layout group for more complex arrangement
                        .addComponent(cmbCustomer, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE) // Adds this component into the layout group at this position
                        .addContainerGap()) // Adds standard container margin gaps at the edges of the panel
                    .addGroup(formPanelLayout.createSequentialGroup() // Nests a sub-group inside this layout group for more complex arrangement
                        .addGroup(formPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING) // Groups components side-by-side (parallel / same row)
                            .addComponent(lblBookingId) // Places the lblBookingId into this layout group
                            .addComponent(txtBookingId) // Places the txtBookingId into this layout group
                            .addComponent(lblCustomer) // Places the lblCustomer into this layout group
                            .addComponent(lblPackage) // Places the lblPackage into this layout group
                            .addComponent(cmbPackage, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE) // Adds this component into the layout group at this position
                            .addComponent(lblRoom) // Places the lblRoom into this layout group
                            .addComponent(cmbRoom, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE) // Adds this component into the layout group at this position
                            .addComponent(lblCheckin) // Places the lblCheckin into this layout group
                            .addComponent(spnCheckin) // Places the spnCheckin into this layout group
                            .addComponent(lblCheckout) // Places the lblCheckout into this layout group
                            .addComponent(spnCheckout) // Places the spnCheckout into this layout group
                            .addComponent(btnCalculate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE) // Adds this component into the layout group at this position
                            .addComponent(lblTotal) // Places the lblTotal into this layout group
                            .addComponent(txtTotal) // Places the txtTotal into this layout group
                            .addComponent(lblStatus) // Places the lblStatus into this layout group
                            .addComponent(cmbStatus, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE) // Adds this component into the layout group at this position
                            .addComponent(btnPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)) // Adds this component into the layout group at this position
                        .addGap(10, 10, 10)))) // Inserts a 10px gap between components
        ); // Closes the layout group or method call
        formPanelLayout.setVerticalGroup( // Applies vertical layout rules to this panel
            formPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING) // Groups components side-by-side (parallel / same row)
            .addGroup(formPanelLayout.createSequentialGroup() // Nests a sub-group inside this layout group for more complex arrangement
                .addGap(8, 8, 8) // Inserts a 8px gap between components
                .addComponent(lblBookingId) // Places the lblBookingId into this layout group
                .addGap(3, 3, 3) // Inserts a 3px gap between components
                .addComponent(txtBookingId, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE) // Adds this component into the layout group at this position
                .addGap(6, 6, 6) // Inserts a 6px gap between components
                .addComponent(lblCustomer) // Places the lblCustomer into this layout group
                .addGap(3, 3, 3) // Inserts a 3px gap between components
                .addComponent(cmbCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE) // Adds this component into the layout group at this position
                .addGap(6, 6, 6) // Inserts a 6px gap between components
                .addComponent(lblPackage) // Places the lblPackage into this layout group
                .addGap(3, 3, 3) // Inserts a 3px gap between components
                .addComponent(cmbPackage, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE) // Adds this component into the layout group at this position
                .addGap(6, 6, 6) // Inserts a 6px gap between components
                .addComponent(lblRoom) // Places the lblRoom into this layout group
                .addGap(3, 3, 3) // Inserts a 3px gap between components
                .addComponent(cmbRoom, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE) // Adds this component into the layout group at this position
                .addGap(6, 6, 6) // Inserts a 6px gap between components
                .addComponent(lblCheckin) // Places the lblCheckin into this layout group
                .addGap(3, 3, 3) // Inserts a 3px gap between components
                .addComponent(spnCheckin, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE) // Adds this component into the layout group at this position
                .addGap(6, 6, 6) // Inserts a 6px gap between components
                .addComponent(lblCheckout) // Places the lblCheckout into this layout group
                .addGap(3, 3, 3) // Inserts a 3px gap between components
                .addComponent(spnCheckout, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE) // Adds this component into the layout group at this position
                .addGap(6, 6, 6) // Inserts a 6px gap between components
                .addComponent(btnCalculate, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE) // Adds this component into the layout group at this position
                .addGap(6, 6, 6) // Inserts a 6px gap between components
                .addComponent(lblTotal) // Places the lblTotal into this layout group
                .addGap(3, 3, 3) // Inserts a 3px gap between components
                .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE) // Adds this component into the layout group at this position
                .addGap(6, 6, 6) // Inserts a 6px gap between components
                .addComponent(lblStatus) // Places the lblStatus into this layout group
                .addGap(3, 3, 3) // Inserts a 3px gap between components
                .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE) // Adds this component into the layout group at this position
                .addGap(10, 10, 10) // Inserts a 10px gap between components
                .addComponent(btnPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE) // Adds this component into the layout group at this position
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED) // Adds a standard gap between related components (follows platform guidelines)
                .addComponent(btnDelete) // Places the btnDelete into this layout group
                .addContainerGap(13, Short.MAX_VALUE)) // Adds standard container margin gaps at the edges of the panel
        ); // Closes the layout group or method call

        tablePanel.setBackground(new java.awt.Color(255, 255, 255)); // Sets the background fill colour of this component
        tablePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("All Bookings")); // Applies a visual border or padding around this component

        tblBookings.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        tblBookings.setModel(new javax.swing.table.DefaultTableModel( // Connects the data model to this table so rows appear in the UI
            new Object [][] { // Starts the initial empty data array for the table model (no rows yet)

            }, // Closes the data array and separates it from the column names array
            new String [] { // Starts the array of column header names for the table
                "ID", "Customer", "Room", "Package", "Check-In", "Check-Out", "Amount (LKR)", "Status" // Column headers for the recent bookings table on the dashboard
            } // Closes this code block (end of method, class, or inner class)
        ) { // Opens the anonymous DefaultTableModel subclass body
            boolean[] canEdit = new boolean [] { // Boolean array defining which columns can be edited (all false = read-only)
                false, false, false, false, false, false, false, false // All columns set to false — no table cell is editable by the user
            }; // Closes this anonymous class or array definition

            public boolean isCellEditable(int rowIndex, int columnIndex) { // Overrides parent — always returns false so table cells cannot be edited
                return canEdit [columnIndex]; // Returns the value: canEdit [columnIndex]
            } // Closes this code block (end of method, class, or inner class)
        }); // Closes the anonymous listener class and the addListener call
        tblBookings.setRowHeight(28); // Sets the height of each table row in pixels
        tblBookings.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION); // Restricts the table so only one row can be selected at a time
        tblBookings.setShowGrid(false); // Hides grid lines between cells for a cleaner modern table look
        tblBookings.addMouseListener(new java.awt.event.MouseAdapter() { // Registers a listener that fires on mouse click events
            public void mouseClicked(java.awt.event.MouseEvent evt) { // Defines the mouseClicked() method
                tblBookingsMouseClicked(evt); // Delegates to tblBookingsMouseClicked to handle the mouse click event
            } // Closes this code block (end of method, class, or inner class)
        }); // Closes the anonymous listener class and the addListener call
        jScrollPane1.setViewportView(tblBookings); // Places the component inside the scroll pane so it scrolls correctly

        javax.swing.GroupLayout tablePanelLayout = new javax.swing.GroupLayout(tablePanel); // Creates a GroupLayout manager for precise component positioning
        tablePanel.setLayout(tablePanelLayout); // Sets the layout manager controlling how child components are positioned
        tablePanelLayout.setHorizontalGroup( // Applies horizontal layout rules to this panel
            tablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING) // Groups components side-by-side (parallel / same row)
            .addGroup(tablePanelLayout.createSequentialGroup() // Nests a sub-group inside this layout group for more complex arrangement
                .addGap(8, 8, 8) // Inserts a 8px gap between components
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 702, Short.MAX_VALUE) // Adds this component into the layout group at this position
                .addGap(8, 8, 8)) // Inserts a 8px gap between components
        ); // Closes the layout group or method call
        tablePanelLayout.setVerticalGroup( // Applies vertical layout rules to this panel
            tablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING) // Groups components side-by-side (parallel / same row)
            .addGroup(tablePanelLayout.createSequentialGroup() // Nests a sub-group inside this layout group for more complex arrangement
                .addGap(8, 8, 8) // Inserts a 8px gap between components
                .addComponent(jScrollPane1) // Places the jScrollPane1 into this layout group
                .addGap(8, 8, 8)) // Inserts a 8px gap between components
        ); // Closes the layout group or method call

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this); // Creates a GroupLayout manager for precise component positioning
        this.setLayout(layout); // Sets the layout manager controlling how child components are positioned
        layout.setHorizontalGroup( // Defines horizontal component arrangement for the outer frame layout
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING) // Groups components side-by-side (parallel / same row)
            .addGroup(layout.createSequentialGroup() // Nests a sub-group inside this layout group for more complex arrangement
                .addContainerGap() // Adds standard container margin gaps at the edges of the panel
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING) // Groups components side-by-side (parallel / same row)
                    .addComponent(titlePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE) // Adds this component into the layout group at this position
                    .addGroup(layout.createSequentialGroup() // Nests a sub-group inside this layout group for more complex arrangement
                        .addGap(10, 10, 10) // Inserts a 10px gap between components
                        .addComponent(formPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE) // Adds this component into the layout group at this position
                        .addGap(18, 18, 18) // Inserts a 18px gap between components
                        .addComponent(tablePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE) // Adds this component into the layout group at this position
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 5, Short.MAX_VALUE))) // Sets maximum size to allow the component to stretch and fill available space
                .addContainerGap()) // Adds standard container margin gaps at the edges of the panel
        ); // Closes the layout group or method call
        layout.setVerticalGroup( // Defines vertical component stacking for the outer frame layout
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING) // Groups components side-by-side (parallel / same row)
            .addGroup(layout.createSequentialGroup() // Nests a sub-group inside this layout group for more complex arrangement
                .addComponent(titlePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE) // Adds this component into the layout group at this position
                .addGap(6, 6, 6) // Inserts a 6px gap between components
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING) // Groups components side-by-side (parallel / same row)
                    .addComponent(formPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE) // Adds this component into the layout group at this position
                    .addComponent(tablePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)) // Adds this component into the layout group at this position
                .addGap(10, 10, 10)) // Inserts a 10px gap between components
        ); // Closes the layout group or method call
    }// </editor-fold>//GEN-END:initComponents

    private void txtBookingIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBookingIdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBookingIdActionPerformed

    private void cmbCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbCustomerActionPerformed
        // TODO add your handling code here:
        // Skip if we are programmatically loading values from a table row click
        if (loadingFromTable) return; // Skips this listener when updating combo programmatically

        int idx = cmbCustomer.getSelectedIndex(); // Get which customer was selected
        if (idx < 0 || customers == null || idx >= customers.size()) return; // Checks for null to prevent NullPointerException

        int customerId = customers.get(idx).getCustomerId(); // Get their database ID

        // Search all bookings to find the most recent one for this customer
        try { // Attempts the following database operations — errors caught below
            for (Booking b : bookingCtrl.getAllBookings()) { // Loops through each booking record from the database
                if (b.getCustomerId() == customerId) { // Checks condition and executes block if true
                    loadBookingIntoForm(b.getBookingId()); // Load the booking details
                    return; // Exits the method immediately without executing further code
                } // Closes this code block (end of method, class, or inner class)
            } // Closes this code block (end of method, class, or inner class)
            clearFormExceptCustomer(); // No booking found - clear other fields
        } catch (SQLException ex) { // Catches database errors and shows an error message to the user
            showError("Could not load customer bookings: " + ex.getMessage()); // Shows an error dialog popup with the failure message
        } // Closes this code block (end of method, class, or inner class)
    
    }//GEN-LAST:event_cmbCustomerActionPerformed

    private void cmbPackageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbPackageActionPerformed
        // TODO add your handling code here:
        // Package selection triggers automatic total recalculation
        calculateTotal(); // Recalculates and updates the total booking amount field
    }//GEN-LAST:event_cmbPackageActionPerformed

    private void cmbRoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbRoomActionPerformed
        // TODO add your handling code here:
        // Room selection triggers automatic total recalculation
        calculateTotal(); // Recalculates and updates the total booking amount field
    }//GEN-LAST:event_cmbRoomActionPerformed

    private void spnCheckinStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnCheckinStateChanged
        // TODO add your handling code here:
        // Date change triggers automatic total recalculation
        calculateTotal(); // Recalculates and updates the total booking amount field
    }//GEN-LAST:event_spnCheckinStateChanged

    private void spnCheckoutStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnCheckoutStateChanged
        // TODO add your handling code here:
        // Date change triggers automatic total recalculation
        calculateTotal(); // Recalculates and updates the total booking amount field
    }//GEN-LAST:event_spnCheckoutStateChanged

    private void btnCalculateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalculateActionPerformed
        // TODO add your handling code here:
        // Manually trigger total recalculation
        calculateTotal(); // Recalculates and updates the total booking amount field
    }//GEN-LAST:event_btnCalculateActionPerformed

    private void txtTotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTotalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTotalActionPerformed

    private void cmbStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbStatusActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbStatusActionPerformed

    private void btnCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateActionPerformed
        // TODO add your handling code here:
        try { // Attempts the following database operations — errors caught below
            // Get the selected IDs from the combo-boxes
            int customerId    = getSelectedCustomerId(); // Declares int variable "customerId" with initial value
            Integer packageId = getSelectedPackageId();   // null = no package
            int roomId        = getSelectedRoomId(); // Declares int variable "roomId" with initial value

            // Both customer and room are mandatory
            if (customerId < 0 || roomId < 0) { // Checks condition and executes block if true
                showError("Please select a customer and a room."); // Shows an error dialog popup with the failure message
                return; // Exits the method immediately without executing further code
            } // Closes this code block (end of method, class, or inner class)

            Date checkin  = (Date) ((javax.swing.SpinnerDateModel) spnCheckin.getModel()).getDate(); // Declares Date model variable "checkin"
            Date checkout = (Date) ((javax.swing.SpinnerDateModel) spnCheckout.getModel()).getDate(); // Declares Date model variable "checkout"

            // Parse total – remove commas that DecimalFormat may have added
            double total = Double.parseDouble(txtTotal.getText().replace(",", "")); // Converts the text string to a decimal number

            // Delegate to controller (validates dates, checks availability, saves)
            bookingCtrl.createBooking(customerId, packageId, roomId, checkin, checkout, total); // Creates a new booking record in the database with all the form values
            showSuccess("Booking created successfully!"); // Shows a success dialog popup with the result message

            // Refresh everything so the new booking appears in the table
            clearForm(); // Resets all form fields back to their empty default state
            loadCombos(); // Reloads all combo-boxes with the latest data from the database
            loadTable(); // Reloads the table with fresh data from the database

        } catch (InvalidBookingException ex) { showError(ex.getMessage()); } // Catches validation failures from the controller layer
          catch (NumberFormatException ex)    { showError("Invalid total amount."); } // Catches non-numeric total amount text and shows an error message
          catch (SQLException ex)             { showError("Database error: " + ex.getMessage()); } // Catches database errors and shows them as an error dialog to the user
    
    }//GEN-LAST:event_btnCreateActionPerformed

    private void btnUpdateStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateStatusActionPerformed
        // TODO add your handling code here:
        if (selectedBookingId < 0) { // Checks a valid record is selected before attempting an operation
            showError("Please select a booking from the table first."); // Shows an error dialog popup with the failure message
            return; // Exits the method immediately without executing further code
        } // Closes this code block (end of method, class, or inner class)
        try { // Attempts the following database operations — errors caught below
            Booking.Status newStatus = // Starts reading the selected status string from the dropdown combo-box
                    Booking.Status.valueOf((String) cmbStatus.getSelectedItem()); // Gets the currently selected item from the dropdown as an Object
            bookingCtrl.updateStatus(selectedBookingId, newStatus); // Updates the booking status to the newly selected value
            showSuccess("Booking status updated successfully."); // Shows a success dialog popup with the result message
            loadTable(); // Reloads the table with fresh data from the database
        } catch (SQLException ex) { showError(ex.getMessage()); } // Catches database errors and shows an error message to the user
    
    }//GEN-LAST:event_btnUpdateStatusActionPerformed

    private void btnCancelBookingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelBookingActionPerformed
        // TODO add your handling code here:
        if (selectedBookingId < 0) { // Checks a valid record is selected before attempting an operation
            showError("Please select a booking from the table first."); // Shows an error dialog popup with the failure message
            return; // Exits the method immediately without executing further code
        } // Closes this code block (end of method, class, or inner class)
        int answer = JOptionPane.showConfirmDialog(this, // Shows a Yes/No dialog and captures the user response
                "Cancel Booking BK-" + selectedBookingId + "?\n" + // First line of the cancellation confirmation message showing the booking ID
                "The room will be released and made available again.", // Second line of the cancellation message explaining the room release effect
                "Confirm Cancellation", JOptionPane.YES_NO_OPTION); // Shows a Yes/No confirmation dialog for the booking cancellation
        if (answer != JOptionPane.YES_OPTION) return; // Proceeds only if user clicked Yes in the confirmation dialog

        try { // Attempts the following database operations — errors caught below
            bookingCtrl.cancelBooking(selectedBookingId); // Cancels this booking and releases the room back to AVAILABLE status
            showSuccess("Booking cancelled. Room is now available."); // Shows a success dialog popup with the result message
            clearForm(); // Resets all form fields back to their empty default state
            loadCombos();  // refresh rooms (one just became available)
            loadTable(); // Reloads the table with fresh data from the database
        } catch (SQLException ex) { showError(ex.getMessage()); } // Catches database errors and shows an error message to the user
    
    }//GEN-LAST:event_btnCancelBookingActionPerformed

    // ── Core logic ────────────────────────────────────────────────────────────

    /**
     * Calculates the booking total and updates the Total Amount field.
     *
     * Formula: total = (number of nights × room price per night) + package price.
     *
     * Called automatically whenever the room, check-in, or check-out changes,
     * and also when the "Calculate Total" button is clicked.
     */
    private void calculateTotal() { // Computes booking cost = (nights x room price) + package price and shows in txtTotal
        try { // Attempts the following database operations — errors caught below
            int roomId = getSelectedRoomId(); // Declares int variable "roomId" with initial value
            if (roomId < 0) return; // no room selected yet — nothing to calculate

            Integer packageId = getSelectedPackageId(); // Gets the selected package ID (null if "No Package" is selected)

            // SpinnerDateModel.getValue() returns a Date — cast safely
            Date checkin  = (Date) ((javax.swing.SpinnerDateModel) spnCheckin.getModel()).getDate(); // Declares Date model variable "checkin"
            Date checkout = (Date) ((javax.swing.SpinnerDateModel) spnCheckout.getModel()).getDate(); // Declares Date model variable "checkout"

            double total = bookingCtrl.calculateTotal(roomId, packageId, checkin, checkout); // Stores the calculated total booking amount
            txtTotal.setText(new DecimalFormat("#,###.00").format(total)); // Sets the display text of this component
        } catch (ClassCastException ex) { // Catches type mismatch — spinner model not yet set to DateModel
            // Spinner model not yet a DateModel — skip silently during init
        } catch (SQLException ex) { // Catches database errors and shows an error message to the user
            // Silently ignore — total shows 0.00 until valid inputs are chosen
        } // Closes this code block (end of method, class, or inner class)
    } // Closes this code block (end of method, class, or inner class)

    // ── Data loading ──────────────────────────────────────────────────────────

    /**
     * Issue 1 Fix: Reloads all three combo-boxes from the database.
     *
     * Called on panel refresh (when user navigates to Bookings) so that
     * any new customers, packages, or rooms added in other panels appear
     * here immediately — without restarting the application.
     */
    private void loadCombos() { // Reloads all three combo-boxes from the database (customers, packages, rooms)
        try { // Attempts the following database operations — errors caught below
            // ── Customers ──────────────────────────────────────────────────────
            customers = customerCtrl.getAllCustomers(); // Fetches all records from the database for this entity type
            loadingFromTable = true; // Prevent listener firing while populating
            cmbCustomer.removeAllItems(); // Clears all existing items from the combo-box before reloading
            for (Customer c : customers) { // Loops through each customer record from the database
                cmbCustomer.addItem(c.getFullName() + " [" + c.getCustomerId() + "]"); // Adds one item to the dropdown combo-box list
            } // Closes this code block (end of method, class, or inner class)
            loadingFromTable = false; // Re-enable listener after loading

            // ── Tour Packages (active only) ────────────────────────────────────
            packages = packageCtrl.getActivePackages(); // Fetches only ACTIVE packages suitable for new bookings
            cmbPackage.removeAllItems(); // Clears all existing items from the combo-box before reloading
            cmbPackage.addItem("-- No Package --"); // always first item at index 0
            for (model.Package p : packages) { // Loops through each tour package record from the database
                cmbPackage.addItem(p.getPackageName() + " [" + p.getPackageId() + "]"); // Adds one item to the dropdown combo-box list
            } // Closes this code block (end of method, class, or inner class)

            // ── Rooms ──────────────────────────────────────────────────────────
            rooms = roomCtrl.getAllRooms(); // Fetches all records from the database for this entity type
            cmbRoom.removeAllItems(); // Clears all existing items from the combo-box before reloading
            for (Room r : rooms) { // Loops through each room record from the database
                cmbRoom.addItem("Rm " + r.getRoomNumber() + " - " + r.getRoomType() // Adds one item to the dropdown combo-box list
                        + " [" + r.getRoomId() + "]"); // Gets the room ID associated with this booking
            } // Closes this code block (end of method, class, or inner class)
        } catch (SQLException ex) { // Catches database errors and shows an error message to the user
            showError("Failed to load form data: " + ex.getMessage()); // Shows an error dialog popup with the failure message
        } // Closes this code block (end of method, class, or inner class)
    } // Closes this code block (end of method, class, or inner class)

    /**
     * Reloads the bookings table from the database.
     * Shows all bookings with customer name, room, package, dates, amount, status.
     */
    private void loadTable() { // Fetches all records from database and populates the table rows
        try { // Attempts the following database operations — errors caught below
            tableModel.setRowCount(0); // clear existing rows first
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); // Creates a date formatter to convert Date objects to "dd/MM/yyyy" strings
            DecimalFormat df = new DecimalFormat("#,###.00"); // Creates a number formatter using the given pattern (e.g. commas for thousands)

            for (Booking b : bookingCtrl.getAllBookings()) { // Loops through each booking record from the database
                tableModel.addRow(new Object[]{ // Adds one data record as a new visible row in the table
                    "BK-" + b.getBookingId(), // Gets the booking ID linked to this payment
                    b.getCustomerName(), // Gets the customer full name from the booking JOIN result
                    "Rm " + b.getRoomNumber(), // Gets the room number string from the booking JOIN result
                    b.getPackageName() == null ? "-" : b.getPackageName(), // Gets the package name from the booking JOIN result (may be null)
                    b.getCheckinDate()  != null ? sdf.format(b.getCheckinDate())  : "", // Converts the Date to a readable "dd/MM/yyyy" string for table display
                    b.getCheckoutDate() != null ? sdf.format(b.getCheckoutDate()) : "", // Converts the Date to a readable "dd/MM/yyyy" string for table display
                    df.format(b.getTotalAmount()), // Formats the number with commas and decimal places for display
                    b.getStatus().name() // Gets the enum value as a plain String (e.g. CONFIRMED, CASH, ACTIVE)
                }); // Closes the anonymous listener class and the addListener call
            } // Closes this code block (end of method, class, or inner class)
        } catch (SQLException ex) { showError("Failed to load bookings."); } // Catches database errors and shows an error message to the user
    } // Closes this code block (end of method, class, or inner class)

    /**
     * Called when the user clicks a row in the bookings table.
     *
     * Reads the booking ID from the selected row, fetches the full Booking
     * object from the database, then populates ALL form fields:
     * Customer, Package, Room, Check-In, Check-Out, Total, Status.
     *
     * The loadingFromTable flag is set to true during this process so the
     * Customer combo-box listener does NOT trigger and overwrite our values.
     */
    private void populateFormFromTable() { // Fetches full booking from DB and loads all details into every form field
    int row = tblBookings.getSelectedRow(); // Gets the index of the selected table row (-1 means no row is selected)
    if (row < 0) return; // Checks condition and executes block if true

    String raw = (String) tableModel.getValueAt(row, 0); // Reads the value from column 0 of the selected table row
    selectedBookingId = Integer.parseInt(raw.replace("BK-", "")); // Converts the text string to an integer number for use as an ID
    txtBookingId.setText(raw); // Sets the display text of this component

    try { // Attempts the following database operations — errors caught below
        Booking booking = bookingCtrl.getBookingById(selectedBookingId); // Declares Booking model variable "booking"
        if (booking == null) return; // Checks for null to prevent NullPointerException

        loadingFromTable = true; // Sets flag to true to prevent the customer combo listener from triggering

        selectCustomerInCombo(booking.getCustomerId()); // Gets the customer ID for this entity
        selectPackageInCombo(booking.getPackageId()); // Gets the package ID (may be null if no package)
        selectRoomInCombo(booking.getRoomId()); // Gets the room ID associated with this booking

        if (booking.getCheckinDate() != null) // Checks for null to prevent NullPointerException
            spnCheckin.setValue(booking.getCheckinDate()); // Updates the date spinner to display this date

        if (booking.getCheckoutDate() != null) // Checks for null to prevent NullPointerException
            spnCheckout.setValue(booking.getCheckoutDate()); // Updates the date spinner to display this date

        txtTotal.setText(new DecimalFormat("#,###.00") // Sets the display text of this component
                .format(booking.getTotalAmount())); // Gets the pre-calculated total cost from the booking record

        cmbStatus.setSelectedItem(booking.getStatus().name()); // Sets the dropdown to show a specific item by its string value

        loadingFromTable = false; // Resets flag to false so the customer combo listener works normally again

    } catch (SQLException ex) { // Catches database errors and shows an error message to the user
        loadingFromTable = false; // Resets flag to false so the customer combo listener works normally again
        showError("Could not load booking details: " + ex.getMessage()); // Shows an error dialog popup with the failure message
    } // Closes this code block (end of method, class, or inner class)
} // Closes this code block (end of method, class, or inner class)

    /**
     * Loads a booking by ID and populates the form.
     * Used by the customer combo-box listener (Issue 2 fix) to load
     * the most recent booking for the selected customer.
     *
     * @param bookingId the booking_id to load
     */
    private void loadBookingIntoForm(int bookingId) { // Fetches booking by ID from DB and populates all form fields
        selectedBookingId = bookingId; // Saves the booking ID being loaded so it can be referenced by update/delete actions
        txtBookingId.setText("BK-" + bookingId); // Sets the display text of this component
        try { // Attempts the following database operations — errors caught below
            Booking booking = bookingCtrl.getBookingById(bookingId); // Declares Booking model variable "booking"
            if (booking == null) return; // Checks for null to prevent NullPointerException

            // Use the flag to prevent the combo listener from firing again
            loadingFromTable = true; // Sets flag to true to prevent the customer combo listener from triggering

            selectPackageInCombo(booking.getPackageId()); // Gets the package ID (may be null if no package)
            selectRoomInCombo(booking.getRoomId()); // Gets the room ID associated with this booking
            if (booking.getCheckinDate()  != null) spnCheckin.setValue(booking.getCheckinDate()); // Updates the date spinner to display this date
            if (booking.getCheckoutDate() != null) spnCheckout.setValue(booking.getCheckoutDate()); // Updates the date spinner to display this date
            txtTotal.setText(new DecimalFormat("#,###.00").format(booking.getTotalAmount())); // Sets the display text of this component
            cmbStatus.setSelectedItem(booking.getStatus().name()); // Sets the dropdown to show a specific item by its string value

            loadingFromTable = false; // Resets flag to false so the customer combo listener works normally again
        } catch (SQLException ex) { // Catches database errors and shows an error message to the user
            loadingFromTable = false; // Resets flag to false so the customer combo listener works normally again
            showError("Could not load booking: " + ex.getMessage()); // Shows an error dialog popup with the failure message
        } // Closes this code block (end of method, class, or inner class)
    } // Closes this code block (end of method, class, or inner class)

    // ── Combo-box selection helpers ───────────────────────────────────────────

    /**
     * Scans the customers list and selects the one with the given ID.
     *
     * @param customerId the customer_id to select
     */
    private void selectCustomerInCombo(int customerId) { // Searches the customers list and sets the matching index in the combo-box
        if (customers == null) return; // Checks for null to prevent NullPointerException
        for (int i = 0; i < customers.size(); i++) { // Loops through each item in the collection
            if (customers.get(i).getCustomerId() == customerId) { // Checks condition and executes block if true
                cmbCustomer.setSelectedIndex(i); // Sets the dropdown to a specific position by its index number
                return; // Exits the method immediately without executing further code
            } // Closes this code block (end of method, class, or inner class)
        } // Closes this code block (end of method, class, or inner class)
    } // Closes this code block (end of method, class, or inner class)

    /**
     * Selects the matching package in the combo-box.
     * Index 0 is always "-- No Package --", real packages start at index 1.
     *
     * Issue 4 Fix: compares packageId using == (int comparison) instead of
     * .equals() to avoid "int cannot be dereferenced" compile error.
     *
     * @param packageId the package_id to select, or null for "No Package"
     */
    private void selectPackageInCombo(Integer packageId) { // Searches the packages list and sets the matching index in the combo-box
        if (packages == null) return; // Checks for null to prevent NullPointerException
        if (packageId == null) { // Checks for null to prevent NullPointerException
            cmbPackage.setSelectedIndex(0); // "-- No Package --"
            return; // Exits the method immediately without executing further code
        } // Closes this code block (end of method, class, or inner class)
        for (int i = 0; i < packages.size(); i++) { // Loops through each item in the collection
            // Fix 4: use int comparison (==) not .equals() on a primitive int
            if (packages.get(i).getPackageId() == packageId) { // Checks condition and executes block if true
                cmbPackage.setSelectedIndex(i + 1); // +1 because index 0 = "No Package"
                return; // Exits the method immediately without executing further code
            } // Closes this code block (end of method, class, or inner class)
        } // Closes this code block (end of method, class, or inner class)
        cmbPackage.setSelectedIndex(0); // package not found – default to "No Package"
    } // Closes this code block (end of method, class, or inner class)

    /**
     * Scans the rooms list and selects the one with the given ID.
     *
     * @param roomId the room_id to select
     */
    private void selectRoomInCombo(int roomId) { // Searches the rooms list and sets the matching index in the combo-box
        if (rooms == null) return; // Checks for null to prevent NullPointerException
        for (int i = 0; i < rooms.size(); i++) { // Loops through each item in the collection
            if (rooms.get(i).getRoomId() == roomId) { // Checks condition and executes block if true
                cmbRoom.setSelectedIndex(i); // Sets the dropdown to a specific position by its index number
                return; // Exits the method immediately without executing further code
            } // Closes this code block (end of method, class, or inner class)
        } // Closes this code block (end of method, class, or inner class)
    } // Closes this code block (end of method, class, or inner class)

    // ── ID extractors ─────────────────────────────────────────────────────────

    /**
     * Returns the customer_id of the currently selected customer combo item,
     * or -1 if nothing is selected.
     */
    private int getSelectedCustomerId() { // Returns the customer_id for the currently selected combo-box entry
        int idx = cmbCustomer.getSelectedIndex(); // Gets the index (position number) of the selected dropdown item
        if (idx < 0 || customers == null || idx >= customers.size()) return -1; // Checks for null to prevent NullPointerException
        return customers.get(idx).getCustomerId(); // Returns the value: customers.get(idx).getCustomerId()
    } // Closes this code block (end of method, class, or inner class)

    /**
     * Returns the package_id of the selected package, or null if
     * "-- No Package --" (index 0) is selected.
     */
    private Integer getSelectedPackageId() { // Returns the package_id for the selected combo entry (null if No Package)
        int idx = cmbPackage.getSelectedIndex(); // Gets the index (position number) of the selected dropdown item
        // Index 0 = "-- No Package --"; packages start at index 1
        if (idx <= 0 || packages == null || (idx - 1) >= packages.size()) return null; // Checks for null to prevent NullPointerException
        return packages.get(idx - 1).getPackageId(); // Returns the value: packages.get(idx - 1).getPackageId()
    } // Closes this code block (end of method, class, or inner class)

    /**
     * Returns the room_id of the currently selected room, or -1 if none.
     */
    private int getSelectedRoomId() { // Returns the room_id for the currently selected room combo entry
        int idx = cmbRoom.getSelectedIndex(); // Gets the index (position number) of the selected dropdown item
        if (idx < 0 || rooms == null || idx >= rooms.size()) return -1; // Checks for null to prevent NullPointerException
        return rooms.get(idx).getRoomId(); // Returns the value: rooms.get(idx).getRoomId()
    } // Closes this code block (end of method, class, or inner class)

    // ── Form helpers ──────────────────────────────────────────────────────────

    /**
     * Resets all form fields to their default empty state.
     * Also clears the table selection so no row remains highlighted.
     */
    private void clearForm() { // Resets every form field to blank and deselects any highlighted table row
    selectedBookingId = -1; // Resets selected booking ID to -1 to indicate nothing is selected
    txtBookingId.setText(""); // Sets visible text to: ""
    txtTotal.setText("0.00"); // Sets visible text to: "0.00"
    cmbStatus.setSelectedIndex(0); // Resets the dropdown back to its first item
    tblBookings.clearSelection(); // Removes any row highlight/selection from the table
    } // Closes this code block (end of method, class, or inner class)

    /**
     * Clears all fields EXCEPT the customer combo-box.
     * Used when the customer combo changes but no booking was found for them –
     * we want to keep the customer selected but reset the other fields.
     */
    private void clearFormExceptCustomer() { // Clears all fields except the customer combo — used when switching customers
        selectedBookingId = -1; // Resets selected booking ID to -1 to indicate nothing is selected
        txtBookingId.setText(""); // Sets visible text to: ""
        txtTotal.setText("0.00"); // Sets visible text to: "0.00"
        cmbPackage.setSelectedIndex(0); // Resets the dropdown back to its first item
        if (rooms != null && !rooms.isEmpty()) cmbRoom.setSelectedIndex(0); // Resets the dropdown back to its first item
        cmbStatus.setSelectedIndex(0); // Resets the dropdown back to its first item
        tblBookings.clearSelection(); // Removes any row highlight/selection from the table
    } // Closes this code block (end of method, class, or inner class)

    /**
     * Called by MainFrame whenever the user navigates to the Bookings panel.
     * Reloads combo-boxes (picks up new customers/packages/rooms) and table.
     */
    @Override // Signals this method overrides a method from the parent class or interface
    public void refresh() { // Called by MainFrame when user navigates here — reloads all data from database
        loadCombos(); // Issue 1 Fix: always reload combos on panel switch
        loadTable(); // Reloads the table with fresh data from the database
    } // Closes this code block (end of method, class, or inner class)

    // ── Shared UI styling helpers ─────────────────────────────────────────────

    /** Applies standard grey label styling. */
    private void styleLabel(JLabel l, String text) { // Applies standard small grey font styling to a label component
        l.setFont(new Font("Segoe UI", Font.PLAIN, 11)); // Sets the font typeface, style, and size for this component
        l.setForeground(new Color(95, 107, 124)); // Sets the text (foreground) colour of this component
        l.setText(text); // Sets the display text of this component
    } // Closes this code block (end of method, class, or inner class)

    /** Applies standard coloured button styling. */
    private void styleBtn(JButton b, String text, Color bg) { // Applies standard coloured bold font and white text styling to a button
        b.setFont(new Font("Segoe UI", Font.BOLD, 12)); // Sets the font typeface, style, and size for this component
        b.setText(text); // Sets the display text of this component
        b.setBackground(bg); // Sets the background fill colour of this component
        b.setForeground(Color.WHITE); // Sets the text (foreground) colour of this component
        b.setOpaque(true); // Makes this component paint its own background colour
    } // Closes this code block (end of method, class, or inner class)

    /** Shows a green success dialog. */
    private void showSuccess(String msg) { // Displays a blue information dialog with a success message
        JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE); // Shows a pop-up dialog box with the specified message and title
    } // Closes this code block (end of method, class, or inner class)

    /** Shows a red error dialog. */
    private void showError(String msg) { // Displays a red error dialog with the given error description
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE); // Shows a pop-up dialog box with the specified message and title
    } // Closes this code block (end of method, class, or inner class)

    
    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        // TODO add your handling code here:
        clearForm(); // Clear all form fields and deselect any table row
    }//GEN-LAST:event_btnClearActionPerformed

    private void tblBookingsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblBookingsMouseClicked
        // TODO add your handling code here:
        //Row click logic is handled by ListSelectionListener in setupTable()
        
    }//GEN-LAST:event_tblBookingsMouseClicked

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // Check that a booking is actually selected in the table
        if (selectedBookingId < 0) { // Checks a valid record is selected before attempting an operation
            showError("Please select a booking from the table first."); // Shows an error dialog popup with the failure message
            return; // Exits the method immediately without executing further code
        } // Closes this code block (end of method, class, or inner class)

        // Ask the user to confirm before permanently deleting
        int answer = JOptionPane.showConfirmDialog(this, // Shows a Yes/No dialog and captures the user response
                "Permanently delete Booking BK-" + selectedBookingId + "?\n" // Warning message shown to the user before the irreversible delete operation
                + "This action cannot be undone.\n" // Warning message shown to the user before the irreversible delete operation
                + "The room will also be released back to AVAILABLE.", // Second line of the delete confirmation explaining the room release side effect
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE); // Shows a Yes/No warning dialog asking the user to confirm the payment deletion
        if (answer != JOptionPane.YES_OPTION) return; // Proceeds only if user clicked Yes in the confirmation dialog

        try { // Attempts the following database operations — errors caught below
            // Cancel first so the room is released back to AVAILABLE
            bookingCtrl.cancelBooking(selectedBookingId); // Cancels this booking and releases the room back to AVAILABLE status
            // Then permanently delete the booking record from the database
            bookingCtrl.deleteBooking(selectedBookingId); // Permanently deletes this booking record from the database
            showSuccess("Booking BK-" + selectedBookingId + " deleted successfully."); // Shows a success dialog popup with the result message
            clearForm();      // Reset the form fields
            loadCombos();     // Refresh combos (room is now available again)
            loadTable();      // Refresh the table
        } catch (SQLException ex) { // Catches database errors and shows an error message to the user
            showError("Delete failed: " + ex.getMessage()); // Shows an error dialog popup with the failure message
        } // Closes this code block (end of method, class, or inner class)
    }//GEN-LAST:event_btnDeleteActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCalculate; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JButton btnCancelBooking; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JButton btnClear; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JButton btnCreate; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JButton btnDelete; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JPanel btnPanel; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JButton btnUpdateStatus; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JComboBox<String> cmbCustomer; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JComboBox<String> cmbPackage; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JComboBox<String> cmbRoom; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JComboBox<String> cmbStatus; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JPanel formPanel; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JScrollPane jScrollPane1; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JLabel lblBookingId; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JLabel lblCheckin; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JLabel lblCheckout; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JLabel lblCustomer; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JLabel lblPackage; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JLabel lblRoom; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JLabel lblStatus; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JLabel lblSubtitle; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JLabel lblTitle; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JLabel lblTotal; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JSpinner spnCheckin; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JSpinner spnCheckout; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JPanel tablePanel; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JTable tblBookings; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JPanel titlePanel; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JTextField txtBookingId; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JTextField txtTotal; // Declares a private field variable (value assigned later during initialisation)
    // End of variables declaration//GEN-END:variables
} // Closes this code block (end of method, class, or inner class)
