package LearnX.com.example.LearnX.dtos;

import java.util.List;

public record SectionDto(
    Long id,
    String name,
    int orderIndex,
    List<LectureDto> lectures
) {}
