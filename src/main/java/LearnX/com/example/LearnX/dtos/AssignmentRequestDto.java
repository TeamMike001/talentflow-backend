package LearnX.com.example.LearnX.dtos;

import lombok.Data;

import java.time.LocalDateTime;



public record AssignmentRequestDto (
    String title,
     String description,
    LocalDateTime dueDate,
    int maxScore){
}