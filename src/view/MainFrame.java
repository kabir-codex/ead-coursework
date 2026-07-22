package view; // Declares this file belongs to the "view" package

import model.User; // Imports User data model (POJO)

import javax.swing.*; // Imports all Swing UI components (JPanel, JButton, JLabel, etc.)
import javax.swing.border.EmptyBorder; // Imports EmptyBorder Swing component
import java.awt.*; // Imports AWT classes (Color, Font, Graphics, Layout managers)
import java.awt.event.*; // Imports * from AWT

/**
 * MainFrame – Application shell.
 * Contains the collapsible sidebar, top header bar, and a CardLayout
 * content area that swaps between module panels.
 */
public class MainFrame extends JFrame { // Declares MainFrame as a JFrame (a standalone application window)

    // ── State ──────────────────────────────────────────────────────────────────
    private final User currentUser; // Stores the logged-in User object passed from LoginForm

    // ── Layout ────────────────────────────────────────────────────────────────
    private CardLayout cardLayout; // Layout manager that switches between the 7 module panels

    // ── Nav buttons ───────────────────────────────────────────────────────────
    private JButton activeNavBtn; // Tracks which sidebar button is currently highlighted in blue

    // ── Card keys ─────────────────────────────────────────────────────────────
    private static final String CARD_DASHBOARD  = "DASHBOARD"; // String key "DASHBOARD" used to show this panel in the CardLayout
    private static final String CARD_CUSTOMERS  = "CUSTOMERS"; // String key "CUSTOMERS" used to show this panel in the CardLayout
    private static final String CARD_PACKAGES   = "PACKAGES"; // String key "PACKAGES" used to show this panel in the CardLayout
    private static final String CARD_ROOMS      = "ROOMS"; // String key "ROOMS" used to show this panel in the CardLayout
    private static final String CARD_BOOKINGS   = "BOOKINGS"; // String key "BOOKINGS" used to show this panel in the CardLayout
    private static final String CARD_PAYMENTS   = "PAYMENTS"; // String key "PAYMENTS" used to show this panel in the CardLayout
    private static final String CARD_REPORTS    = "REPORTS"; // String key "REPORTS" used to show this panel in the CardLayout

    // ── Constructor ───────────────────────────────────────────────────────────

    public MainFrame(User user) { // Constructor — receives User user and initialises this panel
        this.currentUser = user; // Stores the user parameter into the "currentUser" instance field
        initComponents(); // Calls NetBeans-generated method that builds and wires all UI components
        setLocationRelativeTo(null); // Centers this window in the middle of the screen

        // Show dashboard by default
        showCard(CARD_DASHBOARD); // Switches the main content area to show the specified panel
        setVisible(true); // Makes this window visible — shows it to the user on screen
    } // Closes this code block (end of method, class, or inner class)

    // ── UI Build (NetBeans-style initComponents) ─────────────────────────────
    // Note: the sidebar, header (with live clock) and CardLayout content area
    // use custom Graphics2D painting for hover effects and rounded styling,
    // which the Form Editor's Design view represents as a single composite
    // panel placeholder. The overall frame structure (title, size, BorderLayout
    // regions) is fully form-editable.

    @SuppressWarnings("unchecked") // Tells compiler to suppress unchecked type cast warnings
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() { // NetBeans auto-generated method — creates and configures all UI components
        setTitle("TourEase – Hotel & Tourism Management  |  " + currentUser.getFullName()); // Sets the text shown in the title bar of the window
        setDefaultCloseOperation(EXIT_ON_CLOSE); // Defines what happens when user clicks the X close button
        setSize(1280, 780); // Sets the initial width and height of the window in pixels
        setMinimumSize(new Dimension(1024, 650)); // Sets the minimum window size to prevent it from being shrunk too small

        getContentPane().setLayout(new BorderLayout()); // Sets the layout manager controlling how child components are positioned
        getContentPane().setBackground(UITheme.BG_PAGE); // Sets the background fill colour of this component

        sidebarPanel = buildSidebar(); // Builds and stores the sidebarPanel by calling buildSidebar()
        headerPanel  = buildHeader(); // Builds and stores the headerPanel by calling buildHeader()
        contentPanel = buildContent(); // Builds and stores the contentPanel by calling buildContent()

        getContentPane().add(sidebarPanel, BorderLayout.WEST); // Adds a panel to the window in the specified region
        getContentPane().add(headerPanel,  BorderLayout.NORTH); // Adds a panel to the window in the specified region
        getContentPane().add(contentPanel, BorderLayout.CENTER); // Adds a panel to the window in the specified region
    } // Closes this code block (end of method, class, or inner class)
    // </editor-fold>//GEN-END:initComponents

    // ── Sidebar ───────────────────────────────────────────────────────────────

    private JPanel buildSidebar() { // Builds the dark left-side navigation panel with logo, menu buttons and role badge
        JPanel sidebar = new JPanel(); // Creates a new empty panel container
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS)); // Sets the layout manager controlling how child components are positioned
        sidebar.setBackground(UITheme.BG_SIDEBAR); // Sets the background fill colour of this component
        sidebar.setPreferredSize(new Dimension(UITheme.SIDEBAR_WIDTH, 0)); // Sets the preferred (default) width and height of this component
        sidebar.setBorder(new EmptyBorder(0, 0, 0, 0)); // Applies a visual border or padding around this component

        // Logo area
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 18)); // Creates a FlowLayout — components flow left to right, wrapping as needed
        logoPanel.setBackground(UITheme.PRIMARY_DARK); // Sets the background fill colour of this component
        logoPanel.setMaximumSize(new Dimension(UITheme.SIDEBAR_WIDTH, 70)); // Sets the maximum allowed size so this component does not grow too large
        JLabel logoIco = new JLabel("🏨"); // Creates a label using an emoji as an icon
        logoIco.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26)); // Sets the font typeface, style, and size for this component
        JLabel logoTxt = new JLabel("TourEase"); // Creates a label with text: "TourEase"
        logoTxt.setFont(new Font("Segoe UI", Font.BOLD, 18)); // Sets the font typeface, style, and size for this component
        logoTxt.setForeground(Color.WHITE); // Sets the text (foreground) colour of this component
        logoPanel.add(logoIco); // Adds logoIco into this container panel
        logoPanel.add(logoTxt); // Adds logoTxt into this container panel
        sidebar.add(logoPanel); // Adds this component to the vertical sidebar panel

        sidebar.add(Box.createVerticalStrut(10)); // Inserts a fixed-height vertical gap in the sidebar

        // Navigation items
        String[][] navItems = { // Array of navigation menu item labels shown in the sidebar
            {"🏠",  "Dashboard",   CARD_DASHBOARD}, // Sidebar nav item: icon="🏠", label="Dashboard", cardKey=CARD_DASHBOARD
            {"👥",  "Customers",   CARD_CUSTOMERS}, // Sidebar nav item: icon="👥", label="Customers", cardKey=CARD_CUSTOMERS
            {"🗺️",  "Packages",    CARD_PACKAGES}, // Sidebar nav item: icon="🗺️", label="Packages", cardKey=CARD_PACKAGES
            {"🛏️",  "Rooms",       CARD_ROOMS}, // Sidebar nav item: icon="🛏️", label="Rooms", cardKey=CARD_ROOMS
            {"📋",  "Bookings",    CARD_BOOKINGS}, // Sidebar nav item: icon="📋", label="Bookings", cardKey=CARD_BOOKINGS
            {"💳",  "Payments",    CARD_PAYMENTS}, // Sidebar nav item: icon="💳", label="Payments", cardKey=CARD_PAYMENTS
            {"📄",  "Reports",     CARD_REPORTS}, // Sidebar nav item: icon="📄", label="Reports", cardKey=CARD_REPORTS
        }; // Closes this anonymous class or array definition

        for (String[] item : navItems) { // Loops through each navigation menu item to create a sidebar button
            JButton btn = createNavButton(item[0], item[1], item[2]); // Creates a styled sidebar nav button from the current navigation item data
            sidebar.add(btn); // Adds this component to the vertical sidebar panel
        } // Closes this code block (end of method, class, or inner class)

        sidebar.add(Box.createVerticalGlue()); // Pushes the role badge to the bottom of the sidebar

        // Role badge at bottom
        JPanel roleBadge = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 12)); // Creates a FlowLayout — components flow left to right, wrapping as needed
        roleBadge.setBackground(new Color(0x0D, 0x1A, 0x2D)); // Sets the background fill colour of this component
        roleBadge.setMaximumSize(new Dimension(UITheme.SIDEBAR_WIDTH, 64)); // Sets the maximum allowed size so this component does not grow too large
        JLabel roleIco = new JLabel("👤"); // Creates a label using an emoji as an icon
        roleIco.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20)); // Sets the font typeface, style, and size for this component
        JPanel roleText = new JPanel(); // Creates a new empty panel container
        roleText.setLayout(new BoxLayout(roleText, BoxLayout.Y_AXIS)); // Sets the layout manager controlling how child components are positioned
        roleText.setOpaque(false); // Makes this component transparent so background shows through
        JLabel roleName = new JLabel(currentUser.getFullName()); // Gets the full display name of this user/customer
        roleName.setFont(new Font("Segoe UI", Font.BOLD, 12)); // Sets the font typeface, style, and size for this component
        roleName.setForeground(Color.WHITE); // Sets the text (foreground) colour of this component
        JLabel roleType = new JLabel(currentUser.getRole().name()); // Gets the enum value as a plain String (e.g. CONFIRMED, CASH, ACTIVE)
        roleType.setFont(UITheme.FONT_SMALL); // Sets the font typeface, style, and size for this component
        roleType.setForeground(UITheme.TEXT_SIDEBAR); // Sets the text (foreground) colour of this component
        roleText.add(roleName); // Adds roleName into this container panel
        roleText.add(roleType); // Adds roleType into this container panel
        roleBadge.add(roleIco); // Adds roleIco into this container panel
        roleBadge.add(roleText); // Adds roleText into this container panel
        sidebar.add(roleBadge); // Adds this component to the vertical sidebar panel

        return sidebar; // Returns the completed sidebar panel to the caller (MainFrame)
    } // Closes this code block (end of method, class, or inner class)

    private JButton createNavButton(String icon, String label, String cardKey) { // Creates a styled sidebar button with hover and active-highlight painting
        JButton btn = new JButton(icon + "  " + label) { // Creates a new JButton using an anonymous subclass for custom painting
            @Override // Signals this method overrides a method from the parent class or interface
            protected void paintComponent(Graphics g) { // Overrides paint method to draw custom shapes/colours on this panel
                Graphics2D g2 = (Graphics2D) g.create(); // Creates a 2D graphics context used to draw shapes and text on this panel
                if (this == activeNavBtn) { // Checks condition and executes block if true
                    g2.setColor(UITheme.PRIMARY); // Sets the current drawing colour for subsequent fill or draw operations
                    g2.fillRect(0, 0, getWidth(), getHeight()); // Draws and fills a solid rectangle
                    g2.setColor(UITheme.PRIMARY_LIGHT); // Sets the current drawing colour for subsequent fill or draw operations
                    g2.fillRect(0, 0, 4, getHeight()); // Draws and fills a solid rectangle
                } else if (getModel().isRollover()) { // Checks a second condition if the first was false
                    g2.setColor(UITheme.BG_SIDEBAR_HOV); // Sets the current drawing colour for subsequent fill or draw operations
                    g2.fillRect(0, 0, getWidth(), getHeight()); // Draws and fills a solid rectangle
                } else { // Runs this block when none of the above conditions were true
                    g2.setColor(UITheme.BG_SIDEBAR); // Sets the current drawing colour for subsequent fill or draw operations
                    g2.fillRect(0, 0, getWidth(), getHeight()); // Draws and fills a solid rectangle
                } // Closes this code block (end of method, class, or inner class)
                g2.dispose(); // Closes this window and releases all its memory resources
                super.paintComponent(g); // Calls parent paint first to render the standard background colour
            } // Closes this code block (end of method, class, or inner class)
        }; // Closes this anonymous class or array definition
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13)); // Sets the font typeface, style, and size for this component
        btn.setForeground(UITheme.TEXT_SIDEBAR); // Sets the text (foreground) colour of this component
        btn.setHorizontalAlignment(SwingConstants.LEFT); // Aligns the button text to the left side
        btn.setBorderPainted(false); // Removes the drawn border from this button
        btn.setContentAreaFilled(false); // Removes the default button background fill
        btn.setFocusPainted(false); // Removes the dotted focus rectangle shown when button is focused
        btn.setMaximumSize(new Dimension(UITheme.SIDEBAR_WIDTH, 48)); // Sets the maximum allowed size so this component does not grow too large
        btn.setPreferredSize(new Dimension(UITheme.SIDEBAR_WIDTH, 48)); // Sets the preferred (default) width and height of this component
        btn.setBorder(new EmptyBorder(0, 20, 0, 0)); // Applies a visual border or padding around this component
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Changes cursor to a pointing hand to show this is clickable

        btn.addActionListener(e -> { // Registers a listener that fires when this button or field is activated
            JButton previous = activeNavBtn; // Saves reference to the previously active button so it can be repainted
            activeNavBtn = btn; // Updates the active button tracker to the newly clicked button
            if (previous != null) previous.repaint(); // Checks for null to prevent NullPointerException
            showCard(cardKey); // Switches the main content area to show the specified panel
            btn.repaint(); // Requests this component to redraw itself on the screen
        }); // Closes the anonymous listener class and the addListener call

        btn.addMouseListener(new MouseAdapter() { // Registers a listener that fires on mouse click events
            @Override public void mouseEntered(MouseEvent e) { btn.repaint(); } // Requests this component to redraw itself on the screen
            @Override public void mouseExited(MouseEvent e)  { btn.repaint(); } // Requests this component to redraw itself on the screen
        }); // Closes the anonymous listener class and the addListener call

        return btn; // Returns the fully configured button
    } // Closes this code block (end of method, class, or inner class)

    // ── Header ────────────────────────────────────────────────────────────────

    private JPanel buildHeader() { // Builds the top header bar with app title, live clock and logout button
        JPanel header = new JPanel(new BorderLayout()); // Creates a BorderLayout — positions components N/S/E/W/Centre
        header.setBackground(Color.WHITE); // Sets the background fill colour of this component
        header.setPreferredSize(new Dimension(0, UITheme.HEADER_HEIGHT)); // Sets the preferred (default) width and height of this component
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xE0, 0xE7, 0xFF))); // Applies a visual border or padding around this component

        JLabel title = new JLabel("  TourEase Hotel & Tourism Management System"); // Creates a label with text: "  TourEase Hotel & Tourism Management System"
        title.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Sets the font typeface, style, and size for this component
        title.setForeground(UITheme.PRIMARY); // Sets the text (foreground) colour of this component

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 14)); // Creates a FlowLayout — components flow left to right, wrapping as needed
        right.setOpaque(false); // Makes this component transparent so background shows through

        JLabel clock = new JLabel(); // Creates a blank label component with no text initially
        clock.setFont(UITheme.FONT_BODY); // Sets the font typeface, style, and size for this component
        clock.setForeground(UITheme.TEXT_SECONDARY); // Sets the text (foreground) colour of this component
        updateClock(clock); // Calls updateClock() to immediately show the time before the timer fires

        // Refresh clock every second
        new Timer(1000, e -> updateClock(clock)).start(); // Creates a timer that fires every 1000ms (1 second) to update the clock

        JButton btnLogout = UITheme.createRoundedButton("Logout", UITheme.ACCENT_RED, Color.WHITE); // Creates the red Logout button in the top header bar
        btnLogout.setPreferredSize(new Dimension(90, 32)); // Sets the preferred (default) width and height of this component
        btnLogout.addActionListener(e -> { // Registers a listener that fires when this button or field is activated
            int choice = JOptionPane.showConfirmDialog(this, // Shows a Yes/No dialog and captures the user response
                    "Are you sure you want to logout?", "Confirm Logout", // Confirmation message asking user to confirm they want to log out
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE); // Shows a Yes/No question dialog for logout confirmation
            if (choice == JOptionPane.YES_OPTION) { // Proceeds only if user clicked Yes in the confirmation dialog
                dispose(); // Closes the current MainFrame window after logout
                SwingUtilities.invokeLater(LoginForm::new); // Schedules code to run on the Swing Event Dispatch Thread (prevents UI freezes)
            } // Closes this code block (end of method, class, or inner class)
        }); // Closes the anonymous listener class and the addListener call

        right.add(clock); // Adds clock into this container panel
        right.add(btnLogout); // Adds btnLogout into this container panel

        header.add(title, BorderLayout.WEST); // Adds title into this container panel
        header.add(right,  BorderLayout.EAST); // Adds right into this container panel
        return header; // Returns the completed header panel to the caller (MainFrame)
    } // Closes this code block (end of method, class, or inner class)

    private void updateClock(JLabel lbl) { // Updates the header clock label with the current date and time string
        java.time.LocalDateTime now = java.time.LocalDateTime.now(); // Gets the current local date and time for the clock display
        lbl.setText(now.format(java.time.format.DateTimeFormatter.ofPattern("EEE, dd MMM yyyy  HH:mm:ss"))); // Sets the display text of this component
    } // Closes this code block (end of method, class, or inner class)

    // ── Content area ──────────────────────────────────────────────────────────

    private JPanel buildContent() { // Creates the CardLayout panel and adds all 7 module panels to it
        cardLayout  = new CardLayout(); // Creates a CardLayout — shows one panel at a time, like a deck of cards
        contentPanel = new JPanel(cardLayout); // Creates a new panel container
        contentPanel.setBackground(UITheme.BG_PAGE); // Sets the background fill colour of this component

        contentPanel.add(new DashboardPanel(),   CARD_DASHBOARD); // Adds this module panel to the CardLayout so it can be shown by navigation
        contentPanel.add(new CustomerPanel(),         CARD_CUSTOMERS); // Adds this module panel to the CardLayout so it can be shown by navigation
        contentPanel.add(new PackagePanel(),          CARD_PACKAGES); // Adds this module panel to the CardLayout so it can be shown by navigation
        contentPanel.add(new RoomPanel(),             CARD_ROOMS); // Adds this module panel to the CardLayout so it can be shown by navigation
        contentPanel.add(new BookingPanel(),          CARD_BOOKINGS); // Adds this module panel to the CardLayout so it can be shown by navigation
        contentPanel.add(new PaymentPanel(),          CARD_PAYMENTS); // Adds this module panel to the CardLayout so it can be shown by navigation
        contentPanel.add(new ReportPanel(),           CARD_REPORTS); // Adds this module panel to the CardLayout so it can be shown by navigation

        return contentPanel; // Returns the completed content panel to the caller (MainFrame)
    } // Closes this code block (end of method, class, or inner class)

    // ── Navigation ────────────────────────────────────────────────────────────

    public void showCard(String key) { // Switches the CardLayout to show the given panel and calls its refresh() method
        cardLayout.show(contentPanel, key); // Switches the visible panel to the one registered under this key
        // refresh panel when navigating to it
        Component[] comps = contentPanel.getComponents(); // Gets all child components inside the content panel
        for (Component comp : comps) { // Loops through content panels to find the currently visible one
            if (comp.isVisible() && comp instanceof Refreshable) { // Checks if this panel is the one currently shown on screen
                ((Refreshable) comp).refresh(); // Casts to Refreshable and calls refresh() so the panel reloads its data
            } // Closes this code block (end of method, class, or inner class)
        } // Closes this code block (end of method, class, or inner class)
    } // Closes this code block (end of method, class, or inner class)

    public User getCurrentUser() { return currentUser; } // Returns the currently logged-in User object

    /** Implemented by panels that need to reload data when they become visible. */
    public interface Refreshable { // Defines the Refreshable interface that panels implement for auto-reload on navigation
        void refresh(); // Contract method — implementing panels must provide this to reload their data
    } // Closes this code block (end of method, class, or inner class)
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel rootPlaceholder; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JPanel sidebarPanel; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JPanel headerPanel; // Declares a private field variable (value assigned later during initialisation)
    private javax.swing.JPanel contentPanel; // Declares a private field variable (value assigned later during initialisation)
    // End of variables declaration//GEN-END:variables
} // Closes this code block (end of method, class, or inner class)
