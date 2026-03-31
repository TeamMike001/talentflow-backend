package LearnX.com.example.LearnX.dtos;

import LearnX.com.example.LearnX.Enum.Role;
import java.time.LocalDateTime;


public record UserResponseDto (
     Long id,
    String name,
    String email,
    Role role,
    boolean enabled,
    boolean isLocked,
    LocalDateTime lastActiveAt, String lastSeen){


}