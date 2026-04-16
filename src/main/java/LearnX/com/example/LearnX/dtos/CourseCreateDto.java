package LearnX.com.example.LearnX.dtos;

import java.util.List;

public record CourseCreateDto(
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
        List<Long> additionalInstructorIds,
        boolean published
) {}