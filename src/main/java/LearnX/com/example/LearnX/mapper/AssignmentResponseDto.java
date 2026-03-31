package LearnX.com.example.LearnX.mapper;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssignmentResponseDto {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private int maxScore;
    private Long courseId;
}
