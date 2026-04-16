package LearnX.com.example.LearnX.mapper;

import LearnX.com.example.LearnX.Model.Submission;
import LearnX.com.example.LearnX.dtos.SubmissionRequestDto;
import LearnX.com.example.LearnX.dtos.SubmissionResponseDto;
import org.springframework.stereotype.Component;

@Component
public class SubmissionMapper {

    public Submission toEntity(SubmissionRequestDto dto) {
        if (dto == null) {
            return null;
        }

        Submission submission = new Submission();
        submission.setContent(dto.content());
        submission.setAttachmentUrl(dto.attachmentUrl());
        return submission;
    }

    public SubmissionResponseDto toResponseDto(Submission submission) {
        if (submission == null) {
            return null;
        }

        return new SubmissionResponseDto(
                submission.getId(),
                submission.getAssignment() != null ? submission.getAssignment().getId() : null,
                submission.getAssignment() != null ? submission.getAssignment().getTitle() : null,
                submission.getStudent() != null ? submission.getStudent().getId() : null,
                submission.getStudent() != null ? submission.getStudent().getName() : null,
                submission.getContent(),
                submission.getAttachmentUrl(),
                submission.getScore(),
                submission.getFeedback(),
                submission.isGraded(),
                submission.getSubmittedAt(),
                submission.getGradedAt(),
                submission.getAssignment() != null ? submission.getAssignment().getMaxScore() : null
        );
    }
}