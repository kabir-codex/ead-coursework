package view; // Declares this file belongs to the "view" package

import report.ReportFactory; // Imports ReportFactory singleton for generating JasperReports

import javax.swing.*; // Imports all Swing UI components (JPanel, JButton, JLabel, etc.)
import javax.swing.border.EmptyBorder; // Imports EmptyBorder Swing component
import java.awt.*; // Imports AWT classes (Color, Font, Graphics, Layout managers)
import java.util.HashMap; // Imports HashMap for key-value storage

/**
 * ReportPanel – NetBeans Form-based Reports & Analytics screen.
 * Provides buttons to launch report generation.
 */
public class ReportPanel extends JPanel implements MainFrame.Refreshable { // Declares ReportPanel — extends JPanel and implements Refreshable for auto-reload

    public ReportPanel() { // No-arg constructor — called when this panel is first created by MainFrame
        initComponents(); // Calls NetBeans-generated method that builds and wires all UI components
    } // Closes this code block (end of method, class, or inner class)

    @SuppressWarnings("unchecked") // Tells compiler to suppress unchecked type cast warnings
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() { // NetBeans auto-generated method — creates and configures all UI components
        titlePanel = new javax.swing.JPanel(); // Creates new JPanel component named "titlePanel"
        lblTitle = new javax.swing.JLabel(); // Creates new JLabel component named "lblTitle"
        lblSubtitle = new javax.swing.JLabel(); // Creates new JLabel component named "lblSubtitle"

        cardsPanel = new javax.swing.JPanel(); // Creates new JPanel component named "cardsPanel"
        cardBookingSummary  = buildReportCard("📋", "Booking Summary Report", // Builds the Booking Summary report card with icon, title, description and button
                "Full list of all bookings with customer details, room info and revenue totals.", // Description text shown on the Booking Summary report card
                new Color(0x1A,0x73,0xE8), () -> generateReport(ReportFactory.ReportType.BOOKING_SUMMARY)); // Creates a custom colour using RGB values
        cardPackageRevenue  = buildReportCard("🗺️", "Tour Package Revenue Report", // Builds the Package Revenue report card with icon, title, description and button
                "Package-level revenue breakdown with booking counts and popularity analysis.", // Description text shown on the Package Revenue report card
                new Color(0x00,0x96,0x88), () -> generateReport(ReportFactory.ReportType.PACKAGE_REVENUE)); // Creates a custom colour using RGB values
        cardPaymentSummary  = buildReportCard("💰", "Payment Summary Report", // Builds the Payment Summary report card with icon, title, description and button
                "Daily/monthly payment totals broken down by payment method. (Coming soon)", // Description text for the Payment Summary report card
                new Color(0xFF,0x8F,0x00), () -> JOptionPane.showMessageDialog(this, // Shows a pop-up dialog box with the specified message and title
                        "Report coming soon!", "Info", JOptionPane.INFORMATION_MESSAGE)); // Shows a popup telling the user this report is not yet implemented
        cardCustomerAnalytics = buildReportCard("📊", "Customer Analytics Report", // Builds the Customer Analytics report card with icon, title, description and button
                "Customer demographics, nationality breakdown and booking frequency. (Coming soon)", // Description text for the Customer Analytics report card
                new Color(0x8E,0x24,0xAA), () -> JOptionPane.showMessageDialog(this, // Shows a pop-up dialog box with the specified message and title
                        "Report coming soon!", "Info", JOptionPane.INFORMATION_MESSAGE)); // Shows a popup telling the user this report is not yet implemented

        // ── Title ─────────────────────────────────────────────────────────────
        titlePanel.setBackground(new Color(13, 71, 161)); // Sets the background fill colour of this component
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20)); // Sets the font typeface, style, and size for this component
        lblTitle.setForeground(Color.WHITE); // Sets the text (foreground) colour of this component
        lblTitle.setText("Reports & Analytics"); // Sets visible text to: "Reports & Analytics"
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 11)); // Sets the font typeface, style, and size for this component
        lblSubtitle.setForeground(new Color(179, 229, 252)); // Sets the text (foreground) colour of this component
        lblSubtitle.setText("Generate professional management reports"); // Sets visible text to: "Generate professional management reports"

        GroupLayout titleLayout = new GroupLayout(titlePanel); // GroupLayout configuration for component sizing and positioning
        titlePanel.setLayout(titleLayout); // Sets the layout manager controlling how child components are positioned
        titleLayout.setHorizontalGroup(titleLayout.createSequentialGroup().addGap(20) // Applies horizontal layout rules to this panel
            .addGroup(titleLayout.createParallelGroup().addComponent(lblTitle).addComponent(lblSubtitle)) // Nests a sub-group inside this layout group for more complex arrangement
            .addContainerGap(Short.MAX_VALUE,Short.MAX_VALUE)); // Adds standard container margin gaps at the edges of the panel
        titleLayout.setVerticalGroup(titleLayout.createSequentialGroup().addGap(12) // Applies vertical layout rules to this panel
            .addComponent(lblTitle).addGap(4).addComponent(lblSubtitle).addGap(12)); // Places the lblTitle into this layout group

        // ── Cards grid ────────────────────────────────────────────────────────
        cardsPanel.setOpaque(false); // Makes this component transparent so background shows through
        cardsPanel.setLayout(new GridLayout(2, 2, 20, 20)); // Sets the layout manager controlling how child components are positioned
        cardsPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Applies a visual border or padding around this component
        cardsPanel.add(cardBookingSummary); // Adds this report card to the 2x2 grid layout
        cardsPanel.add(cardPackageRevenue); // Adds this report card to the 2x2 grid layout
        cardsPanel.add(cardPaymentSummary); // Adds this report card to the 2x2 grid layout
        cardsPanel.add(cardCustomerAnalytics); // Adds this report card to the 2x2 grid layout

        // ── Main layout ───────────────────────────────────────────────────────
        GroupLayout layout = new GroupLayout(this); // GroupLayout configuration for component sizing and positioning
        this.setLayout(layout); // Sets the layout manager controlling how child components are positioned
        layout.setHorizontalGroup(layout.createParallelGroup() // Starts a parallel group — components aligned on the same axis
            .addComponent(titlePanel,GroupLayout.DEFAULT_SIZE,GroupLayout.DEFAULT_SIZE,Short.MAX_VALUE) // Adds this component into the layout group at this position
            .addComponent(cardsPanel,GroupLayout.DEFAULT_SIZE,GroupLayout.DEFAULT_SIZE,Short.MAX_VALUE)); // Adds this component into the layout group at this position
        layout.setVerticalGroup(layout.createSequentialGroup() // Starts a sequential group — components placed one after another
            .addComponent(titlePanel,GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE,GroupLayout.PREFERRED_SIZE) // Adds this component into the layout group at this position
            .addComponent(cardsPanel,GroupLayout.DEFAULT_SIZE,GroupLayout.DEFAULT_SIZE,Short.MAX_VALUE)); // Adds this component into the layout group at this position

        setBackground(new Color(0xF8,0xF4,0xF0)); // Creates a custom colour using RGB values
    } // Closes this code block (end of method, class, or inner class)
    // </editor-fold>//GEN-END:initComponents

    // ── Report card builder ──────────────────────────────────────────────────

    private JPanel buildReportCard(String icon, String title, String description, // Method signature — builds one report card using icon emoji, title, description, accent colour and action
                                    Color accent, Runnable action) { // Continuation of buildReportCard parameters: accent colour and click action
        JPanel card = new JPanel(new BorderLayout(0, 12)) { // Creates a BorderLayout — positions components N/S/E/W/Centre
            @Override protected void paintComponent(Graphics g) { // Overrides paint to draw a rounded rectangle background for the card panel
                Graphics2D g2 = (Graphics2D) g.create(); // Creates a 2D graphics context used to draw shapes and text on this panel
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // Enables anti-aliasing for smooth curve and text rendering
                g2.setColor(Color.WHITE); // Sets the current drawing colour for subsequent fill or draw operations
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16); // Draws and fills a rounded rectangle (bar or card background)
                g2.setColor(accent); // Sets the current drawing colour for subsequent fill or draw operations
                g2.fillRoundRect(0, 0, getWidth(), 8, 8, 8); // Draws and fills a rounded rectangle (bar or card background)
                g2.fillRect(0, 4, getWidth(), 4); // Draws and fills a solid rectangle
                g2.dispose(); // Closes this window and releases all its memory resources
                super.paintComponent(g); // Calls parent paint first to render the standard background colour
            } // Closes this code block (end of method, class, or inner class)
        }; // Closes this anonymous class or array definition
        card.setOpaque(false); // Makes this component transparent so background shows through
        card.setBorder(new EmptyBorder(20, 20, 20, 20)); // Applies a visual border or padding around this component

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); // Creates a FlowLayout — components flow left to right, wrapping as needed
        header.setOpaque(false); // Makes this component transparent so background shows through
        JLabel ico = new JLabel(icon); // Creates the emoji icon label using the passed-in icon string
        ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32)); // Sets the font typeface, style, and size for this component
        JLabel lbl = new JLabel(title); // Creates the report title label using the passed-in title string
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15)); // Sets the font typeface, style, and size for this component
        lbl.setForeground(new Color(33,37,41)); // Sets the text (foreground) colour of this component
        header.add(ico); // Adds ico into this container panel
        header.add(lbl); // Adds lbl into this container panel

        JTextArea desc = new JTextArea(description); // Creates a multi-line text area for the report description text
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 12)); // Sets the font typeface, style, and size for this component
        desc.setForeground(new Color(95,107,124)); // Sets the text (foreground) colour of this component
        desc.setLineWrap(true); // Enables automatic line wrapping so text does not overflow
        desc.setWrapStyleWord(true); // Wraps at word boundaries so words are not split mid-character
        desc.setEditable(false); // Makes this text field read-only — user cannot type in it
        desc.setOpaque(false); // Makes this component transparent so background shows through
        desc.setBorder(null); // Applies a visual border or padding around this component

        JButton btn = new JButton("▶  Generate Report"); // Creates a new JButton using an anonymous subclass for custom painting
        btn.setBackground(accent); // Sets the background fill colour of this component
        btn.setForeground(Color.WHITE); // Sets the text (foreground) colour of this component
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12)); // Sets the font typeface, style, and size for this component
        btn.setOpaque(true); // Makes this component paint its own background colour
        btn.setBorderPainted(false); // Removes the drawn border from this button
        btn.setPreferredSize(new Dimension(200, 40)); // Sets the preferred (default) width and height of this component
        btn.addActionListener(e -> action.run()); // Registers a listener that fires when this button or field is activated

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Creates a FlowLayout — components flow left to right, wrapping as needed
        footer.setOpaque(false); // Makes this component transparent so background shows through
        footer.add(btn); // Adds btn into this container panel

        card.add(header, BorderLayout.NORTH); // Adds header into this container panel
        card.add(desc,   BorderLayout.CENTER); // Adds desc into this container panel
        card.add(footer, BorderLayout.SOUTH); // Adds footer into this container panel
        return card; // Returns the completed report card panel
    } // Closes this code block (end of method, class, or inner class)

    // ── Report generation ─────────────────────────────────────────────────────

    private void generateReport(ReportFactory.ReportType type) { // Runs report generation on a background thread so the UI stays responsive
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)); // Changes cursor to hourglass spinner to indicate loading in progress
        SwingWorker<Void, Void> worker = new SwingWorker<>() { // Creates a background worker thread so the UI stays responsive during generation
            @Override protected Void doInBackground() { // Runs the heavy report task on a background thread away from the UI thread
                ReportFactory.getInstance().generateReport(type, new HashMap<>()); // Calls ReportFactory to compile and display the report in a viewer window
                return null; // Returns null — record was not found or nothing to return
            } // Closes this code block (end of method, class, or inner class)
            @Override protected void done() { // Called on the UI thread after background report generation completes
                setCursor(Cursor.getDefaultCursor()); // Restores the default arrow cursor after the operation completes
            } // Closes this code block (end of method, class, or inner class)
        }; // Closes this anonymous class or array definition
        worker.execute(); // Starts the SwingWorker and begins background report generation
    } // Closes this code block (end of method, class, or inner class)

    @Override public void refresh() { /* nothing to refresh */ } // Contract method — implementing panels must provide this to reload their data

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel titlePanel, cardsPanel; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JLabel lblTitle, lblSubtitle; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JPanel cardBookingSummary, cardPackageRevenue, cardPaymentSummary, cardCustomerAnalytics; // Declares a private field variable (value assigned later during initialisation)
    // End of variables declaration//GEN-END:variables
} // Closes this code block (end of method, class, or inner class)
