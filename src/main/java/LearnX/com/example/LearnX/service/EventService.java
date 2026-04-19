package LearnX.com.example.LearnX.service;

import LearnX.com.example.LearnX.Enum.Role;
import LearnX.com.example.LearnX.Model.Event;
import LearnX.com.example.LearnX.Model.EventRegistration;
import LearnX.com.example.LearnX.Model.User;
import LearnX.com.example.LearnX.Repository.EventRegistrationRepository;
import LearnX.com.example.LearnX.Repository.EventRepository;
import LearnX.com.example.LearnX.dtos.EventRequestDto;
import LearnX.com.example.LearnX.dtos.EventResponseDto;
import LearnX.com.example.LearnX.mapper.EventMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final EventRegistrationRepository eventRegistrationRepository;
    private final EventMapper eventMapper;
    private final UserService userService;

    public EventService(EventRepository eventRepository,
                        EventRegistrationRepository eventRegistrationRepository,
                        EventMapper eventMapper,
                        UserService userService) {
        this.eventRepository = eventRepository;
        this.eventRegistrationRepository = eventRegistrationRepository;
        this.eventMapper = eventMapper;
        this.userService = userService;
    }

    // ========== Event CRUD (Admin only) ==========

    @Transactional
    public EventResponseDto createEvent(EventRequestDto dto) {
        User currentUser = userService.getCurrentUser();
        if (currentUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("Access denied: Only admins can create events");
        }
        Event event = eventMapper.toEntity(dto, currentUser);
        Event saved = eventRepository.save(event);
        return eventMapper.toResponseDto(saved);
    }

    @Transactional
    public EventResponseDto updateEvent(Long id, EventRequestDto dto) {
        User currentUser = userService.getCurrentUser();
        if (currentUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("Access denied: Only admins can update events");
        }
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        eventMapper.updateEntity(event, dto);
        Event updated = eventRepository.save(event);
        return eventMapper.toResponseDto(updated);
    }

    // FIXED: Method to get registrants for an event
    public List<Map<String, Object>> getEventRegistrants(Long eventId) {
        // Verify event exists
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // Get all registrations for this event - NOW WORKS
        List<EventRegistration> registrations = eventRegistrationRepository.findByEventId(eventId);

        return registrations.stream()
                .map(reg -> {
                    Map<String, Object> registrant = new HashMap<>();
                    registrant.put("studentId", reg.getUser().getId());
                    registrant.put("studentName", reg.getUser().getName() != null ? reg.getUser().getName() : "Student");
                    registrant.put("studentEmail", reg.getUser().getEmail());
                    registrant.put("registeredAt", reg.getRegisteredAt());
                    return registrant;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteEvent(Long id) {
        User currentUser = userService.getCurrentUser();
        if (currentUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("Access denied: Only admins can delete events");
        }
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // Delete all registrations first
        List<EventRegistration> registrations = eventRegistrationRepository.findByEventId(id);
        eventRegistrationRepository.deleteAll(registrations);

        // Then delete the event
        eventRepository.delete(event);
    }

    // ========== Event Queries ==========

    public List<EventResponseDto> getPublishedEvents() {
        return eventRepository.findByPublishedTrueOrderByEventDateAsc().stream()
                .map(eventMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<EventResponseDto> getAllEvents() {
        User currentUser = userService.getCurrentUser();
        if (currentUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("Access denied: Only admins can view all events");
        }
        return eventRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(eventMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public EventResponseDto getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        User currentUser = userService.getCurrentUser();
        if (currentUser.getRole() == Role.STUDENT && !event.isPublished()) {
            throw new RuntimeException("Access denied: Event not published");
        }
        return eventMapper.toResponseDto(event);
    }

    // ========== Registration Methods ==========

    @Transactional
    public String registerForEvent(Long eventId) {
        User currentUser = userService.getCurrentUser();
        if (currentUser.getRole() != Role.STUDENT) {
            throw new RuntimeException("Only students can register for events");
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (!event.isPublished()) {
            throw new RuntimeException("Event is not available for registration");
        }

        if (eventRegistrationRepository.existsByEventIdAndUserId(eventId, currentUser.getId())) {
            throw new RuntimeException("You are already registered for this event");
        }

        long registeredCount = eventRegistrationRepository.countByEventId(eventId);
        if (registeredCount >= event.getTicketsAvailable()) {
            throw new RuntimeException("No tickets available for this event");
        }

        EventRegistration registration = new EventRegistration();
        registration.setEvent(event);
        registration.setUser(currentUser);
        registration.setRegisteredAt(LocalDateTime.now());
        registration.setAttended(false);
        eventRegistrationRepository.save(registration);

        return "Successfully registered for " + event.getTitle();
    }

    @Transactional
    public void cancelRegistration(Long eventId) {
        User currentUser = userService.getCurrentUser();
        EventRegistration registration = eventRegistrationRepository.findByEventIdAndUserId(eventId, currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Registration not found"));
        eventRegistrationRepository.delete(registration);
    }

    public boolean isUserRegistered(Long eventId) {
        User currentUser = userService.getCurrentUser();
        return eventRegistrationRepository.existsByEventIdAndUserId(eventId, currentUser.getId());
    }

    public long getAvailableTickets(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        long registeredCount = eventRegistrationRepository.countByEventId(eventId);
        return event.getTicketsAvailable() - registeredCount;
    }

    public List<EventResponseDto> getMyRegisteredEvents() {
        User currentUser = userService.getCurrentUser();
        return eventRegistrationRepository.findByUserId(currentUser.getId()).stream()
                .map(reg -> eventMapper.toResponseDto(reg.getEvent()))
                .collect(Collectors.toList());
    }
}