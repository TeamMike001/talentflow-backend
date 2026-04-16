package LearnX.com.example.LearnX.dtos;

public record LectureDto(
    String name,
    int orderIndex,
    String videoUrl,
    String notes,
    String caption,
    String description,
    String attachmentUrl
) {}