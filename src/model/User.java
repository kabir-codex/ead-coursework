package model; // Declares this class belongs to the model package (entity/data layer)

/**
 * Model – User
 * Represents an authenticated system user with a role.
 */
public class User { // Defines User model class

    public enum Role { ADMIN, RECEPTIONIST } // Enum representing user roles in the system

    private int    userId;    // Primary key (unique user ID)
    private String username;  // Login username
    private String password;  // Login password (should ideally be hashed in real systems)
    private String fullName;  // Full name of user
    private Role   role;      // Role of user (ADMIN or RECEPTIONIST)
    private String email;     // Email address of user
    private boolean active;   // Whether user account is active (1/true or 0/false in DB)

    // ── Constructors ──────────────────────────────────────────────────────────

    public User() {} // Default constructor (required for DAO mapping)

    public User(int userId, String username, String fullName, Role role, String email, boolean active) { // Parameterized constructor

        this.userId   = userId;   // Assign user ID
        this.username = username; // Assign username
        this.fullName = fullName; // Assign full name
        this.role     = role;     // Assign role
        this.email    = email;    // Assign email
        this.active   = active;   // Assign active status
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────
    // Encapsulation: controlled access to private fields

    public int getUserId() { return userId; } // Returns user ID
    public void setUserId(int userId) { this.userId = userId; } // Sets user ID

    public String getUsername() { return username; } // Returns username
    public void setUsername(String username) { this.username = username; } // Sets username

    public String getPassword() { return password; } // Returns password
    public void setPassword(String password) { this.password = password; } // Sets password

    public String getFullName() { return fullName; } // Returns full name
    public void setFullName(String fullName) { this.fullName = fullName; } // Sets full name

    public Role getRole() { return role; } // Returns role
    public void setRole(Role role) { this.role = role; } // Sets role

    public String getEmail() { return email; } // Returns email
    public void setEmail(String email) { this.email = email; } // Sets email

    public boolean isActive() { return active; } // Returns whether user is active
    public void setActive(boolean active) { this.active = active; } // Sets active status

    public boolean isAdmin() { return Role.ADMIN.equals(role); } // Helper method: checks if user is admin

    @Override
    public String toString() { // Custom string representation
        return fullName + " (" + role + ")"; // Example: "John Doe (ADMIN)"
    }
} // End of User class