package report; // Declares this file belongs to the "report" package

import util.DatabaseConnection; // Shared singleton JDBC connection to tourism_management_db

import net.sf.jasperreports.engine.JasperCompileManager; // Compiles a .jrxml design file into a .jasper compiled report
import net.sf.jasperreports.engine.JasperFillManager; // Fills a compiled report with data (here: a live DB Connection)
import net.sf.jasperreports.engine.JasperPrint; // Represents a filled, ready-to-view/print report
import net.sf.jasperreports.engine.JasperReport; // Represents a compiled .jasper report definition
import net.sf.jasperreports.engine.JRException; // Base exception type thrown by JasperReports operations
import net.sf.jasperreports.view.JasperViewer; // Swing window that displays a JasperPrint on screen

import javax.swing.JOptionPane; // Used to show error dialogs if report generation fails

import java.io.File; // Used to check file existence/timestamps for the .jrxml/.jasper pair
import java.sql.Connection; // JDBC connection passed into JasperReports as the data source
import java.sql.PreparedStatement; // Used to run the chart's own small data query directly
import java.sql.ResultSet; // Holds rows returned by the chart's own data query
import java.util.Map; // Report parameters map type
import java.util.HashMap; // Used to build the parameters map passed into fillReport
import java.awt.Image; // The chart image type expected by the .jrxml's image parameter
import java.text.SimpleDateFormat; // Formats checkin_date into category labels matching the chart's x-axis

import net.sf.jasperreports.engine.DefaultJasperReportsContext; // Global JasperReports config context, used to disable buggy XML schema validation below
import net.sf.jasperreports.engine.JasperExportManager; // Exports a filled JasperPrint straight to a PDF file on disk
import org.jfree.chart.ChartFactory; // Builds charts directly with JFreeChart, bypassing JasperReports' native chart bridge
import org.jfree.chart.JFreeChart; // Represents a built JFreeChart chart, ready to render to an image
import org.jfree.chart.plot.CategoryPlot; // Lets us style the plot background/orientation after building the chart
import org.jfree.chart.renderer.category.LineAndShapeRenderer; // Lets us set per-series line colors and shape visibility
import org.jfree.data.category.DefaultCategoryDataset; // Simple in-memory dataset for category/line/bar charts
import java.awt.Color; // Used to translate the original design's hex colors into AWT colors for styling

/**
 * ReportFactory – singleton responsible for compiling (and caching),
 * filling, and displaying JasperReports (.jrxml / .jasper) files that
 * live under the top-level "resources/reports" folder.
 */
public class ReportFactory { // Declares the ReportFactory class

    // ── Singleton plumbing ───────────────────────────────────────────────────
    private static ReportFactory instance; // Holds the single shared ReportFactory instance

    static { // Runs once when this class is first loaded, before any report is compiled
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug"); // Turns on verbose logging so JasperReports prints exactly what it's doing while parsing

        // JasperReports validates .jrxml files against its internal report schema before
        // loading them. That validation step has known false positives against the newer
        // v7 "compact" <element kind="..."> syntax (the kind Jaspersoft Studio 7.x
        // generates), causing a JRException("Unable to load report") with NO underlying
        // cause attached — impossible to diagnose from the stack trace alone. Since the
        // XML is otherwise well-formed and correct, we disable this pre-check here.
        DefaultJasperReportsContext.getInstance() // Gets the global JasperReports configuration context
                .setProperty("net.sf.jasperreports.compiler.xml.validation", "false"); // Disables the schema validation step before report loading
    } // Closes this code block

    private ReportFactory() { } // Private constructor — prevents external instantiation

    public static synchronized ReportFactory getInstance() { // Returns the shared singleton instance, creating it on first call
        if (instance == null) { // Checks whether the singleton has been created yet
            instance = new ReportFactory(); // Lazily creates the single ReportFactory instance
        } // Closes this code block
        return instance; // Returns the shared instance to the caller
    } // Closes this code block

    // ── Report registry ──────────────────────────────────────────────────────

    /**
     * Enum of all known report types. Each entry stores the base filename
     * (without extension) as it appears in resources/reports/.
     */
    public enum ReportType { // Declares the ReportType enum used to select which report to run
        BOOKING_SUMMARY("booking-summary_report"), // Maps to resources/reports/booking-summary_report.jrxml
        PACKAGE_REVENUE("package-revenue_report");  // Maps to resources/reports/package-revenue_report.jrxml

        private final String fileBaseName; // Stores the filename (no extension) for this report type

        ReportType(String fileBaseName) { // Enum constructor — assigns the base filename
            this.fileBaseName = fileBaseName; // Stores the passed-in base filename
        } // Closes this code block

        public String getFileBaseName() { // Getter for the base filename
            return fileBaseName; // Returns the base filename for this report type
        } // Closes this code block
    } // Closes this code block

    // Classpath location of the .jrxml report designs. build.xml already
    // copies the project's "resources" folder into build/classes/resources
    // at compile time, and the "jar" target packages all of build/classes
    // (including that resources folder) straight into TourEase.jar. That
    // means the .jrxml files are always available on the classpath — both
    // when running from NetBeans (build/classes/resources) and from the
    // packaged jar (resources/ is an entry inside the jar itself). Loading
    // them this way means report lookup no longer depends on the JVM's
    // working directory, or on any resources/ folder existing next to the
    // jar on disk.
    private static final String REPORTS_CLASSPATH_DIR = "/resources/reports/"; // Leading "/" = resolve from classpath root, not this class's package

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Compiles (if needed), fills with live DB data, and displays the given
     * report type in a JasperViewer window.
     *
     * @param type       which report to generate
     * @param parameters report parameters (pass an empty Map if none needed)
     */
    public void generateReport(ReportType type, Map<String, Object> parameters) { // Entry point called from ReportPanel
        try { // Wraps report generation so any failure shows a friendly dialog instead of crashing
            JasperReport jasperReport = getCompiledReport(type); // Compiles the .jrxml (or loads cached .jasper) for this report type

            Connection conn = DatabaseConnection.getInstance().getConnection(); // Borrows the app's shared singleton connection (do NOT close it here)

            Map<String, Object> allParams = new HashMap<>(parameters); // Copies the caller's parameters so we can add the chart image without mutating their map
            Image chartImage = buildChartImage(conn, type); // Builds the trend chart ourselves with JFreeChart directly, bypassing JasperReports' native (and currently broken) chart bridge
            if (chartImage != null) { // Only adds the parameter if chart building actually succeeded
                allParams.put("chartImage", chartImage); // Passes the pre-rendered chart image into the report as a parameter
            } // Closes this code block

            JasperPrint print = JasperFillManager.fillReport(jasperReport, allParams, conn); // Fills the report with live data from the DB

            String pdfPath = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + type.getFileBaseName() + ".pdf"; // Builds a path to save a debug copy on the Desktop
            JasperExportManager.exportReportToPdfFile(print, pdfPath); // Exports the filled report straight to a PDF file for reliable inspection
            System.out.println("PDF exported to: " + pdfPath); // Prints the saved PDF's location to the console

            JasperViewer.viewReport(print, false); // Opens the built-in JasperReports viewer window (false = don't exit app on close)

        } catch (JRException e) { // Catches compilation/fill errors from JasperReports
            System.err.println("=== JRException diagnostics ==="); // Marks the start of extra diagnostic output
            System.err.println("Message: " + e.getMessage()); // Prints the exception's message directly
            Throwable cause = e.getCause(); // Starts walking the cause chain manually
            int depth = 0; // Tracks how many levels deep we are in the cause chain
            while (cause != null && depth < 10) { // Walks up to 10 levels of nested causes
                System.err.println("Caused by [" + depth + "]: " + cause.getClass().getName() + ": " + cause.getMessage()); // Prints each nested cause's type and message
                cause = cause.getCause(); // Moves to the next nested cause
                depth++; // Increments the depth counter
            } // Closes this code block
            for (Throwable sup : e.getSuppressed()) { // Checks for any suppressed exceptions attached to this JRException
                System.err.println("Suppressed: " + sup.getClass().getName() + ": " + sup.getMessage()); // Prints each suppressed exception's type and message
            } // Closes this code block
            System.err.println("=== End diagnostics ==="); // Marks the end of extra diagnostic output
            e.printStackTrace(); // Logs the full stack trace to the console for debugging
            JOptionPane.showMessageDialog(null, // Shows a pop-up dialog box with the specified message and title
                    "Failed to generate report:\n" + e.getMessage(), // Displays the underlying JasperReports error message
                    "Report Error", JOptionPane.ERROR_MESSAGE); // Titles the dialog "Report Error" with an error icon
        } catch (Exception e) { // Catches anything else (e.g. SQLException from DBConnection.getConnection())
            e.printStackTrace(); // Logs the full stack trace to the console for debugging
            JOptionPane.showMessageDialog(null, // Shows a pop-up dialog box with the specified message and title
                    "Failed to generate report:\n" + e.getMessage(), // Displays the underlying error message
                    "Report Error", JOptionPane.ERROR_MESSAGE); // Titles the dialog "Report Error" with an error icon
        } // Closes this code block
    } // Closes this code block

    // ── Chart image building (bypasses JasperReports' native chart bridge) ────

    /**
     * Builds the trend chart for the given report type directly with
     * JFreeChart, returning a ready-to-embed Image. Returns null (and logs
     * a warning) if anything goes wrong, so a chart failure never blocks
     * the rest of the report from generating.
     */
    private Image buildChartImage(Connection conn, ReportType type) {
        try {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset(); // Holds the (series, category, value) triples for the chart
            SimpleDateFormat dateFmt = new SimpleDateFormat("dd MMM yyyy"); // Matches the category label format used in the original report design
            String title;
            Color titleColor;
            Color plotBackground;
            Color[] seriesColors;

            if (type == ReportType.BOOKING_SUMMARY) { // Booking Summary: revenue trend by check-in date, one line per room type
                title = "Booking Revenue Trend by Check-In Date & Room Type";
                titleColor = new Color(0x1A, 0x73, 0xE8); // Matches the original design's titleColor="#1A73E8"
                plotBackground = new Color(0xF5, 0xF9, 0xFF); // Matches the original design's plot backcolor="#F5F9FF"
                seriesColors = new Color[] { // Matches the original design's <seriesColor> palette, in order
                        new Color(0x1A, 0x73, 0xE8), // order 0
                        new Color(0xFF, 0x8F, 0x00), // order 1
                        new Color(0x1A, 0x73, 0x34), // order 2
                        new Color(0xD3, 0x2D, 0x2D), // order 3
                        new Color(0x8E, 0x24, 0xAA)  // order 4
                };
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT r.room_type, b.checkin_date, b.total_amount " +
                        "FROM bookings b JOIN rooms r ON b.room_id = r.room_id " +
                        "ORDER BY b.checkin_date");
                     ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String roomType = rs.getString("room_type"); // The series (one line per room type)
                        String category = dateFmt.format(rs.getDate("checkin_date")); // The x-axis category label
                        double amount = rs.getBigDecimal("total_amount").doubleValue(); // The y-axis value
                        dataset.addValue(amount, roomType, category); // Adds this data point to the chart
                    } // Closes this code block
                } // Closes the try-with-resources block, releasing the statement/result set

            } else if (type == ReportType.PACKAGE_REVENUE) { // Package Revenue: revenue by package, one line per package status
                title = "Package Revenue Trend by Status - Decision Insight";
                titleColor = new Color(0x00, 0x69, 0x5C); // Matches the original design's titleColor="#00695C"
                plotBackground = new Color(0xE0, 0xF2, 0xF1); // Matches the original design's plot backcolor="#E0F2F1"
                seriesColors = new Color[] { // Matches the original design's <seriesColor> palette, in order
                        new Color(0x00, 0x69, 0x5C), // order 0
                        new Color(0xFF, 0x8F, 0x00), // order 1
                        new Color(0x1A, 0x73, 0xE8), // order 2
                        new Color(0xD3, 0x2D, 0x2D)  // order 3
                };
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT p.package_name, p.status AS package_status, " +
                        "       COALESCE(SUM(py.amount), 0) AS total_revenue " +
                        "FROM packages p " +
                        "LEFT JOIN bookings b ON p.package_id = b.package_id AND b.status != 'CANCELLED' " +
                        "LEFT JOIN payments py ON b.booking_id = py.booking_id " +
                        "GROUP BY p.package_id, p.package_name, p.status " +
                        "ORDER BY p.package_name");
                     ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String status = rs.getString("package_status"); // The series (one line per package status)
                        String packageName = rs.getString("package_name"); // The x-axis category label
                        double revenue = rs.getBigDecimal("total_revenue").doubleValue(); // The y-axis value
                        dataset.addValue(revenue, status, packageName); // Adds this data point to the chart
                    } // Closes this code block
                } // Closes the try-with-resources block, releasing the statement/result set

            } else { // Any other report type currently has no chart defined
                return null; // Skips chart building entirely for report types that don't need one
            } // Closes this code block

            JFreeChart chart = ChartFactory.createLineChart(title, "", "Revenue (LKR)", dataset); // Builds the base line chart with JFreeChart
            chart.setBackgroundPaint(Color.WHITE); // Matches the original design's chart element backcolor="#FFFFFF"
            chart.getTitle().setPaint(titleColor); // Matches the original design's titleColor
            chart.getLegend().setBackgroundPaint(Color.WHITE); // Keeps the legend background clean/white like the original

            CategoryPlot plot = chart.getCategoryPlot(); // Gets the plot area so we can style its background and series colors
            plot.setBackgroundPaint(plotBackground); // Matches the original design's plot backcolor
            plot.setBackgroundAlpha(1.0f); // Matches the original design's plot backgroundAlpha="1.0"
            plot.setForegroundAlpha(0.9f); // Matches the original design's plot foregroundAlpha="0.9"

            LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer(); // Gets the renderer so we can set per-series line colors
            renderer.setDefaultShapesVisible(true); // Matches the original design's showShapes="true"
            for (int i = 0; i < dataset.getRowCount() && i < seriesColors.length; i++) { // Applies the original color palette to each series, in order
                renderer.setSeriesPaint(i, seriesColors[i]); // Sets this series' line/shape color to match the original design
            } // Closes this code block

            int imageHeight = (type == ReportType.PACKAGE_REVENUE) ? 200 : 196; // Matches each report's actual reserved chart element height
            return chart.createBufferedImage(782, imageHeight); // Renders it to a fixed-size image matching the space reserved in the .jrxml

        } catch (Exception e) { // Catches any DB/rendering error while building the chart
            System.err.println("Chart image build failed for " + type + ": " + e.getMessage()); // Logs the failure so it's visible without blocking the report
            e.printStackTrace(); // Prints the full stack trace for debugging
            return null; // Returns null so the report still generates, just without the chart image
        } // Closes this code block
    } // Closes this code block

    // ── Compilation ────────────────────────────────────────────────────────────

    /**
     * Loads and compiles the .jrxml for the given report type from the
     * classpath (works identically whether running from NetBeans, from
     * "ant run", or from the packaged TourEase.jar — no dependency on any
     * particular folder existing next to the jar on disk).
     *
     * Note: this recompiles the .jrxml on every call rather than caching a
     * .jasper file to disk. That's intentional — since the .jrxml now lives
     * inside the jar itself, there's no safe writable location right next
     * to it to cache a compiled copy. Compilation is fast enough (well
     * under a second for reports this size) that recompiling per-click is
     * not noticeable to the user.
     */
    private JasperReport getCompiledReport(ReportType type) throws JRException { // Loads/compiles the report definition for the given type
        String resourcePath = REPORTS_CLASSPATH_DIR + type.getFileBaseName() + ".jrxml"; // Builds the classpath-relative resource path, e.g. /resources/reports/booking-summary_report.jrxml

        try (java.io.InputStream jrxmlStream = ReportFactory.class.getResourceAsStream(resourcePath)) { // Opens the .jrxml as a classpath resource stream
            if (jrxmlStream == null) { // getResourceAsStream returns null (not an exception) when the resource isn't found
                throw new JRException("Report design not found on classpath: " + resourcePath // Fails fast with a clear message if the resource is missing
                        + " (check that build.xml's compile target is copying resources/ into build/classes/resources)");
            } // Closes this code block
            return JasperCompileManager.compileReport(jrxmlStream); // Compiles the .jrxml straight from the stream, no disk I/O needed
        } catch (java.io.IOException e) { // Thrown if closing the stream fails
            throw new JRException("Failed to read report design: " + resourcePath, e); // Wraps it as a JRException so callers only need to catch one exception type
        } // Closes this code block
    } // Closes this code block

} // Closes this code block (end of class)
