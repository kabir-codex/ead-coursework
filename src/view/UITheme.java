package view; // Declares this class belongs to the view/UI layer package

import java.awt.*; // Imports AWT classes (Color, Font, Graphics, etc.)

/**
 * UITheme – Central colour palette, fonts, and dimension constants.
 *
 * Every Swing form imports this class so the entire application
 * shares a consistent, vibrant visual identity.
 */
public final class UITheme { // Final class so it cannot be extended

    // ── Brand colours ─────────────────────────────────────────────────────────

    public static final Color PRIMARY        = new Color(0x1A, 0x73, 0xE8); // Main brand blue
    public static final Color PRIMARY_DARK   = new Color(0x0D, 0x47, 0xA1); // Darker blue variant
    public static final Color PRIMARY_LIGHT  = new Color(0x42, 0xA5, 0xF5); // Lighter blue variant

    public static final Color ACCENT_TEAL    = new Color(0x00, 0x96, 0x88); // Teal accent color
    public static final Color ACCENT_AMBER   = new Color(0xFF, 0x8F, 0x00); // Amber accent color
    public static final Color ACCENT_GREEN   = new Color(0x2E, 0x7D, 0x32); // Green success color
    public static final Color ACCENT_RED     = new Color(0xC6, 0x28, 0x28); // Red danger/error color
    public static final Color ACCENT_PURPLE  = new Color(0x6A, 0x1B, 0x9A); // Purple accent color

    // ── Background & surface ──────────────────────────────────────────────────

    public static final Color BG_PAGE        = new Color(0xF0, 0xF4, 0xF8); // Light page background
    public static final Color BG_CARD        = Color.WHITE; // Card background color
    public static final Color BG_SIDEBAR     = new Color(0x1E, 0x27, 0x3A); // Dark sidebar background
    public static final Color BG_SIDEBAR_HOV = new Color(0x2D, 0x3E, 0x58); // Sidebar hover color
    public static final Color BG_HEADER      = new Color(0x1A, 0x73, 0xE8); // Header background color

    // ── Text ──────────────────────────────────────────────────────────────────

    public static final Color TEXT_PRIMARY   = new Color(0x21, 0x27, 0x2E); // Main text color
    public static final Color TEXT_SECONDARY = new Color(0x5F, 0x6B, 0x7C); // Secondary/dim text
    public static final Color TEXT_WHITE     = Color.WHITE; // White text
    public static final Color TEXT_SIDEBAR   = new Color(0xB0, 0xBE, 0xC5); // Sidebar text color

    // ── Status colours ────────────────────────────────────────────────────────

    public static final Color STATUS_CONFIRMED  = new Color(0x1B, 0x87, 0x3A); // Green (confirmed)
    public static final Color STATUS_PENDING    = new Color(0xFF, 0x8F, 0x00); // Orange (pending)
    public static final Color STATUS_CANCELLED  = new Color(0xC6, 0x28, 0x28); // Red (cancelled)
    public static final Color STATUS_COMPLETED  = new Color(0x1A, 0x73, 0xE8); // Blue (completed)

    // ── Fonts ─────────────────────────────────────────────────────────────────

    public static final Font FONT_TITLE    = new Font("Segoe UI", Font.BOLD,  22); // Main title font
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD,  15); // Subtitle font
    public static final Font FONT_BODY     = new Font("Segoe UI", Font.PLAIN, 13); // Normal text font
    public static final Font FONT_SMALL    = new Font("Segoe UI", Font.PLAIN, 11); // Small text font
    public static final Font FONT_SIDEBAR  = new Font("Segoe UI", Font.PLAIN, 13); // Sidebar font
    public static final Font FONT_BTN      = new Font("Segoe UI", Font.BOLD,  13); // Button font
    public static final Font FONT_CARD_VAL = new Font("Segoe UI", Font.BOLD,  28); // Large card value font

    // ── Sizes ─────────────────────────────────────────────────────────────────

    public static final int  SIDEBAR_WIDTH = 220; // Width of sidebar panel
    public static final int  HEADER_HEIGHT = 60;  // Height of header panel
    public static final int  CARD_ARC      = 16;  // Rounded corner radius for cards
    public static final int  BTN_ARC       = 10;  // Rounded corner radius for buttons

    // Prevent instantiation
    private UITheme() {} // Private constructor prevents object creation

    // ── Factory helpers ───────────────────────────────────────────────────────

    /**
     * Creates a stylised rounded button with hover support.
     *
     * @param text label text
     * @param bg background colour
     * @param fg foreground (text) colour
     * @return configured JButton
     */
    public static javax.swing.JButton createRoundedButton(String text, Color bg, Color fg) { // Factory method for styled button

        javax.swing.JButton btn = new javax.swing.JButton(text) { // Anonymous subclass of JButton

            @Override // Signals this method overrides a method from the parent class or interface
            protected void paintComponent(Graphics g) { // Custom paint logic for button
                Graphics2D g2 = (Graphics2D) g.create(); // Create graphics context copy

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // Smooth edges

                if (getModel().isPressed()) { // If button is pressed
                    g2.setColor(bg.darker()); // Darker shade
                } else if (getModel().isRollover()) { // If mouse hovers
                    g2.setColor(bg.brighter()); // Brighter shade
                } else { // Runs this block when none of the above conditions were true
                    g2.setColor(bg); // Normal state color
                } // Closes this code block (end of method, class, or inner class)

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), BTN_ARC, BTN_ARC); // Draw rounded rectangle
                g2.dispose(); // Release graphics resources

                super.paintComponent(g); // Paint default text on top
            } // Closes this code block (end of method, class, or inner class)
        }; // Closes this anonymous class or array definition

        btn.setForeground(fg); // Set text color
        btn.setFont(FONT_BTN); // Set button font
        btn.setFocusPainted(false); // Remove focus border
        btn.setBorderPainted(false); // Remove border
        btn.setContentAreaFilled(false); // Disable default background fill
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Set hand cursor on hover
        btn.setPreferredSize(new Dimension(130, 36)); // Set default button size

        return btn; // Return configured button
    } // Closes this code block (end of method, class, or inner class)

    /**
     * Returns a colour associated with a booking status string.
     */
    public static Color statusColor(String status) { // Maps status text to color

        if (status == null) return TEXT_SECONDARY; // Default color if null

        switch (status.toUpperCase()) { // Normalize string and switch

            case "CONFIRMED":  return STATUS_CONFIRMED; // Green
            case "PENDING":    return STATUS_PENDING;   // Orange
            case "CANCELLED":  return STATUS_CANCELLED; // Red
            case "COMPLETED":  return STATUS_COMPLETED; // Blue

            default:           return TEXT_SECONDARY;   // Fallback color
        } // Closes this code block (end of method, class, or inner class)
    } // Closes this code block (end of method, class, or inner class)
} // End of UITheme class
