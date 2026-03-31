package LearnX.com.example.LearnX.dtos;


import lombok.Data;

import java.time.LocalDateTime;


public record ProgressReportResponseDto (
    Long id,
    String reportText,
    int progressPercent,
    LocalDateTime createdAt,
    Long userId){
}