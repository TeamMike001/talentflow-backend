package LearnX.com.example.LearnX.mapper;

import LearnX.com.example.LearnX.Model.Event;
import LearnX.com.example.LearnX.Model.User;
import LearnX.com.example.LearnX.Repository.EventRegistrationRepository;
import LearnX.com.example.LearnX.dtos.EventRequestDto;
import LearnX.com.example.LearnX.dtos.EventResponseDto;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class EventMapper {
    private final EventRegistrationRepository eventRegistrationRepository;

    public EventMapper(EventRegistrationRepository eventRegistrationRepository) {
        this.eventRegistrationRepository = eventRegistrationRepository;
    }

    public Event toEntity(EventRequestDto dto, User createdBy) {
        Event event = new Event();
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setVenue(dto.getVenue());
        event.setEventDate(dto.getEventDate());
        event.setTicketsAvailable(dto.getTicketsAvailable());
        event.setPublished(dto.isPublished());
        event.setCreatedBy(createdBy);
        event.setCreatedAt(LocalDateTime.now());
        return event;
    }

    public void updateEntity(Event event, EventRequestDto dto) {
        if (dto.getTitle() != null) event.setTitle(dto.getTitle());
        if (dto.getDescription() != null) event.setDescription(dto.getDescription());
        if (dto.getVenue() != null) event.setVenue(dto.getVenue());
        if (dto.getEventDate() != null) event.setEventDate(dto.getEventDate());
        if (dto.getTicketsAvailable() > 0) event.setTicketsAvailable(dto.getTicketsAvailable());
        event.setPublished(dto.isPublished());
    }

    public EventResponseDto toResponseDto(Event event) {
        EventResponseDto dto = new EventResponseDto();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setVenue(event.getVenue());
        dto.setEventDate(event.getEventDate());
        dto.setTicketsAvailable(event.getTicketsAvailable());
        dto.setPublished(event.isPublished());
        dto.setRegisteredCount(eventRegistrationRepository.countByEventId(event.getId()));
        dto.setCreatedBy(event.getCreatedBy() != null ? event.getCreatedBy().getName() : "Admin");
        dto.setCreatedAt(event.getCreatedAt());
        return dto;
    }
}