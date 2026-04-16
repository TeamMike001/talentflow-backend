package LearnX.com.example.LearnX.mapper;

import LearnX.com.example.LearnX.Enum.Role;
import LearnX.com.example.LearnX.Model.User;
import LearnX.com.example.LearnX.dtos.UserRegistrationDto;
import LearnX.com.example.LearnX.dtos.UserResponseDto;
import LearnX.com.example.LearnX.dtos.UserSummaryDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponseDto toResponseDto(User user) {
        // Generate avatar URL based on user's name and role
        String avatar = "https://ui-avatars.com/api/?background=" +
                (user.getRole() == Role.INSTRUCTOR ? "2563EB" : "16A34A") +
                "&color=fff&name=" + (user.getName() != null && !user.getName().isEmpty() ? user.getName().charAt(0) : "U");

        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.isOnline(),
                avatar,
                user.getLastActiveAt(),
                user.getLastSeenText()
        );
    }

    public User toEntity(UserRegistrationDto dto) {
        User user = new User();
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setPassword(dto.password());
        user.setRole(dto.role() != null ? dto.role() : Role.STUDENT);
        user.setEnabled(true);
        user.setLocked(false);
        return user;
    }

    public UserSummaryDto toSummaryDto(User user) {
        return new UserSummaryDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}