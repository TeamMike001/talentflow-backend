package LearnX.com.example.LearnX.dtos;

import lombok.Data;

import java.time.LocalDateTime;


public record SubmissionResponseDto (
    Long id,
    String content,
    LocalDateTime submittedAt,
    Integer score,
    String feedback,
    LocalDateTime gradedAt,
    Long assignmentId,
    String assignmentTitle,
    Long studentId,
    String studentName){
}
