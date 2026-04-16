// UserStatusDto.java
package LearnX.com.example.LearnX.dtos;

import java.time.LocalDateTime;

public record UserStatusDto(Long userId, String userName, String userAvatar, boolean isOnline, LocalDateTime lastSeen) {}