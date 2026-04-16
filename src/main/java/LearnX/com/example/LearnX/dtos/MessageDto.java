package LearnX.com.example.LearnX.dtos;

import java.time.LocalDateTime;

public record MessageDto(Long id, Long userId, String userName, String content, String fileUrl, LocalDateTime timestamp) {}