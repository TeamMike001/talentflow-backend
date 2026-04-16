package LearnX.com.example.LearnX.dtos;

import LearnX.com.example.LearnX.Enum.Role;
import java.time.LocalDateTime;

public record UserResponseDto(
        Long id,
        String name,
        String email,
        Role role,
        boolean online,
        String avatar,
        LocalDateTime lastActiveAt,
        String lastSeen
) {
    public String getDisplayName() {
        return name != null && !name.isEmpty() ? name : email;
    }

    public String getInitials() {
        if (name != null && !name.isEmpty()) {
            return String.valueOf(name.charAt(0)).toUpperCase();
        }
        return email != null ? String.valueOf(email.charAt(0)).toUpperCase() : "U";
    }
}