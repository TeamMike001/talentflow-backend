package LearnX.com.example.LearnX.dtos;

import LearnX.com.example.LearnX.Enum.Role;

public class UserDto {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private String userAvatar;
    private boolean online;
    private String lastActiveAt;

    public UserDto() {}

    public UserDto(Long id, String name, String email, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public UserDto(Long id, String name, String email, Role role, String userAvatar, boolean online, String lastActiveAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.userAvatar = userAvatar;
        this.online = online;
        this.lastActiveAt = lastActiveAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getUserAvatar() { return userAvatar; }
    public void setUserAvatar(String userAvatar) { this.userAvatar = userAvatar; }

    public boolean isOnline() { return online; }
    public void setOnline(boolean online) { this.online = online; }

    public String getLastActiveAt() { return lastActiveAt; }
    public void setLastActiveAt(String lastActiveAt) { this.lastActiveAt = lastActiveAt; }
}