package LearnX.com.example.LearnX.Controller;

import LearnX.com.example.LearnX.dtos.EventResponseDto;
import LearnX.com.example.LearnX.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/public")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<EventResponseDto>> getPublishedEvents() {
        return ResponseEntity.ok(eventService.getPublishedEvents());
    }
    @GetMapping("/{eventId}/registrants")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getEventRegistrants(@PathVariable Long eventId) {
        List<Map<String, Object>> registrants = eventService.getEventRegistrants(eventId);
        return ResponseEntity.ok(registrants);
    }


    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EventResponseDto> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @PostMapping("/{id}/register")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Map<String, Object>> registerForEvent(@PathVariable Long id) {
        String message = eventService.registerForEvent(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/cancel")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Map<String, Object>> cancelRegistration(@PathVariable Long id) {
        eventService.cancelRegistration(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Registration cancelled");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-registrations")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<EventResponseDto>> getMyRegisteredEvents() {
        return ResponseEntity.ok(eventService.getMyRegisteredEvents());
    }

    @GetMapping("/{id}/registered")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> isUserRegistered(@PathVariable Long id) {
        boolean isRegistered = eventService.isUserRegistered(id);
        Map<String, Object> response = new HashMap<>();
        response.put("registered", isRegistered);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/available-tickets")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getAvailableTickets(@PathVariable Long id) {
        long availableTickets = eventService.getAvailableTickets(id);
        Map<String, Object> response = new HashMap<>();
        response.put("availableTickets", availableTickets);
        return ResponseEntity.ok(response);
    }
}