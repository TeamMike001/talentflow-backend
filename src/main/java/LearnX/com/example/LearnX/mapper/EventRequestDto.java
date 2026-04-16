package LearnX.com.example.LearnX.mapper;

import java.time.LocalDateTime;

public record EventRequestDto(
    String title,
    String description,
    String venue,
    String time,
    LocalDateTime eventDate,
    int daysLeft,
    String color,
    String avatarUrl,
    int ticketsAvailable,
    boolean published
) {}