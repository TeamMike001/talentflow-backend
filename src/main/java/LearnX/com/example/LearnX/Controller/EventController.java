package LearnX.com.example.LearnX.Controller;


import LearnX.com.example.LearnX.mapper.EventRequestDto;
import LearnX.com.example.LearnX.mapper.EventResponseDto;
import LearnX.com.example.LearnX.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // ========== Event CRUD (Admin only) ==========

    @PostMapping
    public ResponseEntity<EventResponseDto> createEvent(@RequestBody EventRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createEvent(request));
    }

    @GetMapping("/published")
    public ResponseEntity<List<EventResponseDto>> getPublishedEvents() {
        return ResponseEntity.ok(eventService.getPublishedEvents());
    }

    @GetMapping
    public ResponseEntity<List<EventResponseDto>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDto> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponseDto> updateEvent(@PathVariable Long id, @RequestBody EventRequestDto request) {
        return ResponseEntity.ok(eventService.updateEvent(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    // ========== Registration Endpoints (Students) ==========

    @PostMapping("/{id}/register")
    public ResponseEntity<String> registerForEvent(@PathVariable Long id) {
        String message = eventService.registerForEvent(id);
        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelRegistration(@PathVariable Long id) {
        eventService.cancelRegistration(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/registered")
    public ResponseEntity<Boolean> isUserRegistered(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.isUserRegistered(id));
    }

    @GetMapping("/{id}/available-tickets")
    public ResponseEntity<Long> getAvailableTickets(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getAvailableTickets(id));
    }

    @GetMapping("/my-registrations")
    public ResponseEntity<List<EventResponseDto>> getMyRegisteredEvents() {
        return ResponseEntity.ok(eventService.getMyRegisteredEvents());
    }
}