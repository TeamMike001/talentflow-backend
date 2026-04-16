package LearnX.com.example.LearnX.dtos;

import java.util.List;

public record SectionDto(
    String name,
    int orderIndex,
    List<LectureDto> lectures
) {}