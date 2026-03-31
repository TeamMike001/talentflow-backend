package LearnX.com.example.LearnX.Model;

import LearnX.com.example.LearnX.Enum.Role;
import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(nullable = false)
    private boolean isLocked = false;

    private LocalDateTime lastActiveAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private Role role;

    public User() {

    }

    public boolean isOnline() {
        if (lastActiveAt == null) return false;
        return lastActiveAt.isAfter(LocalDateTime.now().minusMinutes(15));
    }

    public String getLastSeenText() {
        if (lastActiveAt == null) return "Never";
        if (isOnline()) return "Online";

        java.time.Duration duration = java.time.Duration.between(lastActiveAt, LocalDateTime.now());

        long hours = duration.toHours();

        if (hours < 1) return "Last seen less than an hour ago";
        if (hours< 24) {
            return "Last seen today";
        } else if (duration.toDays() == 1) {
            return "Last seen yesterday";
        } else {
            return "Last seen " + lastActiveAt.toLocalDate();
        }
    }

    public User(Long id, String name, String email, String password, boolean enabled, boolean isLocked, boolean verified, LocalDateTime lastActiveAt, LocalDateTime createdAt, LocalDateTime updatedAt, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.enabled = enabled;
        this.isLocked = isLocked;
        this.lastActiveAt = lastActiveAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public LocalDateTime getLastActiveAt() {
        return lastActiveAt;
    }

    public void setLastActiveAt(LocalDateTime lastActiveAt) {
        this.lastActiveAt = lastActiveAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}