package LearnX.com.example.LearnX.service;

import LearnX.com.example.LearnX.Enum.Role;
import LearnX.com.example.LearnX.Model.Course;
import LearnX.com.example.LearnX.Model.User;
import LearnX.com.example.LearnX.Repository.CourseRepository;
import LearnX.com.example.LearnX.dtos.CourseCreateDto;
import LearnX.com.example.LearnX.dtos.CourseResponseDto;
import LearnX.com.example.LearnX.mapper.CourseMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final UserService userService;
    private final NotificationService notificationService;

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
    public CourseResponseDto createCourse(CourseCreateDto dto) {
        User currentUser = userService.getCurrentUser();
        if (currentUser.getRole() != Role.INSTRUCTOR && currentUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("Access denied: Only instructors and admins can create courses");
        }

        List<User> additionalInstructors = (dto.additionalInstructorIds() != null)
                ? new ArrayList<>(userService.getUsersByIds(dto.additionalInstructorIds()))
                : new ArrayList<>();

        Course course = courseMapper.toEntity(dto, currentUser, additionalInstructors);
        Course savedCourse = courseRepository.save(course);

        if (savedCourse.isPublished()) {
            String title = "New Course Published";
            String message = "A new course '" + savedCourse.getTitle() + "' has been published.";
            notificationService.sendNotification(currentUser, title, message);
        }

        return courseMapper.toResponseDto(savedCourse);
    }

    public List<CourseResponseDto> getPublishedCourses() {
        return courseRepository.findByPublishedTrue().stream()
                .map(courseMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<CourseResponseDto> getMyCourses() {
        User currentUser = userService.getCurrentUser();
        if (currentUser.getRole() == Role.INSTRUCTOR || currentUser.getRole() == Role.ADMIN) {
            List<Course> courses = courseRepository.findByInstructorId(currentUser.getId());
            return courses.stream()
                    .map(courseMapper::toResponseDto)
                    .collect(Collectors.toList());
        }
        throw new RuntimeException("Access denied: Only instructors and admins can view their courses");
    }

    public List<CourseResponseDto> getAllCourses() {
        User currentUser = userService.getCurrentUser();
        if (currentUser.getRole() == Role.ADMIN || currentUser.getRole() == Role.INSTRUCTOR) {
            return courseRepository.findAll().stream()
                    .map(courseMapper::toResponseDto)
                    .collect(Collectors.toList());
        }
        throw new RuntimeException("Access denied: Only admins and instructors can view all courses");
    }

    public CourseResponseDto getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        User currentUser = userService.getCurrentUser();
        if (currentUser.getRole() == Role.STUDENT && !course.isPublished()) {
            throw new RuntimeException("Access denied: This course is not published");
        }
        return courseMapper.toResponseDto(course);
    }
    @Transactional
    public CourseResponseDto updateCourse(Long id, CourseCreateDto dto) {
        User currentUser = userService.getCurrentUser();
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (currentUser.getRole() != Role.ADMIN &&
                (currentUser.getRole() != Role.INSTRUCTOR ||
                        !course.getInstructor().getId().equals(currentUser.getId()))) {
            throw new RuntimeException("Access denied: You can only update your own courses");
        }

        boolean wasPublished = course.isPublished();
        boolean updated = false;

        // Update only fields that are provided (non-null)
        if (dto.title() != null) {
            course.setTitle(dto.title());
            updated = true;
        }
        if (dto.subtitle() != null) {
            course.setSubtitle(dto.subtitle());
            updated = true;
        }
        if (dto.category() != null) {
            course.setCategory(dto.category());
            updated = true;
        }
        if (dto.subcategory() != null) {
            course.setSubcategory(dto.subcategory());
            updated = true;
        }
        if (dto.topic() != null) {
            course.setTopic(dto.topic());
            updated = true;
        }
        if (dto.language() != null) {
            course.setLanguage(dto.language());
            updated = true;
        }
        if (dto.level() != null) {
            course.setLevel(dto.level());
            updated = true;
        }
        if (dto.duration() != null) {
            course.setDuration(dto.duration());
            updated = true;
        }
        if (dto.thumbnailUrl() != null) {
            course.setThumbnailUrl(dto.thumbnailUrl());
            updated = true;
        }
        if (dto.trailerUrl() != null) {
            course.setTrailerUrl(dto.trailerUrl());
            updated = true;
        }
        if (dto.description() != null) {
            course.setDescription(dto.description());
            updated = true;
        }
        if (dto.teaches() != null) {
            course.getTeaches().clear();
            course.getTeaches().addAll(dto.teaches());
            updated = true;
        }
        if (dto.audience() != null) {
            course.getAudience().clear();
            course.getAudience().addAll(dto.audience());
            updated = true;
        }
        if (dto.requirements() != null) {
            course.getRequirements().clear();
            course.getRequirements().addAll(dto.requirements());
            updated = true;
        }
        if (dto.welcomeMessage() != null) {
            course.setWelcomeMessage(dto.welcomeMessage());
            updated = true;
        }
        if (dto.congratsMessage() != null) {
            course.setCongratsMessage(dto.congratsMessage());
            updated = true;
        }
        if (dto.additionalInstructorIds() != null) {
            List<User> additionalInstructors = new ArrayList<>(userService.getUsersByIds(dto.additionalInstructorIds()));
            course.setAdditionalInstructors(additionalInstructors);
            updated = true;
        }
        // Always allow published to be set (even if false)
        course.setPublished(dto.published());
        updated = true;

        if (!updated) {
            throw new RuntimeException("No fields to update");
        }

        Course updatedCourse = courseRepository.save(course);

        // Send notification if course was just published
        if (dto.published() && !wasPublished) {
            String title = "Course Published";
            String message = "Your course '" + updatedCourse.getTitle() + "' has been published.";
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

    public Course getCourseEntityById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));
    }

}