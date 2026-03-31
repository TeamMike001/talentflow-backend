package LearnX.com.example.LearnX.service;

import LearnX.com.example.LearnX.Enum.Role;
import LearnX.com.example.LearnX.Enum.TaskStatus;
import LearnX.com.example.LearnX.Model.Course;
import LearnX.com.example.LearnX.Model.User;
import LearnX.com.example.LearnX.Repository.CourseRepository;
import LearnX.com.example.LearnX.dtos.CourseRequestDto;
import LearnX.com.example.LearnX.dtos.CourseResponseDto;
import LearnX.com.example.LearnX.mapper.CourseMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final UserService userService;

    public CourseService(CourseRepository courseRepository,
                         CourseMapper courseMapper,
                         UserService userService) {
        this.courseRepository = courseRepository;
        this.courseMapper = courseMapper;
        this.userService = userService;
    }

    @Transactional
    public CourseResponseDto createCourse(CourseRequestDto requestDto) {
        User currentUser = userService.getCurrentUser();

        if (currentUser.getRole() != Role.INSTRUCTOR && currentUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("Access denied: Only instructors and admins can create courses");
        }

        Course course = courseMapper.toEntity(requestDto, currentUser);
        Course savedCourse = courseRepository.save(course);

        return courseMapper.toResponseDto(savedCourse);
    }

    // ====================== GET ALL PUBLISHED COURSES (For Students) ======================
    public List<CourseResponseDto> getPublishedCourses() {
        return courseRepository.findByPublishedTrue().stream()
                .map(courseMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    // ====================== GET ALL COURSES (Admin + Instructor) ======================
    public List<CourseResponseDto> getAllCourses() {
        User currentUser = userService.getCurrentUser();

        if (currentUser.getRole() == Role.ADMIN || currentUser.getRole() == Role.INSTRUCTOR) {
            return courseRepository.findAll().stream()
                    .map(courseMapper::toResponseDto)
                    .collect(Collectors.toList());
        }

        throw new RuntimeException("Access denied: Only admins and instructors can view all courses");
    }

    // ====================== GET COURSE BY ID ======================
    public CourseResponseDto getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Students can only see published courses
        User currentUser = userService.getCurrentUser();
        if (currentUser.getRole() == Role.STUDENT && !course.isPublished()) {
            throw new RuntimeException("Access denied: This course is not published");
        }

        return courseMapper.toResponseDto(course);
    }

    // ====================== UPDATE COURSE ======================
    @Transactional
    public CourseResponseDto updateCourse(Long id, CourseRequestDto requestDto) {
        User currentUser = userService.getCurrentUser();
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Only the instructor who created it or Admin can update
        if (currentUser.getRole() != Role.ADMIN &&
                (currentUser.getRole() != Role.INSTRUCTOR || !course.getInstructor().getId().equals(currentUser.getId()))) {
            throw new RuntimeException("Access denied: You can only update your own courses");
        }

        // Update fields
        course.setTitle(requestDto.title());
        course.setDescription(requestDto.description());
        course.setThumbnail(requestDto.thumbnail());
        course.setPublished(requestDto.published());

        // If published, change status to APPROVED (optional)
        if (requestDto.published()) {
            course.setStatus(TaskStatus.APPROVED);
        }

        Course updatedCourse = courseRepository.save(course);
        return courseMapper.toResponseDto(updatedCourse);
    }

    @Transactional
    public void deleteCourse(Long id) {
        User currentUser = userService.getCurrentUser();
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Only Admin or the course owner (Instructor) can delete
        if (currentUser.getRole() != Role.ADMIN &&
                (currentUser.getRole() != Role.INSTRUCTOR || !course.getInstructor().getId().equals(currentUser.getId()))) {
            throw new RuntimeException("Access denied: You can only delete your own courses");
        }

        courseRepository.delete(course);
    }
}