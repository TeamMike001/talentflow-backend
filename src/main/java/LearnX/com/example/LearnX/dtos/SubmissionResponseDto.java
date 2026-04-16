package LearnX.com.example.LearnX.dtos;

import java.time.LocalDateTime;

public record SubmissionResponseDto(
        Long id,
        Long assignmentId,
        String assignmentTitle,
        Long studentId,
        String studentName,
        String content,
        String attachmentUrl,
        Integer score,
        String feedback,
        boolean graded,
        LocalDateTime submittedAt,
        LocalDateTime gradedAt,
        Integer maxScore
) {}