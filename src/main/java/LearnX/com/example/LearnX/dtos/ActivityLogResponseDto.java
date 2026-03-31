package LearnX.com.example.LearnX.dtos;


import lombok.Data;

import java.time.LocalDateTime;


public record ActivityLogResponseDto (
     Long id,
    String action,
    LocalDateTime createdAt,
    boolean completed,
    Long lessonId,
    Long userId){
}