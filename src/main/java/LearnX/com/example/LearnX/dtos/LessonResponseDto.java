package LearnX.com.example.LearnX.dtos;


public record LessonResponseDto (
     Long id,
     String title,
    String content,
    String attachmentUrl,
    int orderIndex,
    Long courseId){
}

