package LearnX.com.example.LearnX.mapper;

import LearnX.com.example.LearnX.Model.Assignment;
import LearnX.com.example.LearnX.dtos.AssignmentRequestDto;
import LearnX.com.example.LearnX.dtos.AssignmentResponseDto;
import org.springframework.stereotype.Component;

@Component
public class AssignmentMapper {

    public AssignmentResponseDto toResponseDto(Assignment assignment) {
        return new AssignmentResponseDto(
                assignment.getId(),
                assignment.getTitle(),
                assignment.getDescription(),
                assignment.getDueDate(),
                assignment.getMaxScore(),
                assignment.getCourse() != null ? assignment.getCourse().getId() : null
        );
    }

    public Assignment toEntity(AssignmentRequestDto dto) {
        Assignment assignment = new Assignment();
        assignment.setTitle(dto.title());
        assignment.setDescription(dto.description());
        assignment.setDueDate(dto.dueDate());
        assignment.setMaxScore(dto.maxScore());
        return assignment;
    }
}