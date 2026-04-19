package LearnX.com.example.LearnX.Controller;

import LearnX.com.example.LearnX.Enum.Role;
import LearnX.com.example.LearnX.Model.User;
import LearnX.com.example.LearnX.dtos.UpdateUserDto;
import LearnX.com.example.LearnX.dtos.UserDto;
import LearnX.com.example.LearnX.dtos.UserResponseDto;
import LearnX.com.example.LearnX.mapper.UserMapper;
import LearnX.com.example.LearnX.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserController(UserService userService, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDto> getCurrentUserProfile() {
        return ResponseEntity.ok(userService.getCurrentUserProfile());
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDto> updateCurrentUser(@RequestBody UpdateUserDto updateDto) {
        User currentUser = userService.getCurrentUser();
        return ResponseEntity.ok(userService.updateUser(currentUser.getId(), updateDto));
    }

    @DeleteMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteCurrentUser() {
        userService.deleteOwnAccount();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/messaging")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserResponseDto>> getUsersForMessaging() {
        try {
            User currentUser = userService.getCurrentUser();
            List<User> allUsers = userService.getAllUsersEntities();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' h:mm a");

            List<UserResponseDto> filteredUsers = allUsers.stream()
                    .filter(user -> !user.getId().equals(currentUser.getId()))
                    .filter(user -> user.getRole() != Role.ADMIN)
                    .map(user -> {
                        String avatar = "https://ui-avatars.com/api/?background=" +
                                (user.getRole() == Role.INSTRUCTOR ? "2563EB" : "16A34A") +
                                "&color=fff&name=" + (user.getName() != null ? user.getName().charAt(0) : "U");

                        String lastSeen = null;
                        if (user.getLastActiveAt() != null) {
                            lastSeen = user.getLastActiveAt().format(formatter);
                        } else {
                            lastSeen = "Never";
                        }

                        return new UserResponseDto(
                                user.getId(),
                                user.getName() != null ? user.getName() : "User",
                                user.getEmail(),
                                user.getRole(),
                                user.isOnline(),
                                avatar,
                                user.getLastActiveAt(),
                                lastSeen
                        );
                    })
                    .collect(Collectors.toList());

            System.out.println("✅ Returning " + filteredUsers.size() + " users for messaging");
            return ResponseEntity.ok(filteredUsers);

        } catch (Exception e) {
            System.err.println("❌ Error fetching users: " + e.getMessage());
            return ResponseEntity.ok(List.of());
        }
    }
    // Add to UserController.java

    @GetMapping("/instructors")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public ResponseEntity<List<UserResponseDto>> getAllInstructors() {
        List<UserResponseDto> instructors = userService.getAllInstructors();
        return ResponseEntity.ok(instructors);
    }

    @GetMapping("/students")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public ResponseEntity<List<UserResponseDto>> getAllStudents() {
        List<UserResponseDto> students = userService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/courses/{courseId}/students")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public ResponseEntity<List<Map<String, Object>>> getCourseStudents(@PathVariable Long courseId) {
        List<Map<String, Object>> students = userService.getCourseStudents(courseId);
        return ResponseEntity.ok(students);
    }

    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> passwordData) {
        String currentPassword = passwordData.get("currentPassword");
        String newPassword = passwordData.get("newPassword");

        User currentUser = userService.getCurrentUser();

        if (!passwordEncoder.matches(currentPassword, currentUser.getPassword())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Current password is incorrect"));
        }

        currentUser.setPassword(passwordEncoder.encode(newPassword));
        userService.updateUserLastActive(currentUser);

        return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
    }

    @PostMapping("/session-timeout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateSessionTimeout(@RequestBody Map<String, Integer> timeoutData) {
        return ResponseEntity.ok(Map.of("message", "Session timeout updated"));
    }

    @PostMapping("/login-alerts")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateLoginAlerts(@RequestBody Map<String, Boolean> alertsData) {
        return ResponseEntity.ok(Map.of("message", "Login alerts updated"));
    }

    @GetMapping("/export-data")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> exportUserData() {
        User currentUser = userService.getCurrentUser();
        String exportData = String.format("""
            {
                "user": {
                    "id": %d,
                    "name": "%s",
                    "email": "%s",
                    "role": "%s",
                    "createdAt": "%s"
                }
            }
            """,
                currentUser.getId(),
                currentUser.getName() != null ? currentUser.getName() : "",
                currentUser.getEmail(),
                currentUser.getRole(),
                currentUser.getCreatedAt()
        );

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=user-data.json")
                .body(exportData.getBytes());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        UserDto userDto = userService.getUserDtoById(id);
        return ResponseEntity.ok(userDto);
    }
}