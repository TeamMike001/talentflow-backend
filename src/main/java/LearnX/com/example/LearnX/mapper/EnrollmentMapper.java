package LearnX.com.example.LearnX.mapper;

import LearnX.com.example.LearnX.Model.Course;
import LearnX.com.example.LearnX.Model.Enrollment;
import LearnX.com.example.LearnX.Model.User;
import LearnX.com.example.LearnX.dtos.EnrollmentResponseDto;
import org.springframework.stereotype.Component;

@Component
public class EnrollmentMapper {

    public EnrollmentResponseDto toResponseDto(Enrollment enrollment) {
        return new EnrollmentResponseDto(
                enrollment.getId(),
                enrollment.getStudent() != null ? enrollment.getStudent().getId() : null,
                enrollment.getCourse() != null ? enrollment.getCourse().getId() : null,
                enrollment.getCourse() != null ? enrollment.getCourse().getTitle() : null,
                enrollment.getEnrolledAt()
        );
    }

    public Enrollment toEntity(User student, Course course) {
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        return enrollment;
    }
}