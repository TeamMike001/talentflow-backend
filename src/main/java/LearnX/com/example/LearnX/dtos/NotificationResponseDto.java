package LearnX.com.example.LearnX.dtos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public record NotificationResponseDto (
    Long id,
    String title,
    String message,
    boolean isRead,
    LocalDateTime createdAt){
}