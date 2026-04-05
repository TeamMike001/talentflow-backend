package LearnX.com.example.LearnX.dtos;


import java.time.LocalDateTime;
public record EnrollmentResponseDto (
     Long id,
     Long studentId,
    Long courseId,
    String courseTitle,
    LocalDateTime enrolledAt,Integer progressPercentage){
}