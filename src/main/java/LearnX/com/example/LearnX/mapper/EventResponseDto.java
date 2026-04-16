// EventResponseDto.java
package LearnX.com.example.LearnX.mapper;

import LearnX.com.example.LearnX.dtos.UserSummaryDto;

import java.time.LocalDateTime;

public record EventResponseDto(
    Long id,
    String title,
    String description,
    String venue,
    String time,
    LocalDateTime eventDate,
    int daysLeft,
    String color,
    String avatarUrl,
    int ticketsAvailable,
    boolean published,
    LocalDateTime createdAt,
    UserSummaryDto createdBy
) {}