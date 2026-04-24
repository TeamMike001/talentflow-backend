package LearnX.com.example.LearnX.service;

import LearnX.com.example.LearnX.Enum.Role;
import LearnX.com.example.LearnX.Model.Course;
import LearnX.com.example.LearnX.Model.Enrollment;
import LearnX.com.example.LearnX.Model.User;
import LearnX.com.example.LearnX.Repository.ActivityLogRepository;
import LearnX.com.example.LearnX.Repository.CourseRepository;
import LearnX.com.example.LearnX.Repository.EnrollmentRepository;
import LearnX.com.example.LearnX.Repository.LessonRepository;
import LearnX.com.example.LearnX.dtos.EnrollmentRequestDto;
import LearnX.com.example.LearnX.dtos.EnrollmentResponseDto;
import LearnX.com.example.LearnX.mapper.EnrollmentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final ActivityLogRepository activityLogRepository;
    private final EnrollmentMapper enrollmentMapper;
    private final UserService userService;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             CourseRepository courseRepository,
                             LessonRepository lessonRepository,
                             ActivityLogRepository activityLogRepository,
                             EnrollmentMapper enrollmentMapper,
                             UserService userService) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
        this.activityLogRepository = activityLogRepository;
        this.enrollmentMapper = enrollmentMapper;
        this.userService = userService;
    }

    @Transactional
    public EnrollmentResponseDto enrollInCourse(EnrollmentRequestDto request) {
        User student = userService.getCurrentUser();

        if (student.getRole() != Role.STUDENT) {
            throw new RuntimeException("Only students can enroll in courses");
        }

        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.isPublished()) {
            throw new RuntimeException("Cannot enroll: This course is not published yet");
        }

        boolean alreadyEnrolled = enrollmentRepository.existsByStudentIdAndCourseId(
                student.getId(), course.getId());

        if (alreadyEnrolled) {
            throw new RuntimeException("You are already enrolled in this course");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setProgressPercentage(0);

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        return enrollmentMapper.toResponseDto(savedEnrollment);
    }

    @Transactional
    public void unenrollFromCourse(Long courseId) {
        User student = userService.getCurrentUser();

        if (student.getRole() != Role.STUDENT) {
            throw new RuntimeException("Only students can unenroll from courses");
        }

        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(student.getId(), courseId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("You are not enrolled in this course"));

        enrollmentRepository.delete(enrollment);
    }

    public boolean isEnrolled(Long courseId) {
        User student = userService.getCurrentUser();
        if (student.getRole() != Role.STUDENT) {
            return false;
        }
        return enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), courseId);
    }

    public List<EnrollmentResponseDto> getMyEnrollments() {
        User student = userService.getCurrentUser();

        if (student.getRole() != Role.STUDENT) {
            throw new RuntimeException("Only students can view their enrollments");
        }

        return enrollmentRepository.findByStudentId(student.getId())
                .stream()
                .map(enrollmentMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<EnrollmentResponseDto> getEnrollmentsByCourse(Long courseId) {
        User currentUser = userService.getCurrentUser();

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (currentUser.getRole() != Role.ADMIN &&
                (currentUser.getRole() != Role.INSTRUCTOR ||
                        !course.getInstructor().getId().equals(currentUser.getId()))) {
            throw new RuntimeException("Access denied: You can only view enrollments of your own courses");
        }

        return enrollmentRepository.findByCourseId(courseId)
                .stream()
                .map(enrollmentMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public long getEnrollmentCountByCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new RuntimeException("Course not found");
        }
        return enrollmentRepository.countByCourseId(courseId);
    }

    @Transactional
    public void recalculateCourseProgress(Long courseId) {
        User student = userService.getCurrentUser();

        if (student.getRole() != Role.STUDENT) {
            throw new RuntimeException("Only students can update progress");
        }

        List<Enrollment> list = enrollmentRepository.findByStudentIdAndCourseId(student.getId(), courseId);
        if (list.isEmpty()) {
            throw new RuntimeException("Enrollment not found for this course");
        }

        Enrollment enrollment = list.get(0);

        long totalLessons = lessonRepository.countByCourseId(courseId);
        if (totalLessons == 0) {
            enrollment.setProgressPercentage(0);
            enrollmentRepository.save(enrollment);
            return;
        }

        long completed = activityLogRepository.countCompletedLessonsByUserAndCourse(student.getId(), courseId);

        int percentage = (int) Math.round(((double) completed / totalLessons) * 100);
        if (percentage > 100) percentage = 100;

        enrollment.setProgressPercentage(percentage);
        enrollmentRepository.save(enrollment);
    }

    public int getCourseProgress(Long courseId) {
        User student = userService.getCurrentUser();
        List<Enrollment> list = enrollmentRepository.findByStudentIdAndCourseId(student.getId(), courseId);

        if (list.isEmpty()) return 0;

        Enrollment enrollment = list.get(0);
        return enrollment.getProgressPercentage() != null ? enrollment.getProgressPercentage() : 0;
    }
}
