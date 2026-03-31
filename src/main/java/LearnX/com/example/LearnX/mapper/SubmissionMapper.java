package LearnX.com.example.LearnX.mapper;

import LearnX.com.example.LearnX.Model.Submission;
import LearnX.com.example.LearnX.dtos.SubmissionRequestDto;
import LearnX.com.example.LearnX.dtos.SubmissionResponseDto;
import org.springframework.stereotype.Component;

@Component
public class SubmissionMapper {

    public SubmissionResponseDto toResponseDto(Submission submission) {
        return new SubmissionResponseDto(
                submission.getId(),
                submission.getContent(),
                submission.getSubmittedAt(),
                submission.getScore(),
                submission.getFeedback(),
                submission.getGradedAt(),
                submission.getAssignment() != null ? submission.getAssignment().getId() : null,
                submission.getAssignment() != null ? submission.getAssignment().getTitle() : null,
                submission.getStudent() != null ? submission.getStudent().getId() : null,
                submission.getStudent() != null ? submission.getStudent().getName() : null
        );
    }

    public Submission toEntity(SubmissionRequestDto dto) {

        Submission submission = new Submission();
        submission.setContent(dto.content());
        return submission;
    }
}