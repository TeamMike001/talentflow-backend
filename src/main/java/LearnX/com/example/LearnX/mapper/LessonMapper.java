package LearnX.com.example.LearnX.mapper;

import LearnX.com.example.LearnX.Model.Lesson;
import LearnX.com.example.LearnX.dtos.LessonRequestDto;
import LearnX.com.example.LearnX.dtos.LessonResponseDto;
import org.springframework.stereotype.Component;

@Component
public class LessonMapper {

    public LessonResponseDto toResponseDto(Lesson lesson) {
        return new LessonResponseDto(
                lesson.getId(),
                lesson.getTitle(),
                lesson.getContent(),
                lesson.getAttachmentUrl(),
                lesson.getOrderIndex(),
                lesson.getCourse() != null ? lesson.getCourse().getId() : null
        );
    }

    public Lesson toEntity(LessonRequestDto dto) {
        Lesson lesson = new Lesson();
        lesson.setTitle(dto.title());
        lesson.setContent(dto.content());
        lesson.setAttachmentUrl(dto.attachmentUrl());
        lesson.setOrderIndex(dto.orderIndex());
        return lesson;
    }
}