package LearnX.com.example.LearnX.dtos;


import java.time.LocalDateTime;


public record AssignmentResponseDto(
    Long id,
    String title,
    String description,
    LocalDateTime dueDate,
    int maxScore,
    Long courseId){
}

