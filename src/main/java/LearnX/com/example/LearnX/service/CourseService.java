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
    private final NotificationService notificationService;   // Injected for notifications

    public CourseService(CourseRepository courseRepository,
                         CourseMapper courseMapper,
                         UserService userService,
                         NotificationService notificationService) {
        this.courseRepository = courseRepository;
        this.courseMapper = courseMapper;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @Transactional
    public CourseResponseDto createCourse(CourseRequestDto requestDto) {
        User currentUser = userService.getCurrentUser();

        if (currentUser.getRole() != Role.INSTRUCTOR && currentUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("Access denied: Only instructors and admins can create courses");
        }

        Course course = courseMapper.toEntity(requestDto, currentUser);
        Course savedCourse = courseRepository.save(course);

        // Send notification if the course is published
        if (savedCourse.isPublished()) {
            String title = "New Course Published";
            String message = "A new course '" + savedCourse.getTitle() + "' has been published. Check it out!";

            notificationService.sendNotification(currentUser, title, message);   // Notify the instructor first
            // You can also notify enrolled students here if needed
        }

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

        if (currentUser.getRole() != Role.ADMIN &&
                (currentUser.getRole() != Role.INSTRUCTOR ||
                        !course.getInstructor().getId().equals(currentUser.getId()))) {
            throw new RuntimeException("Access denied: You can only update your own courses");
        }

        course.setTitle(requestDto.title());
        course.setDescription(requestDto.description());
        course.setThumbnail(requestDto.thumbnail());
        course.setPublished(requestDto.published());

        if (requestDto.published()) {
            course.setStatus(TaskStatus.APPROVED);
        }

        Course updatedCourse = courseRepository.save(course);

        // Send notification when course becomes published
        if (requestDto.published() && !course.isPublished()) {   // was not published before
            String title = "New Course Published";
            String message = "A new course '" + updatedCourse.getTitle() + "' has been published.";

            notificationService.sendNotification(currentUser, title, message);
        }

        return courseMapper.toResponseDto(updatedCourse);
    }

    @Transactional
    public void deleteCourse(Long id) {
        User currentUser = userService.getCurrentUser();

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (currentUser.getRole() != Role.ADMIN &&
                (currentUser.getRole() != Role.INSTRUCTOR ||
                        !course.getInstructor().getId().equals(currentUser.getId()))) {
            throw new RuntimeException("Access denied: You can only delete your own courses");
        }

        courseRepository.delete(course);
    }
}