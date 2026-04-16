package LearnX.com.example.LearnX.service;

import LearnX.com.example.LearnX.Model.Event;
import LearnX.com.example.LearnX.Model.User;

import LearnX.com.example.LearnX.dtos.UserSummaryDto;
import LearnX.com.example.LearnX.mapper.EventRequestDto;
import LearnX.com.example.LearnX.mapper.EventResponseDto;
import LearnX.com.example.LearnX.mapper.UserMapper;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class EventMapper {
    private final UserMapper userMapper;

    public EventMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public Event toEntity(EventRequestDto dto, User createdBy) {
        Event event = new Event();
        event.setTitle(dto.title());
        event.setDescription(dto.description());
        event.setVenue(dto.venue());
        event.setTime(dto.time());
        event.setEventDate(dto.eventDate());
        event.setDaysLeft(dto.daysLeft());
        event.setColor(dto.color());
        event.setAvatarUrl(dto.avatarUrl());
        event.setTicketsAvailable(dto.ticketsAvailable());
        event.setPublished(dto.published());
        event.setCreatedBy(createdBy);
        event.setCreatedAt(LocalDateTime.now());
        return event;
    }

    public void updateEntity(Event event, EventRequestDto dto) {
        event.setTitle(dto.title());
        event.setDescription(dto.description());
        event.setVenue(dto.venue());
        event.setTime(dto.time());
        event.setEventDate(dto.eventDate());
        event.setDaysLeft(dto.daysLeft());
        event.setColor(dto.color());
        event.setAvatarUrl(dto.avatarUrl());
        event.setTicketsAvailable(dto.ticketsAvailable());
        event.setPublished(dto.published());
    }

    public EventResponseDto toResponseDto(Event event) {
        UserSummaryDto createdByDto = event.getCreatedBy() != null
                ? userMapper.toSummaryDto(event.getCreatedBy())
                : null;

        return new EventResponseDto(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getVenue(),
                event.getTime(),
                event.getEventDate(),
                event.getDaysLeft(),
                event.getColor(),
                event.getAvatarUrl(),
                event.getTicketsAvailable(),
                event.isPublished(),
                event.getCreatedAt(),
                createdByDto
        );
    }
}