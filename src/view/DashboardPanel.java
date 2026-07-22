package view; // Declares this file belongs to the "view" package

import controller.BookingController; // Imports BookingController controller for business logic
import controller.CustomerController; // Imports CustomerController controller for business logic
import controller.PackageController; // Imports PackageController controller for business logic
import controller.RoomController; // Imports RoomController controller for business logic
import model.Booking; // Imports Booking data model (POJO)

import javax.swing.*; // Imports all Swing UI components (JPanel, JButton, JLabel, etc.)
import javax.swing.table.DefaultTableModel; // Imports DefaultTableModel Swing component
import java.awt.*; // Imports AWT classes (Color, Font, Graphics, Layout managers)
import java.sql.SQLException; // Imports SQL exception class for database error handling
import java.text.DecimalFormat; // Imports DecimalFormat for formatting numbers as currency strings
import java.text.SimpleDateFormat; // Imports SimpleDateFormat to convert Date to readable string
import java.util.List; // Imports List interface for ordered collections

/**
 * DashboardPanel – Overview screen shown immediately after login.
 *
 * Shows 4 stat cards (Customers, Rooms, Bookings, Revenue),
 * a custom-painted monthly revenue bar chart, and a recent bookings table.
 *
 * The stat cards are built in code (buildStatCard) and embedded inside
 * the NetBeans-generated card container panels after initComponents() runs.
 */
public class DashboardPanel extends javax.swing.JPanel implements MainFrame.Refreshable { // Declares DashboardPanel — extends JPanel and implements Refreshable for auto-reload

    // ── Controllers ───────────────────────────────────────────────────────────
    private final BookingController  bookingCtrl  = new BookingController(); // Creates controller instance to handle business logic and database operations
    private final CustomerController customerCtrl = new CustomerController(); // Creates controller instance to handle business logic and database operations
    private final PackageController  packageCtrl  = new PackageController(); // Creates controller instance to handle business logic and database operations
    private final RoomController     roomCtrl     = new RoomController(); // Creates controller instance to handle business logic and database operations

    // ── State ─────────────────────────────────────────────────────────────────
    /** Monthly revenue values (index 0 = Jan, 11 = Dec) used by the chart. */
    private double[] monthlyRevenue = new double[12]; // Array holding 12 monthly revenue totals (index 0=January, index 11=December)

    /** The custom-painted chart panel — repainted when data refreshes. */
    private JPanel chartPanel; // Reference to the custom-painted bar chart panel

    /** Table model for the recent bookings table. */
    private DefaultTableModel recentTableModel; // Stores the table data model managing all rows and columns in the JTable

    // ── Stat card value labels (kept as fields so refreshData can update them) ─
    private JLabel lblCustomersVal; // Saved reference to this stat card value label so refreshData() can update it
    private JLabel lblRoomsVal; // Saved reference to this stat card value label so refreshData() can update it
    private JLabel lblBookingsVal; // Saved reference to this stat card value label so refreshData() can update it
    private JLabel lblRevenueVal; // Saved reference to this stat card value label so refreshData() can update it

    // ── Constructor ───────────────────────────────────────────────────────────

    public DashboardPanel() { // No-arg constructor — called when this panel is first created by MainFrame
        initComponents();       // Build the NetBeans-generated skeleton layout
        buildStatCards();       // Fill each empty card panel with title + value labels
        buildChart();           // Embed the custom bar chart into chartCard
        setupRecentTable();     // Wire up the recent bookings table model
        refreshData();          // Load live data from the database
    } // Closes this code block (end of method, class, or inner class)

    // ── NetBeans generated skeleton ───────────────────────────────────────────

    @SuppressWarnings("unchecked") // Tells compiler to suppress unchecked type cast warnings
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() { // NetBeans auto-generated method — creates and configures all UI components

        titlePanel    = new javax.swing.JPanel(); // Creates new JPanel component named "titlePanel"
        lblTitle      = new javax.swing.JLabel(); // Creates new JLabel component named "lblTitle"
        lblSubtitle   = new javax.swing.JLabel(); // Creates new JLabel component named "lblSubtitle"
        statsPanel    = new javax.swing.JPanel(); // Creates new JPanel component named "statsPanel"
        cardCustomers = new javax.swing.JPanel(); // Creates new JPanel component named "cardCustomers"
        cardRooms     = new javax.swing.JPanel(); // Creates new JPanel component named "cardRooms"
        cardBookings  = new javax.swing.JPanel(); // Creates new JPanel component named "cardBookings"
        cardRevenue   = new javax.swing.JPanel(); // Creates new JPanel component named "cardRevenue"
        chartCard     = new javax.swing.JPanel(); // Creates new JPanel component named "chartCard"
        recentCard    = new javax.swing.JPanel(); // Creates new JPanel component named "recentCard"
        jScrollPane1  = new javax.swing.JScrollPane(); // Creates new JScrollPane component named "jScrollPane1"
        tblRecent     = new javax.swing.JTable(); // Creates new JTable component named "tblRecent"

        // ── Title bar ──────────────────────────────────────────────────────────
        titlePanel.setBackground(new java.awt.Color(13, 71, 161)); // Sets the background fill colour of this component

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 20)); // Sets the font typeface, style, and size for this component
        lblTitle.setForeground(new java.awt.Color(255, 255, 255)); // Sets the text (foreground) colour of this component
        lblTitle.setText("Dashboard"); // Sets visible text to: "Dashboard"

        lblSubtitle.setFont(new java.awt.Font("Segoe UI", 0, 11)); // Sets the font typeface, style, and size for this component
        lblSubtitle.setForeground(new java.awt.Color(179, 229, 252)); // Sets the text (foreground) colour of this component
        lblSubtitle.setText("Overview of hotel & tourism operations"); // Sets visible text to: "Overview of hotel & tourism operations"

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

        // ── Stat cards row – empty containers, filled by buildStatCards() ──────
        statsPanel.setOpaque(false); // Makes this component transparent so background shows through

        cardCustomers.setBackground(new java.awt.Color(255, 255, 255)); // Sets the background fill colour of this component
        cardRooms.setBackground(new java.awt.Color(255, 255, 255)); // Sets the background fill colour of this component
        cardBookings.setBackground(new java.awt.Color(255, 255, 255)); // Sets the background fill colour of this component
        cardRevenue.setBackground(new java.awt.Color(255, 255, 255)); // Sets the background fill colour of this component

        javax.swing.GroupLayout statsPanelLayout = new javax.swing.GroupLayout(statsPanel); // Creates a GroupLayout manager for precise component positioning
        statsPanel.setLayout(statsPanelLayout); // Sets the layout manager controlling how child components are positioned
        statsPanelLayout.setHorizontalGroup( // Applies horizontal layout rules to this panel
            statsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING) // Groups components side-by-side (parallel / same row)
            .addGroup(statsPanelLayout.createSequentialGroup() // Nests a sub-group inside this layout group for more complex arrangement
                .addComponent(cardCustomers, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE) // Adds this component into the layout group at this position
                .addGap(10, 10, 10) // Inserts a 10px gap between components
                .addComponent(cardRooms, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE) // Adds this component into the layout group at this position
                .addGap(10, 10, 10) // Inserts a 10px gap between components
                .addComponent(cardBookings, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE) // Adds this component into the layout group at this position
                .addGap(10, 10, 10) // Inserts a 10px gap between components
                .addComponent(cardRevenue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)) // Adds this component into the layout group at this position
        ); // Closes the layout group or method call
        statsPanelLayout.setVerticalGroup( // Applies vertical layout rules to this panel
            statsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING) // Groups components side-by-side (parallel / same row)
            .addComponent(cardCustomers, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE) // Adds this component into the layout group at this position
            .addComponent(cardRooms,     javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE) // Adds this component into the layout group at this position
            .addComponent(cardBookings,  javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE) // Adds this component into the layout group at this position
            .addComponent(cardRevenue,   javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE) // Adds this component into the layout group at this position
        ); // Closes the layout group or method call

        // ── Chart card – empty container, filled by buildChart() ───────────────
        chartCard.setBackground(new java.awt.Color(255, 255, 255)); // Sets the background fill colour of this component
        chartCard.setBorder(javax.swing.BorderFactory.createTitledBorder("Monthly Revenue (2026)")); // Applies a visual border or padding around this component

        javax.swing.GroupLayout chartCardLayout = new javax.swing.GroupLayout(chartCard); // Creates a GroupLayout manager for precise component positioning
        chartCard.setLayout(chartCardLayout); // Sets the layout manager controlling how child components are positioned
        chartCardLayout.setHorizontalGroup( // Applies horizontal layout rules to this panel
            chartCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING) // Groups components side-by-side (parallel / same row)
            .addGap(0, 820, Short.MAX_VALUE) // Inserts a 0px gap between components
        ); // Closes the layout group or method call
        chartCardLayout.setVerticalGroup( // Applies vertical layout rules to this panel
            chartCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING) // Groups components side-by-side (parallel / same row)
            .addGap(0, 216, Short.MAX_VALUE) // Inserts a 0px gap between components
        ); // Closes the layout group or method call

        // ── Recent bookings card ───────────────────────────────────────────────
        recentCard.setBackground(new java.awt.Color(255, 255, 255)); // Sets the background fill colour of this component
        recentCard.setBorder(javax.swing.BorderFactory.createTitledBorder("Recent Bookings")); // Applies a visual border or padding around this component

        tblRecent.setFont(new java.awt.Font("Segoe UI", 0, 12)); // Sets the font typeface, style, and size for this component
        tblRecent.setRowHeight(26); // Sets the height of each table row in pixels
        tblRecent.setShowGrid(false); // Hides grid lines between cells for a cleaner modern table look
        tblRecent.addMouseListener(new java.awt.event.MouseAdapter() { // Registers a listener that fires on mouse click events
            public void mouseClicked(java.awt.event.MouseEvent evt) { // Defines the mouseClicked() method
                tblRecentMouseClicked(evt); // Delegates to tblRecentMouseClicked to handle the mouse click event
            } // Closes this code block (end of method, class, or inner class)
        }); // Closes the anonymous listener class and the addListener call
        jScrollPane1.setViewportView(tblRecent); // Places the component inside the scroll pane so it scrolls correctly

        javax.swing.GroupLayout recentCardLayout = new javax.swing.GroupLayout(recentCard); // Creates a GroupLayout manager for precise component positioning
        recentCard.setLayout(recentCardLayout); // Sets the layout manager controlling how child components are positioned
        recentCardLayout.setHorizontalGroup( // Applies horizontal layout rules to this panel
            recentCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING) // Groups components side-by-side (parallel / same row)
            .addGroup(recentCardLayout.createSequentialGroup() // Nests a sub-group inside this layout group for more complex arrangement
                .addGap(8, 8, 8) // Inserts a 8px gap between components
                .addComponent(jScrollPane1) // Places the jScrollPane1 into this layout group
                .addGap(8, 8, 8)) // Inserts a 8px gap between components
        ); // Closes the layout group or method call
        recentCardLayout.setVerticalGroup( // Applies vertical layout rules to this panel
            recentCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING) // Groups components side-by-side (parallel / same row)
            .addGroup(recentCardLayout.createSequentialGroup() // Nests a sub-group inside this layout group for more complex arrangement
                .addGap(8, 8, 8) // Inserts a 8px gap between components
                .addComponent(jScrollPane1) // Places the jScrollPane1 into this layout group
                .addGap(8, 8, 8)) // Inserts a 8px gap between components
        ); // Closes the layout group or method call

        // ── Main layout ────────────────────────────────────────────────────────
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this); // Creates a GroupLayout manager for precise component positioning
        this.setLayout(layout); // Sets the layout manager controlling how child components are positioned
        layout.setHorizontalGroup( // Defines horizontal component arrangement for the outer frame layout
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING) // Groups components side-by-side (parallel / same row)
            .addComponent(titlePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE) // Adds this component into the layout group at this position
            .addGroup(layout.createSequentialGroup() // Nests a sub-group inside this layout group for more complex arrangement
                .addGap(10, 10, 10) // Inserts a 10px gap between components
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING) // Groups components side-by-side (parallel / same row)
                    .addComponent(statsPanel,  javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE) // Adds this component into the layout group at this position
                    .addComponent(chartCard,   javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE) // Adds this component into the layout group at this position
                    .addComponent(recentCard,  javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)) // Adds this component into the layout group at this position
                .addGap(10, 10, 10)) // Inserts a 10px gap between components
        ); // Closes the layout group or method call
        layout.setVerticalGroup( // Defines vertical component stacking for the outer frame layout
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING) // Groups components side-by-side (parallel / same row)
            .addGroup(layout.createSequentialGroup() // Nests a sub-group inside this layout group for more complex arrangement
                .addComponent(titlePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE) // Adds this component into the layout group at this position
                .addGap(10, 10, 10) // Inserts a 10px gap between components
                .addComponent(statsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE) // Adds this component into the layout group at this position
                .addGap(10, 10, 10) // Inserts a 10px gap between components
                .addComponent(chartCard,  javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE) // Adds this component into the layout group at this position
                .addGap(10, 10, 10) // Inserts a 10px gap between components
                .addComponent(recentCard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE) // Adds this component into the layout group at this position
                .addGap(10, 10, 10)) // Inserts a 10px gap between components
        ); // Closes the layout group or method call
    }// </editor-fold>//GEN-END:initComponents

    // ── Stat card builder ─────────────────────────────────────────────────────

    /**
     * Populates each of the 4 empty card panels (cardCustomers, cardRooms,
     * cardBookings, cardRevenue) with a title label + large value label.
     *
     * The value labels are saved as fields so refreshData() can update them
     * directly — no need to search the component tree.
     */
    private void buildStatCards() { // Populates each of the 4 empty stat card panels with a title and value label
        lblCustomersVal = fillCard(cardCustomers, "Total Customers",    new Color(0x1A, 0x73, 0xE8)); // Creates a custom colour using RGB values
        lblRoomsVal     = fillCard(cardRooms,     "Available Rooms",    new Color(0x00, 0x96, 0x88)); // Creates a custom colour using RGB values
        lblBookingsVal  = fillCard(cardBookings,  "Total Bookings",     new Color(0xFF, 0x8F, 0x00)); // Creates a custom colour using RGB values
        lblRevenueVal   = fillCard(cardRevenue,   "Total Revenue (LKR)",new Color(0x2E, 0x7D, 0x32)); // Creates a custom colour using RGB values
    } // Closes this code block (end of method, class, or inner class)

    /**
     * Adds a title label and a large bold value label to the given card panel.
     *
     * @param card   the empty JPanel to fill
     * @param title  text shown above the number
     * @param accent colour used for the value label
     * @return the value JLabel so the caller can update it later
     */
    private JLabel fillCard(JPanel card, String title, Color accent) { // Fills a stat card with a title label and a large value label, returns the value label
        card.setLayout(new BorderLayout()); // Sets the layout manager controlling how child components are positioned
        card.setBorder(BorderFactory.createCompoundBorder( // Applies a visual border or padding around this component
            BorderFactory.createLineBorder(new Color(230, 230, 230)), // Creates a custom colour using RGB values
            BorderFactory.createEmptyBorder(14, 16, 14, 16))); // Creates invisible padding border around the panel edges

        JLabel lblTitle = new JLabel(title); // Creates the stat card title label (e.g. "Total Customers") using the passed-in title
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12)); // Sets the font typeface, style, and size for this component
        lblTitle.setForeground(new Color(95, 107, 124)); // Sets the text (foreground) colour of this component

        JLabel lblValue = new JLabel("...");   // placeholder until data loads
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 26)); // Sets the font typeface, style, and size for this component
        lblValue.setForeground(accent); // Sets the text (foreground) colour of this component

        JPanel inner = new JPanel(new GridLayout(2, 1, 0, 6)); // Creates a GridLayout — arranges components in equal-sized grid cells
        inner.setOpaque(false); // Makes this component transparent so background shows through
        inner.add(lblTitle); // Adds lblTitle into this container panel
        inner.add(lblValue); // Adds lblValue into this container panel

        card.add(inner, BorderLayout.CENTER); // Adds inner into this container panel
        return lblValue; // caller saves this reference
    } // Closes this code block (end of method, class, or inner class)

    // ── Chart builder ─────────────────────────────────────────────────────────

    /**
     * Embeds the custom-painted bar chart into the chartCard panel.
     * The chart reads monthlyRevenue[] each time it is repainted.
     */
    private void buildChart() { // Creates and embeds the custom bar chart panel into the chart card
        chartPanel = new JPanel() { // Creates a new empty panel container
            private final String[] MONTHS = { // Array of short month names used as labels below each chart bar
                "Jan","Feb","Mar","Apr","May","Jun", // Array of short month names used as labels below each chart bar
                "Jul","Aug","Sep","Oct","Nov","Dec" // Second half of month name abbreviations (July through December)
            }; // Closes this anonymous class or array definition

            @Override // Signals this method overrides a method from the parent class or interface
            protected void paintComponent(Graphics g) { // Overrides paint method to draw custom shapes/colours on this panel
                super.paintComponent(g); // Calls parent paint first to render the standard background colour
                Graphics2D g2 = (Graphics2D) g.create(); // Creates a 2D graphics context used to draw shapes and text on this panel
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // Enables anti-aliasing for smooth curve and text rendering

                int w = getWidth(), h = getHeight(); // Declares int variable "w" with initial value
                if (w < 10 || h < 10) { g2.dispose(); return; } // Closes this window and releases all its memory resources

                int barCount = 12; // Declares int variable "barCount" with initial value
                int availW   = w - 40; // Declares int variable "availW" with initial value
                int slotW    = availW / barCount; // Declares int variable "slotW" with initial value
                int barW     = Math.max(4, slotW - 6); // Returns the larger value — prevents negative balance from showing
                int maxH     = h - 45; // Declares int variable "maxH" with initial value

                // Find the highest monthly value for scaling
                double maxVal = 0; // Declares double variable "maxVal" with initial value
                for (double v : monthlyRevenue) if (v > maxVal) maxVal = v; // Scans all monthly values to find the maximum for chart scaling

                if (maxVal == 0) { // No data yet — skip drawing bars and show a message instead
                    // No data yet – show a friendly message
                    g2.setColor(new Color(95, 107, 124)); // Sets the current drawing colour for subsequent fill or draw operations
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 12)); // Sets the font typeface, style, and size for this component
                    g2.drawString("No revenue data for 2026 yet.", 20, h / 2); // Draws text at the specified x/y screen coordinates
                    g2.dispose(); // Closes this window and releases all its memory resources
                    return; // Exits the method immediately without executing further code
                } // Closes this code block (end of method, class, or inner class)

                // Colour palette – one colour per month
                Color[] palette = { // Array of 12 distinct colours — one assigned to each monthly bar
                    new Color(0x1A,0x73,0xE8), new Color(0x00,0x96,0x88), // Creates a custom colour using RGB values
                    new Color(0xFF,0x8F,0x00), new Color(0x2E,0x7D,0x32), // Creates a custom colour using RGB values
                    new Color(0x8E,0x24,0xAA), new Color(0x42,0x85,0xF4), // Creates a custom colour using RGB values
                    new Color(0x1A,0x73,0xE8), new Color(0x00,0x96,0x88), // Creates a custom colour using RGB values
                    new Color(0xFF,0x8F,0x00), new Color(0x2E,0x7D,0x32), // Creates a custom colour using RGB values
                    new Color(0x8E,0x24,0xAA), new Color(0x42,0x85,0xF4) // Creates a custom colour using RGB values
                }; // Closes this anonymous class or array definition

                // Baseline
                g2.setColor(new Color(0xE0, 0xE7, 0xFF)); // Sets the current drawing colour for subsequent fill or draw operations
                g2.drawLine(20, h - 28, w - 20, h - 28); // Draws a straight line between two coordinate points

                for (int i = 0; i < barCount; i++) { // Loops through 12 months (0=Jan to 11=Dec) to draw one bar each
                    int x    = 20 + i * slotW + (slotW - barW) / 2; // Declares int variable "x" with initial value
                    int barH = (int)((monthlyRevenue[i] / maxVal) * maxH); // Declares int variable "barH" with initial value
                    if (barH < 2 && monthlyRevenue[i] > 0) barH = 2; // Ensures very small values still draw a visible minimum bar
                    int y    = h - 28 - barH; // Declares int variable "y" with initial value

                    // Subtle shadow
                    g2.setColor(new Color(0, 0, 0, 20)); // Sets the current drawing colour for subsequent fill or draw operations
                    g2.fillRoundRect(x + 2, y + 2, barW, barH, 6, 6); // Draws and fills a rounded rectangle (bar or card background)

                    // Bar
                    g2.setColor(palette[i]); // Sets the current drawing colour for subsequent fill or draw operations
                    g2.fillRoundRect(x, y, barW, barH, 6, 6); // Draws and fills a rounded rectangle (bar or card background)

                    // Month label below bar
                    g2.setColor(new Color(95, 107, 124)); // Sets the current drawing colour for subsequent fill or draw operations
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 9)); // Sets the font typeface, style, and size for this component
                    FontMetrics fm = g2.getFontMetrics(); // Gets font measurement data so we can calculate text width for centering
                    g2.drawString(MONTHS[i], x + (barW - fm.stringWidth(MONTHS[i])) / 2, h - 12); // Draws text at the specified x/y screen coordinates

                    // Value label above bar
                    if (monthlyRevenue[i] > 0) { // Checks condition and executes block if true
                        g2.setColor(new Color(33, 37, 41)); // Sets the current drawing colour for subsequent fill or draw operations
                        g2.setFont(new Font("Segoe UI", Font.BOLD, 8)); // Sets the font typeface, style, and size for this component
                        String val = formatShort(monthlyRevenue[i]); // Formatted string value for "val"
                        FontMetrics fm2 = g2.getFontMetrics(); // Gets font measurement data so we can calculate text width for centering
                        g2.drawString(val, x + (barW - fm2.stringWidth(val)) / 2, y - 3); // Draws text at the specified x/y screen coordinates
                    } // Closes this code block (end of method, class, or inner class)
                } // Closes this code block (end of method, class, or inner class)
                g2.dispose(); // Closes this window and releases all its memory resources
            } // Closes this code block (end of method, class, or inner class)
        }; // Closes this anonymous class or array definition

        chartPanel.setOpaque(false); // Makes this component transparent so background shows through
        // Replace chartCard's layout to use BorderLayout and embed the chart
        chartCard.setLayout(new BorderLayout(0, 0)); // Sets the layout manager controlling how child components are positioned
        chartCard.add(chartPanel, BorderLayout.CENTER); // Adds chartPanel into this container panel
    } // Closes this code block (end of method, class, or inner class)

    // ── Recent bookings table setup ───────────────────────────────────────────

    /**
     * Wires up the DefaultTableModel on tblRecent.
     * Called once from the constructor after initComponents().
     */
    private void setupRecentTable() { // Configures the recent bookings table: columns, header font and colours
        recentTableModel = new DefaultTableModel( // Creates the table model for the recent bookings table with 5 display columns
            new String[]{"ID", "Customer", "Room", "Check-In", "Status"}, 0) { // Creates an array of values representing one table row of data
            @Override public boolean isCellEditable(int r, int c) { return false; } // Returns false — table cell cannot be edited by the user
        }; // Closes this anonymous class or array definition
        tblRecent.setModel(recentTableModel); // Connects the data model to this table so rows appear in the UI
        tblRecent.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12)); // Sets the font typeface, style, and size for this component
        tblRecent.getTableHeader().setBackground(new Color(0xF0, 0xF4, 0xF8)); // Sets the background fill colour of this component
        tblRecent.setSelectionBackground(new Color(0xBB, 0xDE, 0xFF)); // Sets the highlight colour shown on the selected table row
    } // Closes this code block (end of method, class, or inner class)

    // ── Data refresh ──────────────────────────────────────────────────────────

    /**
     * Loads live data from the database and updates:
     * - The 4 stat card values
     * - The monthly revenue array (triggers chart repaint)
     * - The recent bookings table (last 8 bookings)
     */
    public void refreshData() { // Fetches live data from DB and updates all 4 stat cards, chart, and recent table
        try { // Attempts the following database operations — errors caught below
            // Update the 4 stat card labels directly via saved field references
            lblCustomersVal.setText(String.valueOf(customerCtrl.getTotalCustomers())); // Sets the display text of this component
            lblRoomsVal.setText(String.valueOf(roomCtrl.getTotalAvailableRooms())); // Sets the display text of this component
            lblBookingsVal.setText(String.valueOf(bookingCtrl.getTotalBookings())); // Sets the display text of this component
            lblRevenueVal.setText("LKR " + new DecimalFormat("#,###").format(bookingCtrl.getTotalRevenue())); // Sets the display text of this component

            // Update chart data and repaint
            monthlyRevenue = bookingCtrl.getMonthlyRevenue(); // Fetches the 12-element monthly revenue array from the database
            if (chartPanel != null) { // Checks for null to prevent NullPointerException
                chartPanel.revalidate(); // Asks layout manager to recalculate positions after a change
                chartPanel.repaint(); // Requests this component to redraw itself on the screen
            } // Closes this code block (end of method, class, or inner class)

            // Reload recent bookings table
            recentTableModel.setRowCount(0); // Clears all existing rows from the table before reloading fresh data
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); // Creates a date formatter to convert Date objects to "dd/MM/yyyy" strings
            List<Booking> recent = bookingCtrl.getRecentBookings(8); // Fetches the 8 most recent bookings from the database for the dashboard table
            for (Booking b : recent) { // Loops through each booking record from the database
                recentTableModel.addRow(new Object[]{ // Adds one data record as a new visible row in the table
                    "BK-" + b.getBookingId(), // Gets the booking ID linked to this payment
                    b.getCustomerName(), // Gets the customer full name from the booking JOIN result
                    "Rm " + b.getRoomNumber(), // Gets the room number string from the booking JOIN result
                    b.getCheckinDate() != null ? sdf.format(b.getCheckinDate()) : "", // Converts the Date to a readable "dd/MM/yyyy" string for table display
                    b.getStatus().name() // Gets the enum value as a plain String (e.g. CONFIRMED, CASH, ACTIVE)
                }); // Closes the anonymous listener class and the addListener call
            } // Closes this code block (end of method, class, or inner class)
        } catch (SQLException ex) { // Catches database errors and shows an error message to the user
            ex.printStackTrace(); // Prints the full error stack trace to the console for debugging
        } // Closes this code block (end of method, class, or inner class)
    } // Closes this code block (end of method, class, or inner class)

    /** Called by MainFrame each time the user navigates to the Dashboard. */
    @Override // Signals this method overrides a method from the parent class or interface
    public void refresh() { refreshData(); } // Called by MainFrame when user navigates here — reloads all data from database

    // ── Event handlers ────────────────────────────────────────────────────────

    private void tblRecentMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblRecentMouseClicked
        // Reserved for future drill-down navigation
    }//GEN-LAST:event_tblRecentMouseClicked

    // ── Utility ───────────────────────────────────────────────────────────────

    /** Formats a large number compactly: 85000 → "85k". */
    private String formatShort(double v) { // Formats large numbers compactly: 85000 becomes "85k"
        if (v >= 1000) return new DecimalFormat("#.#k").format(v / 1000.0); // Value is 1000+ so show abbreviated format with "k" suffix
        return new DecimalFormat("#").format(v); // Returns the value: new DecimalFormat("#").format(v)
    } // Closes this code block (end of method, class, or inner class)

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel cardBookings; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JPanel cardCustomers; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JPanel cardRevenue; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JPanel cardRooms; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JPanel chartCard; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JScrollPane jScrollPane1; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JLabel lblSubtitle; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JLabel lblTitle; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JPanel recentCard; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JPanel statsPanel; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JTable tblRecent; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JPanel titlePanel; // Declares a private field variable (value assigned later during initialisation)
    // End of variables declaration//GEN-END:variables
} // Closes this code block (end of method, class, or inner class)
