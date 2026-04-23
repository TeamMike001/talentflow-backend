package LearnX.com.example.LearnX.dtos;

import java.time.LocalDateTime;

public record UserStatusDto(
        Long id,
        String name,
        String email,
        String role,
        boolean online,
        LocalDateTime lastActiveAt,
        String avatarUrl
) {
    // Constructor for backward compatibility
    public UserStatusDto(Long id, String name, String email, String role, boolean online, LocalDateTime lastActiveAt) {
        this(id, name, email, role, online, lastActiveAt, null);
    }
}