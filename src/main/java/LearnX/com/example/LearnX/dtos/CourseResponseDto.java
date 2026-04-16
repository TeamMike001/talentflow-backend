package LearnX.com.example.LearnX.dtos;

import java.time.LocalDateTime;
import java.util.List;

public record CourseResponseDto(
        Long id,
        String title,
        String subtitle,
        String category,
        String subcategory,
        String topic,
        String language,
        String level,
        String duration,
        String thumbnailUrl,
        String trailerUrl,
        String description,
        List<String> teaches,
        List<String> audience,
        List<String> requirements,
        List<SectionDto> sections,
        String welcomeMessage,
        String congratsMessage,
        UserSummaryDto instructor,
        List<UserSummaryDto> additionalInstructors,
        boolean published,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}