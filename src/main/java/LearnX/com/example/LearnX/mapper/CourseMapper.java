package LearnX.com.example.LearnX.mapper;

import LearnX.com.example.LearnX.Enum.TaskStatus;
import LearnX.com.example.LearnX.Model.Course;
import LearnX.com.example.LearnX.Model.User;
import LearnX.com.example.LearnX.dtos.CourseRequestDto;
import LearnX.com.example.LearnX.dtos.CourseResponseDto;
import LearnX.com.example.LearnX.dtos.UserSummaryDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CourseMapper {

    public CourseResponseDto toResponseDto(Course course) {
        UserSummaryDto instructorDto = course.getInstructor() != null
                ? new UserSummaryDto(
                    course.getInstructor().getId(),
                    course.getInstructor().getName(),
                    course.getInstructor().getEmail())
                : null;

        return new CourseResponseDto(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getThumbnail(),
                course.isPublished(),
                instructorDto
        );
    }

    public Course toEntity(CourseRequestDto dto, User instructor) {
        Course course = new Course();
        course.setTitle(dto.title());
        course.setDescription(dto.description());
        course.setThumbnail(dto.thumbnail());
        course.setPublished(dto.published());
        course.setInstructor(instructor);
        course.setStatus(TaskStatus.PENDING);
        course.setCreatedAt(LocalDateTime.now());
        return course;
    }
}