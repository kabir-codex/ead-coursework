/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package view; // Declares this file belongs to the "view" package

import controller.PaymentController; // Imports PaymentController controller for business logic
import exception.InvalidBookingException; // Imports custom validation exception class
import model.Payment; // Imports Payment data model (POJO)

import javax.swing.*; // Imports all Swing UI components (JPanel, JButton, JLabel, etc.)
import javax.swing.table.DefaultTableModel; // Imports DefaultTableModel Swing component
import javax.swing.table.DefaultTableCellRenderer; // Imports DefaultTableCellRenderer Swing component
import java.awt.*; // Imports AWT classes (Color, Font, Graphics, Layout managers)
import java.sql.SQLException; // Imports SQL exception class for database error handling
import java.text.DecimalFormat; // Imports DecimalFormat for formatting numbers as currency strings
import java.text.SimpleDateFormat; // Imports SimpleDateFormat to convert Date to readable string
import java.util.Date; // Imports Date class for date values
import java.util.List; // Imports List interface for ordered collections

/**
 *
 * @author kabirmoulana
 */
public class PaymentPanel extends javax.swing.JPanel implements MainFrame.Refreshable { // Declares PaymentPanel — extends JPanel and implements Refreshable for auto-reload

    /**
     * Creates new form PaymentPanel
     */
    
    private final PaymentController ctrl = new PaymentController(); // Creates controller instance to handle business logic and database operations
    private DefaultTableModel tableModel; // Stores the table data model managing all rows and columns in the JTable
    /** The payment_id of the currently selected row (-1 = nothing selected). */
    private int selectedPaymentId = -1; // Tracks the database ID of the currently selected record (-1 = nothing selected)
    
    
    public PaymentPanel() { // No-arg constructor — called when this panel is first created by MainFrame
        initComponents(); // Calls NetBeans-generated method that builds and wires all UI components
        setupTable(); // Calls setupTable() to configure table columns, fonts and selection listener
        loadTable(); // Reloads the table with fresh data from the database
    } // Closes this code block (end of method, class, or inner class)
    
    private void setupTable() { // Configures JTable appearance: columns, fonts, colours, row height, selection listener
        tableModel = new DefaultTableModel( // Creates the DefaultTableModel that holds all table rows and columns
            new String[]{"Pay ID","Booking ID","Customer","Amount (LKR)","Method","Date","Notes"}, 0) { // Creates an array of values representing one table row of data
            @Override public boolean isCellEditable(int r, int c) { return false; } // Returns false — table cell cannot be edited by the user
        }; // Closes this anonymous class or array definition
        tblPayments.setModel(tableModel); // Connects the data model to this table so rows appear in the UI
        tblPayments.setRowHeight(28); // Sets the height of each table row in pixels
        tblPayments.setShowGrid(false); // Hides grid lines between cells for a cleaner modern table look
        tblPayments.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12)); // Sets the font typeface, style, and size for this component
        tblPayments.getTableHeader().setBackground(new Color(46, 125, 50)); // Sets the background fill colour of this component
        tblPayments.getTableHeader().setForeground(Color.WHITE); // Sets the text (foreground) colour of this component
        tblPayments.setSelectionBackground(new Color(0xC8, 0xE6, 0xC9)); // Sets the highlight colour shown on the selected table row

        tblPayments.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() { // Applies a custom cell renderer to this table column for styled display
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) { // Returns the styled cell component for this specific table cell
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c); // Casts the value to the required type
                lbl.setForeground(new Color(46, 125, 50)); // Sets the text (foreground) colour of this component
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 12)); // Sets the font typeface, style, and size for this component
                return lbl; // Returns the value label reference for later updates
            } // Closes this code block (end of method, class, or inner class)
        }); // Closes the anonymous listener class and the addListener call

        tblPayments.getSelectionModel().addListSelectionListener(e -> { // Registers a listener that fires when a table row is selected
            if (!e.getValueIsAdjusting() && tblPayments.getSelectedRow() >= 0) { // Ensures a table row is actually selected before proceeding
                int row = tblPayments.getSelectedRow(); // Gets the index of the selected table row (-1 means no row is selected)

                // ── Get Pay ID from column 0 (e.g. "P-5" or "-") ─────────────
                String payIdStr = String.valueOf(tableModel.getValueAt(row, 0)); // Reads the value from column 0 of the selected table row
                if (payIdStr.startsWith("P-")) { // Checks condition and executes block if true
                    selectedPaymentId = Integer.parseInt(payIdStr.replace("P-", "")); // Converts the text string to an integer number for use as an ID
                } else { // Runs this block when none of the above conditions were true
                    selectedPaymentId = -1; // no payment on this row yet
                } // Closes this code block (end of method, class, or inner class)

                // ── Get Booking ID from column 1 (e.g. "BK-3") ───────────────
                String bkId = String.valueOf(tableModel.getValueAt(row, 1)); // Reads the value from column 1 of the selected table row
                txtBookingId.setText(bkId.replace("BK-", "")); // Sets the display text of this component

                // ── Populate form fields from this specific row's data ─────────
                // This shows the SAME values as the table row — not a recalculated sum
                String amountStr = String.valueOf(tableModel.getValueAt(row, 3)); // Reads the value from column 3 of the selected table row
                // Amount column shows "LKR 25,000.00" — strip prefix and commas for editing
                if (!amountStr.equals("-")) { // Checks condition and executes block if true
                    txtAmount.setText(amountStr.replace("LKR ", "").replace(",", "")); // Sets the display text of this component
                } else { // Runs this block when none of the above conditions were true
                    txtAmount.setText(""); // Sets visible text to: ""
                } // Closes this code block (end of method, class, or inner class)

                // Method column (e.g. "CASH") — set the combo box to match
                String methodStr = String.valueOf(tableModel.getValueAt(row, 4)); // Reads the value from column 4 of the selected table row
                if (!methodStr.equals("-")) { // Checks condition and executes block if true
                    cmbMethod.setSelectedItem(methodStr); // Sets the dropdown to show a specific item by its string value
                } // Closes this code block (end of method, class, or inner class)

                // Notes column
                String notesStr = String.valueOf(tableModel.getValueAt(row, 6)); // Reads the value from column 6 of the selected table row
                txtNotes.setText(notesStr.equals("-") ? "" : notesStr); // Sets the display text of this component

                // ── Run lookup to show total paid & balance for this booking ───
                lookupBooking(); // Calls lookupBooking() to fetch and display paid/balance amounts for this booking
            } // Closes this code block (end of method, class, or inner class)
        }); // Closes the anonymous listener class and the addListener call
    } // Closes this code block (end of method, class, or inner class)

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
        bookingRow = new javax.swing.JPanel(); // Creates new JPanel component named "bookingRow"
        txtBookingId = new javax.swing.JTextField(); // Creates new JTextField component named "txtBookingId"
        lblPaid = new javax.swing.JLabel(); // Creates new JLabel component named "lblPaid"
        txtPaid = new javax.swing.JTextField(); // Creates new JTextField component named "txtPaid"
        lblBalance = new javax.swing.JLabel(); // Creates new JLabel component named "lblBalance"
        txtBalance = new javax.swing.JTextField(); // Creates new JTextField component named "txtBalance"
        lblAmount = new javax.swing.JLabel(); // Creates new JLabel component named "lblAmount"
        txtAmount = new javax.swing.JTextField(); // Creates new JTextField component named "txtAmount"
        lblMethod = new javax.swing.JLabel(); // Creates new JLabel component named "lblMethod"
        cmbMethod = new javax.swing.JComboBox<>(); // Creates new JComboBox component named "cmbMethod"
        lblDate = new javax.swing.JLabel(); // Creates new JLabel component named "lblDate"
        spnDate = new javax.swing.JSpinner( // Creates new JSpinner component named "spnDate"
            new javax.swing.SpinnerDateModel(new java.util.Date(), null, null, // Creates a DateModel so this spinner accepts and displays Date values
                java.util.Calendar.DAY_OF_MONTH)); // Closes the SpinnerDateModel constructor — DAY_OF_MONTH is the spinner step unit
        spnDate.setEditor(new javax.swing.JSpinner.DateEditor(spnDate, "dd/MM/yyyy")); // Sets date editor so spinner displays dates in dd/MM/yyyy format
        lblNotes = new javax.swing.JLabel(); // Creates new JLabel component named "lblNotes"
        txtNotes = new javax.swing.JTextField(); // Creates new JTextField component named "txtNotes"
        btnPanel = new javax.swing.JPanel(); // Creates new JPanel component named "btnPanel"
        btnRecord = new javax.swing.JButton(); // Creates new JButton component named "btnRecord"
        btnReceipt = new javax.swing.JButton(); // Creates new JButton component named "btnReceipt"
        btnClear = new javax.swing.JButton(); // Creates new JButton component named "btnClear"
        btnDelete = new javax.swing.JButton(); // Creates new JButton component named "btnDelete"
        btnLookup = new javax.swing.JButton(); // Creates new JButton component named "btnLookup"
        tablePanel = new javax.swing.JPanel(); // Creates new JPanel component named "tablePanel"
        jScrollPane1 = new javax.swing.JScrollPane(); // Creates new JScrollPane component named "jScrollPane1"
        tblPayments = new javax.swing.JTable(); // Creates new JTable component named "tblPayments"

        titlePanel.setBackground(new java.awt.Color(46, 125, 50)); // Sets the background fill colour of this component

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(255, 255, 255)); // Sets the text (foreground) colour of this component
        lblTitle.setText("Payment Management"); // Sets visible text to: "Payment Management"

        lblSubtitle.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblSubtitle.setForeground(new java.awt.Color(200, 230, 201)); // Sets the text (foreground) colour of this component
        lblSubtitle.setText("Record payments, view history and generate receipts"); // Sets visible text to: "Record payments, view history and generate receipts"

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
        formPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Record Payment")); // Applies a visual border or padding around this component

        lblBookingId.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblBookingId.setForeground(new java.awt.Color(95, 107, 124)); // Sets the text (foreground) colour of this component
        lblBookingId.setText("Booking ID *"); // Sets visible text to: "Booking ID *"

        bookingRow.setOpaque(false); // Makes this component transparent so background shows through
        bookingRow.setLayout(new java.awt.BorderLayout()); // Sets the layout manager controlling how child components are positioned

        txtBookingId.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtBookingId.addActionListener(this::txtBookingIdActionPerformed); // Registers a listener that fires when this button or field is activated
        bookingRow.add(txtBookingId, java.awt.BorderLayout.CENTER); // Adds txtBookingId into this container panel

        lblPaid.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblPaid.setForeground(new java.awt.Color(95, 107, 124)); // Sets the text (foreground) colour of this component
        lblPaid.setText("Total Paid So Far (LKR)"); // Sets visible text to: "Total Paid So Far (LKR)"

        txtPaid.setEditable(false); // Makes this text field read-only — user cannot type in it
        txtPaid.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        txtPaid.setForeground(new java.awt.Color(0, 150, 136)); // Sets the text (foreground) colour of this component
        txtPaid.setText("-"); // Sets visible text to: "-"
        txtPaid.addActionListener(this::txtPaidActionPerformed); // Registers a listener that fires when this button or field is activated

        lblBalance.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblBalance.setForeground(new java.awt.Color(95, 107, 124)); // Sets the text (foreground) colour of this component
        lblBalance.setText("Balance Due (LKR)"); // Sets visible text to: "Balance Due (LKR)"

        txtBalance.setEditable(false); // Makes this text field read-only — user cannot type in it
        txtBalance.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        txtBalance.setForeground(new java.awt.Color(198, 40, 40)); // Sets the text (foreground) colour of this component
        txtBalance.setText("-"); // Sets visible text to: "-"
        txtBalance.addActionListener(this::txtBalanceActionPerformed); // Registers a listener that fires when this button or field is activated

        lblAmount.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblAmount.setForeground(new java.awt.Color(95, 107, 124)); // Sets the text (foreground) colour of this component
        lblAmount.setText("Payment Amount (LKR) *"); // Sets visible text to: "Payment Amount (LKR) *"

        txtAmount.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtAmount.addActionListener(this::txtAmountActionPerformed); // Registers a listener that fires when this button or field is activated

        lblMethod.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblMethod.setForeground(new java.awt.Color(95, 107, 124)); // Sets the text (foreground) colour of this component
        lblMethod.setText("Payment Method *"); // Sets visible text to: "Payment Method *"

        cmbMethod.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cmbMethod.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "CASH", "CARD", "BANK_TRANSFER", "ONLINE" })); // Connects the data model to this table so rows appear in the UI
        cmbMethod.addActionListener(this::cmbMethodActionPerformed); // Registers a listener that fires when this button or field is activated

        lblDate.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblDate.setForeground(new java.awt.Color(95, 107, 124)); // Sets the text (foreground) colour of this component
        lblDate.setText("Payment Date *"); // Sets visible text to: "Payment Date *"

        spnDate.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        spnDate.addChangeListener(this::spnDateStateChanged); // Registers a listener that fires when the spinner value changes

        lblNotes.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblNotes.setForeground(new java.awt.Color(95, 107, 124)); // Sets the text (foreground) colour of this component
        lblNotes.setText("Notes"); // Sets visible text to: "Notes"

        txtNotes.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtNotes.addActionListener(this::txtNotesActionPerformed); // Registers a listener that fires when this button or field is activated

        btnPanel.setOpaque(false); // Makes this component transparent so background shows through

        btnRecord.setBackground(new java.awt.Color(46, 125, 50)); // Sets the background fill colour of this component
        btnRecord.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnRecord.setForeground(new java.awt.Color(255, 255, 255)); // Sets the text (foreground) colour of this component
        btnRecord.setText("Record"); // Sets visible text to: "Record"
        btnRecord.setOpaque(true); // Makes this component paint its own background colour
        btnRecord.addActionListener(this::btnRecordActionPerformed); // Registers a listener that fires when this button or field is activated

        btnReceipt.setBackground(new java.awt.Color(26, 115, 232)); // Sets the background fill colour of this component
        btnReceipt.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnReceipt.setForeground(new java.awt.Color(255, 255, 255)); // Sets the text (foreground) colour of this component
        btnReceipt.setText("Receipt"); // Sets visible text to: "Receipt"
        btnReceipt.setOpaque(true); // Makes this component paint its own background colour
        btnReceipt.addActionListener(this::btnReceiptActionPerformed); // Registers a listener that fires when this button or field is activated

        btnClear.setBackground(new java.awt.Color(95, 107, 124)); // Sets the background fill colour of this component
        btnClear.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnClear.setForeground(new java.awt.Color(255, 255, 255)); // Sets the text (foreground) colour of this component
        btnClear.setText("Clear"); // Sets visible text to: "Clear"
        btnClear.setOpaque(true); // Makes this component paint its own background colour
        btnClear.addActionListener(this::btnClearActionPerformed); // Registers a listener that fires when this button or field is activated

        btnDelete.setBackground(new java.awt.Color(198, 40, 40)); // Sets the background fill colour of this component
        btnDelete.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnDelete.setForeground(new java.awt.Color(255, 255, 255)); // Sets the text (foreground) colour of this component
        btnDelete.setText("Delete"); // Sets visible text to: "Delete"
        btnDelete.setOpaque(true); // Makes this component paint its own background colour
        btnDelete.addActionListener(this::btnDeleteActionPerformed); // Registers a listener that fires when this button or field is activated

        javax.swing.GroupLayout btnPanelLayout = new javax.swing.GroupLayout(btnPanel); // Creates a GroupLayout manager for precise component positioning
        btnPanel.setLayout(btnPanelLayout); // Sets the layout manager controlling how child components are positioned
        btnPanelLayout.setHorizontalGroup( // Applies horizontal layout rules to this panel
            btnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING) // Groups components side-by-side (parallel / same row)
            .addGroup(btnPanelLayout.createSequentialGroup() // Nests a sub-group inside this layout group for more complex arrangement
                .addComponent(btnRecord) // Places the btnRecord into this layout group
                .addGap(6, 6, 6) // Inserts a 6px gap between components
                .addComponent(btnReceipt) // Places the btnReceipt into this layout group
                .addGap(6, 6, 6) // Inserts a 6px gap between components
                .addComponent(btnClear) // Places the btnClear into this layout group
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED) // Adds a standard gap between related components (follows platform guidelines)
                .addComponent(btnDelete) // Places the btnDelete into this layout group
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)) // Adds standard margins at the edge of the container
        ); // Closes the layout group or method call
        btnPanelLayout.setVerticalGroup( // Applies vertical layout rules to this panel
            btnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE) // Groups components side-by-side (parallel / same row)
            .addComponent(btnRecord) // Places the btnRecord into this layout group
            .addComponent(btnReceipt) // Places the btnReceipt into this layout group
            .addComponent(btnClear) // Places the btnClear into this layout group
            .addComponent(btnDelete) // Places the btnDelete into this layout group
        ); // Closes the layout group or method call

        btnLookup.setBackground(new java.awt.Color(26, 115, 232)); // Sets the background fill colour of this component
        btnLookup.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnLookup.setForeground(new java.awt.Color(255, 255, 255)); // Sets the text (foreground) colour of this component
        btnLookup.setText("Lookup"); // Sets visible text to: "Lookup"
        btnLookup.setOpaque(true); // Makes this component paint its own background colour
        btnLookup.addActionListener(this::btnLookupActionPerformed); // Registers a listener that fires when this button or field is activated

        javax.swing.GroupLayout formPanelLayout = new javax.swing.GroupLayout(formPanel); // Creates a GroupLayout manager for precise component positioning
        formPanel.setLayout(formPanelLayout); // Sets the layout manager controlling how child components are positioned
        formPanelLayout.setHorizontalGroup( // Applies horizontal layout rules to this panel
            formPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING) // Groups components side-by-side (parallel / same row)
            .addGroup(formPanelLayout.createSequentialGroup() // Nests a sub-group inside this layout group for more complex arrangement
                .addGap(10, 10, 10) // Inserts a 10px gap between components
                .addGroup(formPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING) // Groups components side-by-side (parallel / same row)
                    .addGroup(formPanelLayout.createSequentialGroup() // Nests a sub-group inside this layout group for more complex arrangement
                        .addGroup(formPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING) // Groups components side-by-side (parallel / same row)
                            .addComponent(bookingRow, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE) // Adds this component into the layout group at this position
                            .addComponent(lblPaid) // Places the lblPaid into this layout group
                            .addComponent(txtPaid) // Places the txtPaid into this layout group
                            .addComponent(lblBalance) // Places the lblBalance into this layout group
                            .addComponent(txtBalance) // Places the txtBalance into this layout group
                            .addComponent(lblAmount) // Places the lblAmount into this layout group
                            .addComponent(txtAmount) // Places the txtAmount into this layout group
                            .addComponent(lblMethod) // Places the lblMethod into this layout group
                            .addComponent(cmbMethod, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE) // Adds this component into the layout group at this position
                            .addComponent(lblDate) // Places the lblDate into this layout group
                            .addComponent(spnDate) // Places the spnDate into this layout group
                            .addComponent(lblNotes) // Places the lblNotes into this layout group
                            .addComponent(txtNotes) // Places the txtNotes into this layout group
                            .addComponent(btnPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)) // Adds this component into the layout group at this position
                        .addGap(10, 10, 10)) // Inserts a 10px gap between components
                    .addGroup(formPanelLayout.createSequentialGroup() // Nests a sub-group inside this layout group for more complex arrangement
                        .addComponent(lblBookingId) // Places the lblBookingId into this layout group
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED) // Adds a standard gap between related components (follows platform guidelines)
                        .addComponent(btnLookup, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE) // Adds this component into the layout group at this position
                        .addContainerGap(10, Short.MAX_VALUE)))) // Adds standard container margin gaps at the edges of the panel
        ); // Closes the layout group or method call
        formPanelLayout.setVerticalGroup( // Applies vertical layout rules to this panel
            formPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING) // Groups components side-by-side (parallel / same row)
            .addGroup(formPanelLayout.createSequentialGroup() // Nests a sub-group inside this layout group for more complex arrangement
                .addGap(4, 4, 4) // Inserts a 4px gap between components
                .addGroup(formPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE) // Groups components side-by-side (parallel / same row)
                    .addComponent(lblBookingId) // Places the lblBookingId into this layout group
                    .addComponent(btnLookup)) // Places the btnLookup into this layout group
                .addGap(10, 10, 10) // Inserts a 10px gap between components
                .addComponent(bookingRow, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE) // Adds this component into the layout group at this position
                .addGap(8, 8, 8) // Inserts a 8px gap between components
                .addComponent(lblPaid) // Places the lblPaid into this layout group
                .addGap(3, 3, 3) // Inserts a 3px gap between components
                .addComponent(txtPaid, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE) // Adds this component into the layout group at this position
                .addGap(8, 8, 8) // Inserts a 8px gap between components
                .addComponent(lblBalance) // Places the lblBalance into this layout group
                .addGap(3, 3, 3) // Inserts a 3px gap between components
                .addComponent(txtBalance, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE) // Adds this component into the layout group at this position
                .addGap(8, 8, 8) // Inserts a 8px gap between components
                .addComponent(lblAmount) // Places the lblAmount into this layout group
                .addGap(3, 3, 3) // Inserts a 3px gap between components
                .addComponent(txtAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE) // Adds this component into the layout group at this position
                .addGap(8, 8, 8) // Inserts a 8px gap between components
                .addComponent(lblMethod) // Places the lblMethod into this layout group
                .addGap(3, 3, 3) // Inserts a 3px gap between components
                .addComponent(cmbMethod, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE) // Adds this component into the layout group at this position
                .addGap(8, 8, 8) // Inserts a 8px gap between components
                .addComponent(lblDate) // Places the lblDate into this layout group
                .addGap(3, 3, 3) // Inserts a 3px gap between components
                .addComponent(spnDate, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE) // Adds this component into the layout group at this position
                .addGap(8, 8, 8) // Inserts a 8px gap between components
                .addComponent(lblNotes) // Places the lblNotes into this layout group
                .addGap(3, 3, 3) // Inserts a 3px gap between components
                .addComponent(txtNotes, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE) // Adds this component into the layout group at this position
                .addGap(12, 12, 12) // Inserts a 12px gap between components
                .addComponent(btnPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE) // Adds this component into the layout group at this position
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)) // Adds standard margins at the edge of the container
        ); // Closes the layout group or method call

        tablePanel.setBackground(new java.awt.Color(255, 255, 255)); // Sets the background fill colour of this component
        tablePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Payment History")); // Applies a visual border or padding around this component

        tblPayments.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        tblPayments.setModel(new javax.swing.table.DefaultTableModel( // Connects the data model to this table so rows appear in the UI
            new Object [][] { // Starts the initial empty data array for the table model (no rows yet)

            }, // Closes the data array and separates it from the column names array
            new String [] { // Starts the array of column header names for the table
                "Pay ID", "Booking ID", "Customer", "Amount (LKR)", "Method", "Date", "Notes" // Column header names: Pay ID, Booking ID, Customer, Amount, Method, Date, Notes
            } // Closes this code block (end of method, class, or inner class)
        ) { // Opens the anonymous DefaultTableModel subclass body
            boolean[] canEdit = new boolean [] { // Boolean array defining which columns can be edited (all false = read-only)
                false, false, false, false, false, false, false // All columns set to false — no table cell is editable by the user
            }; // Closes this anonymous class or array definition

            public boolean isCellEditable(int rowIndex, int columnIndex) { // Overrides parent — always returns false so table cells cannot be edited
                return canEdit [columnIndex]; // Returns the value: canEdit [columnIndex]
            } // Closes this code block (end of method, class, or inner class)
        }); // Closes the anonymous listener class and the addListener call
        tblPayments.setRowHeight(28); // Sets the height of each table row in pixels
        tblPayments.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION); // Restricts the table so only one row can be selected at a time
        tblPayments.setShowGrid(false); // Hides grid lines between cells for a cleaner modern table look
        tblPayments.addMouseListener(new java.awt.event.MouseAdapter() { // Registers a listener that fires on mouse click events
            public void mouseClicked(java.awt.event.MouseEvent evt) { // Defines the mouseClicked() method
                tblPaymentsMouseClicked(evt); // Delegates to tblPaymentsMouseClicked to handle the mouse click event
            } // Closes this code block (end of method, class, or inner class)
        }); // Closes the anonymous listener class and the addListener call
        jScrollPane1.setViewportView(tblPayments); // Places the component inside the scroll pane so it scrolls correctly

        javax.swing.GroupLayout tablePanelLayout = new javax.swing.GroupLayout(tablePanel); // Creates a GroupLayout manager for precise component positioning
        tablePanel.setLayout(tablePanelLayout); // Sets the layout manager controlling how child components are positioned
        tablePanelLayout.setHorizontalGroup( // Applies horizontal layout rules to this panel
            tablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING) // Groups components side-by-side (parallel / same row)
            .addGroup(tablePanelLayout.createSequentialGroup() // Nests a sub-group inside this layout group for more complex arrangement
                .addGap(8, 8, 8) // Inserts a 8px gap between components
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 615, Short.MAX_VALUE) // Adds this component into the layout group at this position
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
                        .addGap(10, 10, 10) // Inserts a 10px gap between components
                        .addComponent(tablePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE) // Adds this component into the layout group at this position
                        .addGap(10, 10, 10))) // Inserts a 10px gap between components
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
        lookupBooking(); // Pressing Enter in Booking ID triggers the lookup
    
    }//GEN-LAST:event_txtBookingIdActionPerformed

    private void btnLookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLookupActionPerformed
        // TODO add your handling code here:
        lookupBooking();  // Calls lookupBooking() to fetch and display paid/balance amounts for this booking
    }//GEN-LAST:event_btnLookupActionPerformed

    private void lookupBooking() { // Queries payments for the entered booking ID and shows paid amount and balance
        // Get the booking ID the user typed in the txtBookingId field
        String raw = txtBookingId.getText().trim(); // Gets the field text and removes any leading/trailing spaces
        if (raw.isEmpty()) { // Checks condition and executes block if true
            showError("Please enter a Booking ID to lookup."); // Shows an error dialog popup with the failure message
            return; // Exits the method immediately without executing further code
        } // Closes this code block (end of method, class, or inner class)
        try { // Attempts the following database operations — errors caught below
            int bookingId = Integer.parseInt(raw); // Convert text to number
            DecimalFormat df = new DecimalFormat("#,###.00"); // Creates a number formatter using the given pattern (e.g. commas for thousands)

            // Get total already paid for this booking from the payments table
            double paid = ctrl.getTotalPaidForBooking(bookingId); // Gets total amount already paid for this booking
            txtPaid.setText("LKR " + df.format(paid)); // Sets the display text of this component

            // Get the booking total from the bookings table to calculate balance
            controller.BookingController bc = new controller.BookingController(); // Creates a BookingController instance to fetch the booking total amount
            model.Booking bk = bc.getBookingById(bookingId); // Fetches the full Booking record from the database using its ID
            if (bk != null) { // Checks for null to prevent NullPointerException
                // Balance due = total booking amount minus what has already been paid
                double balance = Math.max(0, bk.getTotalAmount() - paid); // Returns the larger value — prevents negative balance from showing
                txtBalance.setText("LKR " + df.format(balance)); // Sets the display text of this component
            } else { // Runs this block when none of the above conditions were true
                txtBalance.setText("Booking not found"); // Sets visible text to: "Booking not found"
            } // Closes this code block (end of method, class, or inner class)
        } catch (NumberFormatException ex) { // Catches invalid number text (e.g. letters where digits expected)
            showError("Enter a valid numeric Booking ID (e.g. 3, not BK-3)."); // Shows an error dialog popup with the failure message
        } catch (SQLException ex) { // Catches database errors and shows an error message to the user
            showError("Lookup failed: " + ex.getMessage()); // Shows an error dialog popup with the failure message
        } // Closes this code block (end of method, class, or inner class)
    } // Closes this code block (end of method, class, or inner class)
    
    private void txtPaidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPaidActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPaidActionPerformed

    private void txtBalanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBalanceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBalanceActionPerformed

    private void txtAmountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAmountActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAmountActionPerformed

    private void cmbMethodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbMethodActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbMethodActionPerformed

    private void spnDateStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnDateStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_spnDateStateChanged

    private void txtNotesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNotesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNotesActionPerformed

    private void btnRecordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRecordActionPerformed
        // TODO add your handling code here:
         try { // Attempts the following database operations — errors caught below
            int bookingId = Integer.parseInt(txtBookingId.getText().trim()); // Converts the text string to an integer number for use as an ID
            Date payDate = (Date) ((javax.swing.SpinnerDateModel) spnDate.getModel()).getDate(); // Declares Date model variable "payDate"
            ctrl.recordPayment(bookingId, txtAmount.getText(), // Gets the current text content of this input field as a String
                    Payment.PaymentMethod.valueOf((String)cmbMethod.getSelectedItem()), // Gets the currently selected item from the dropdown as an Object
                    payDate, txtNotes.getText()); // Gets the current text content of this input field as a String
            showSuccess("Payment recorded successfully."); // Shows a success dialog popup with the result message
            clearForm(); loadTable(); // Reloads the table with fresh data from the database
        } catch (NumberFormatException ex) { showError("Enter a valid Booking ID (number)."); } // Catches invalid number text (e.g. letters where digits expected)
          catch (InvalidBookingException ex) { showError(ex.getMessage()); } // Catches validation errors from the controller and shows them as an error dialog
          catch (SQLException ex) { showError("Database error: " + ex.getMessage()); } // Catches database errors and shows them as an error dialog to the user
    
    }//GEN-LAST:event_btnRecordActionPerformed

    private void btnReceiptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReceiptActionPerformed
        // TODO add your handling code here:
        String raw = txtBookingId.getText().trim(); // Gets the field text and removes any leading/trailing spaces
        if (raw.isEmpty()) { showError("Enter a Booking ID first."); return; } // Checks if the input field is empty — stops execution if nothing was typed
        try { // Attempts the following database operations — errors caught below
            int bookingId = Integer.parseInt(raw); // Converts the text string to an integer number for use as an ID
            List<Payment> payments = ctrl.getPaymentsByBookingId(bookingId); // Fetches all payment records for this booking ID to build the receipt
            if (payments.isEmpty()) { showError("No payments found for Booking ID " + bookingId); return; } // Checks if the input field is empty — stops execution if nothing was typed

            StringBuilder sb = new StringBuilder(); // Creates a mutable text builder for assembling the receipt text
            sb.append("========================================\n"); // Appends the given text onto the end of the string builder
            sb.append("       TourEase - PAYMENT RECEIPT       \n"); // Appends the given text onto the end of the string builder
            sb.append("========================================\n"); // Appends the given text onto the end of the string builder
            sb.append("Booking ID : BK-").append(bookingId).append("\n"); // Appends the given text onto the end of the string builder
            sb.append("----------------------------------------\n"); // Appends the given text onto the end of the string builder
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); // Creates a date formatter to convert Date objects to "dd/MM/yyyy" strings
            double total = 0; // Stores the calculated total booking amount
            for (Payment p : payments) { // Loops through each payment record from the database
                sb.append(String.format("%-20s %12s%n", // Creates a formatted string using printf-style placeholders
                        sdf.format(p.getPaymentDate()) + " [" + p.getPaymentMethod() + "]", // Converts the Date to a readable "dd/MM/yyyy" string for table display
                        "LKR " + new DecimalFormat("#,###.00").format(p.getAmount()))); // Creates a number formatter using the given pattern (e.g. commas for thousands)
                total += p.getAmount(); // Gets the payment amount from this payment record
            } // Closes this code block (end of method, class, or inner class)
            sb.append("----------------------------------------\n"); // Appends the given text onto the end of the string builder
            sb.append(String.format("%-20s %12s%n", "TOTAL PAID:", "LKR " + new DecimalFormat("#,###.00").format(total))); // Creates a number formatter using the given pattern (e.g. commas for thousands)
            sb.append("========================================\n"); // Appends the given text onto the end of the string builder
            sb.append("   Thank you for choosing TourEase!    \n"); // Appends the given text onto the end of the string builder
            sb.append("========================================\n"); // Appends the given text onto the end of the string builder

            JTextArea ta = new JTextArea(sb.toString()); // Converts this StringBuilder or object to a plain String
            ta.setFont(new Font("Monospaced", Font.PLAIN, 13)); // Sets the font typeface, style, and size for this component
            ta.setEditable(false); // Makes this text field read-only — user cannot type in it
            JOptionPane.showMessageDialog(this, new JScrollPane(ta), "Payment Receipt - BK-" + bookingId, // Shows a pop-up dialog box with the specified message and title
                    JOptionPane.INFORMATION_MESSAGE); // Closes the showMessageDialog call — INFORMATION_MESSAGE shows a blue info icon
        } catch (NumberFormatException ex) { showError("Invalid Booking ID."); } // Catches invalid number text (e.g. letters where digits expected)
          catch (SQLException ex) { showError("Error loading payments: " + ex.getMessage()); } // Catches errors while loading payments for the receipt and shows an error message
    
    }//GEN-LAST:event_btnReceiptActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        // TODO add your handling code here:
        clearForm(); // Resets all form fields back to their empty default state
    }//GEN-LAST:event_btnClearActionPerformed

    private void loadTable() { // Fetches all records from database and populates the table rows
        try { // Attempts the following database operations — errors caught below
            tableModel.setRowCount(0); // Clears all existing rows from the table before reloading fresh data
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); // Creates a date formatter to convert Date objects to "dd/MM/yyyy" strings
            DecimalFormat df = new DecimalFormat("#,###.00"); // Creates a number formatter using the given pattern (e.g. commas for thousands)

            for (Payment p : ctrl.getAllBookingsWithPaymentInfo()) { // Loops through each booking record from the database
                // If no payment exists yet, payment_id will be 0
                String payId  = (p.getPaymentId() > 0)  ? "P-" + p.getPaymentId() : "-"; // Declares String variable "payId" with initial value
                String amount = (p.getAmount() > 0)      ? "LKR " + df.format(p.getAmount()) : "-"; // Formatted string value for "amount"
                String method = (p.getPaymentMethod() != null) ? p.getPaymentMethod().name() : "-"; // Gets the enum value as a plain String (e.g. CONFIRMED, CASH, ACTIVE)
                String date   = (p.getPaymentDate() != null)   ? sdf.format(p.getPaymentDate()) : "-"; // Formatted string value for "date"
                String notes  = (p.getNotes() != null)          ? p.getNotes() : "-"; // Declares String variable "notes" with initial value

                tableModel.addRow(new Object[]{ // Adds one data record as a new visible row in the table
                    payId, // Formatted payment ID string (e.g. "P-5" or "-") for the Pay ID column
                    "BK-" + p.getBookingId(), // Gets the booking ID linked to this payment
                    p.getCustomerName(), // Gets the customer full name from the booking JOIN result
                    amount, // Formatted amount string (e.g. "LKR 25,000.00" or "-") for the Amount column
                    method, // Payment method string (e.g. "CASH" or "-") for the Method column
                    date, // Formatted date string (e.g. "15/01/2026" or "-") for the Date column
                    notes // Notes string (or "-") for the Notes column — last item in the row array
                }); // Closes the anonymous listener class and the addListener call
            } // Closes this code block (end of method, class, or inner class)
        } catch (SQLException ex) { showError("Failed to load payments."); } // Catches database errors and shows an error message to the user
    } // Closes this code block (end of method, class, or inner class)

    private void clearForm() { // Resets every form field to blank and deselects any highlighted table row
        txtBookingId.setText(""); txtAmount.setText(""); txtNotes.setText(""); // Sets visible text to: ""
        txtPaid.setText("-"); txtBalance.setText("-"); // Sets visible text to: "-"
        cmbMethod.setSelectedIndex(0); spnDate.setValue(new Date()); // Resets the dropdown back to its first item
        tblPayments.clearSelection(); // Removes any row highlight/selection from the table
    } // Closes this code block (end of method, class, or inner class)

    @Override // Signals this method overrides a method from the parent class or interface
    public void refresh() { loadTable(); } // Called by MainFrame when user navigates here — reloads all data from database

    private void styleLabel(JLabel l, String text) { l.setFont(new Font("Segoe UI",Font.PLAIN,11)); l.setForeground(new Color(95,107,124)); l.setText(text); } // Sets the text (foreground) colour of this component
    private void styleBtn(JButton b, String text, Color bg) { b.setFont(new Font("Segoe UI",Font.BOLD,12)); b.setText(text); b.setBackground(bg); b.setForeground(Color.WHITE); b.setOpaque(true); } // Sets the background fill colour of this component
    private void showSuccess(String msg) { JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE); } // Displays a blue information dialog with a success message
    private void showError(String msg)   { JOptionPane.showMessageDialog(this, msg, "Error",   JOptionPane.ERROR_MESSAGE); } // Displays a red error dialog with the given error description

    
    private void tblPaymentsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPaymentsMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tblPaymentsMouseClicked

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // Check that a row with an actual payment is selected
        if (selectedPaymentId < 0) { // Checks a valid record is selected before attempting an operation
            showError("Please select a payment record to delete.\n" // Shows an error dialog popup with the failure message
                    + "(Rows showing '-' have no payment yet and cannot be deleted.)"); // Second line of the delete error message explaining why some rows cannot be deleted
            return; // Exits the method immediately without executing further code
        } // Closes this code block (end of method, class, or inner class)

        // Ask the user to confirm before permanently deleting
        int answer = JOptionPane.showConfirmDialog(this, // Shows a Yes/No dialog and captures the user response
                "Permanently delete Payment P-" + selectedPaymentId + "?\n" // Warning message shown to the user before the irreversible delete operation
                + "This action cannot be undone.", // Warning message shown to the user before the irreversible delete operation
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE); // Shows a Yes/No warning dialog asking the user to confirm the payment deletion
        if (answer != JOptionPane.YES_OPTION) return; // Proceeds only if user clicked Yes in the confirmation dialog

        try { // Attempts the following database operations — errors caught below
            ctrl.deletePayment(selectedPaymentId); // Delete from database
            showSuccess("Payment P-" + selectedPaymentId + " deleted successfully."); // Shows a success dialog popup with the result message
            selectedPaymentId = -1; // Reset selected ID
            clearForm();            // Clear the form fields
            loadTable();            // Refresh the table
        } catch (SQLException ex) { // Catches database errors and shows an error message to the user
            showError("Delete failed: " + ex.getMessage()); // Shows an error dialog popup with the failure message
        } // Closes this code block (end of method, class, or inner class)
    }//GEN-LAST:event_btnDeleteActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bookingRow; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JButton btnClear; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JButton btnDelete; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JButton btnLookup; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JPanel btnPanel; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JButton btnReceipt; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JButton btnRecord; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JComboBox<String> cmbMethod; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JPanel formPanel; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JScrollPane jScrollPane1; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JLabel lblAmount; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JLabel lblBalance; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JLabel lblBookingId; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JLabel lblDate; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JLabel lblMethod; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JLabel lblNotes; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JLabel lblPaid; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JLabel lblSubtitle; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JLabel lblTitle; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JSpinner spnDate; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JPanel tablePanel; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JTable tblPayments; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JPanel titlePanel; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JTextField txtAmount; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JTextField txtBalance; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JTextField txtBookingId; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JTextField txtNotes; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JTextField txtPaid; // Declares a private field variable (value assigned later during initialisation)
    // End of variables declaration//GEN-END:variables
} // Closes this code block (end of method, class, or inner class)
