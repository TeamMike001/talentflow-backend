package LearnX.com.example.LearnX.service;

import java.time.LocalDateTime;

public record BookmarkResponseDto(Long id, Long courseId, String courseTitle, LocalDateTime createdAt) {}