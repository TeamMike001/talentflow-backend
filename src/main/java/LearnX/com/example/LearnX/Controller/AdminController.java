package LearnX.com.example.LearnX.Controller;

import LearnX.com.example.LearnX.Enum.Role;
import LearnX.com.example.LearnX.Model.Event;
import LearnX.com.example.LearnX.Model.User;
import LearnX.com.example.LearnX.dtos.EventRequestDto;
import LearnX.com.example.LearnX.dtos.EventResponseDto;
import LearnX.com.example.LearnX.dtos.UserResponseDto;
import LearnX.com.example.LearnX.service.EventService;
import LearnX.com.example.LearnX.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final EventService eventService;

    public AdminController(UserService userService, EventService eventService) {
        this.userService = userService;
        this.eventService = eventService;
    }

    // ========== User Management ==========

    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<UserResponseDto> updateUserRole(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String role = request.get("role");
        return ResponseEntity.ok(userService.updateUserRole(id, Role.valueOf(role)));
    }

    @PutMapping("/users/{id}/toggle-status")
    public ResponseEntity<Map<String, Object>> toggleUserStatus(@PathVariable Long id) {
        User user = userService.toggleUserEnabled(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("enabled", user.isEnabled());
        response.put("message", user.isEnabled() ? "Account enabled" : "Account disabled");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "User deleted successfully");
        return ResponseEntity.ok(response);
    }

    // ========== Course Management ==========

    @GetMapping("/courses")
    public ResponseEntity<List<Map<String, Object>>> getAllCourses() {
        return ResponseEntity.ok(userService.getAllCoursesWithEnrollments());
    }

    // ========== Event Management ==========

    @GetMapping("/events")
    public ResponseEntity<List<EventResponseDto>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @PostMapping("/events")
    public ResponseEntity<Map<String, Object>> createEvent(@RequestBody EventRequestDto request) {
        EventResponseDto event = eventService.createEvent(request);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("event", event);
        response.put("message", "Event created successfully");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/events/{id}")
    public ResponseEntity<EventResponseDto> updateEvent(@PathVariable Long id, @RequestBody EventRequestDto request) {
        return ResponseEntity.ok(eventService.updateEvent(id, request));
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<Map<String, Object>> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Event deleted successfully");
        return ResponseEntity.ok(response);
    }


    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        return ResponseEntity.ok(userService.getAdminStats());
    }
}