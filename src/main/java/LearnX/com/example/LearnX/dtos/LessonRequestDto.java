package LearnX.com.example.LearnX.dtos;


public record LessonRequestDto (
    String title,
     String content,
     String attachmentUrl,
    int orderIndex){
}
