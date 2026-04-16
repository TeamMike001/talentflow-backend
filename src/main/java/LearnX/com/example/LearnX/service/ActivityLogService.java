package LearnX.com.example.LearnX.service;

import LearnX.com.example.LearnX.Enum.Role;
import LearnX.com.example.LearnX.Model.ActivityLog;
import LearnX.com.example.LearnX.Model.Course;
import LearnX.com.example.LearnX.Model.Lesson;
import LearnX.com.example.LearnX.Model.User;
import LearnX.com.example.LearnX.Repository.ActivityLogRepository;
import LearnX.com.example.LearnX.Repository.CourseRepository;
import LearnX.com.example.LearnX.Repository.LessonRepository;
import LearnX.com.example.LearnX.dtos.ActivityLogResponseDto;
import LearnX.com.example.LearnX.mapper.ActivityLogMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;
    private final ActivityLogMapper activityLogMapper;
    private final UserService userService;
    private final EnrollmentService enrollmentService;
    private final CertificateService certificateService;

    public ActivityLogService(ActivityLogRepository activityLogRepository,
                              LessonRepository lessonRepository,
                              CourseRepository courseRepository,
                              ActivityLogMapper activityLogMapper,
                              UserService userService,
                              EnrollmentService enrollmentService,
                              CertificateService certificateService) {
        this.activityLogRepository = activityLogRepository;
        this.lessonRepository = lessonRepository;
        this.courseRepository = courseRepository;
        this.activityLogMapper = activityLogMapper;
        this.userService = userService;
        this.enrollmentService = enrollmentService;
        this.certificateService = certificateService;
    }

    @Transactional
    public ActivityLogResponseDto markLessonAsCompleted(Long lessonId) {
        User student = userService.getCurrentUser();

        if (student.getRole() != Role.STUDENT) {
            throw new RuntimeException("Only students can mark lessons as completed");
        }

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        boolean alreadyCompleted = activityLogRepository.existsByUserIdAndLessonIdAndCompletedTrue(
                student.getId(), lessonId);

        if (alreadyCompleted) {
            throw new RuntimeException("This lesson is already marked as completed");
        }

        ActivityLog activityLog = new ActivityLog();
        activityLog.setAction("COMPLETED_LESSON");
        activityLog.setLesson(lesson);
        activityLog.setUser(student);
        activityLog.setCompleted(true);

        ActivityLog savedLog = activityLogRepository.save(activityLog);

        // Recalculate progress for this course
        enrollmentService.recalculateCourseProgress(lesson.getCourse().getId());

        // Get updated progress to check for certificate
        int progress = enrollmentService.getCourseProgress(lesson.getCourse().getId());
        if (progress == 100) {
            certificateService.generateCertificateIfCompleted(lesson.getCourse().getId());
        }

        return activityLogMapper.toResponseDto(savedLog);
    }

    public List<ActivityLogResponseDto> getMyActivityLogs() {
        User student = userService.getCurrentUser();
        if (student.getRole() != Role.STUDENT) {
            throw new RuntimeException("Only students can view their own activity logs");
        }
        return activityLogRepository.findByUserId(student.getId())
                .stream()
                .map(activityLogMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<ActivityLogResponseDto> getActivityLogsByLesson(Long lessonId) {
        User currentUser = userService.getCurrentUser();
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        if (currentUser.getRole() == Role.ADMIN) {
            return activityLogRepository.findByLessonId(lessonId)
                    .stream()
                    .map(activityLogMapper::toResponseDto)
                    .collect(Collectors.toList());
        }

        if (currentUser.getRole() == Role.INSTRUCTOR) {
            if (!lesson.getCourse().getInstructor().getId().equals(currentUser.getId())) {
                throw new RuntimeException("Access denied: You can only view activity logs for your own courses");
            }
            return activityLogRepository.findByLessonId(lessonId)
                    .stream()
                    .map(activityLogMapper::toResponseDto)
                    .collect(Collectors.toList());
        }

        throw new RuntimeException("Access denied");
    }

    public List<ActivityLogResponseDto> getCompletedLessonsByCourse(Long courseId) {
        User student = userService.getCurrentUser();
        if (student.getRole() != Role.STUDENT) {
            throw new RuntimeException("Only students can view their completed lessons");
        }
        return activityLogRepository.findCompletedByUserIdAndCourseId(student.getId(), courseId)
                .stream()
                .map(activityLogMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<ActivityLogResponseDto> getActivityLogsByCourseAndStudent(Long courseId, Long studentId) {
        User currentUser = userService.getCurrentUser();

        if (currentUser.getRole() == Role.ADMIN) {
            return activityLogRepository.findByUserIdAndCourseId(studentId, courseId)
                    .stream()
                    .map(activityLogMapper::toResponseDto)
                    .collect(Collectors.toList());
        }

        if (currentUser.getRole() == Role.INSTRUCTOR) {
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("Course not found"));
            if (!course.getInstructor().getId().equals(currentUser.getId())) {
                throw new RuntimeException("Access denied: You can only view students in your own courses");
            }
            return activityLogRepository.findByUserIdAndCourseId(studentId, courseId)
                    .stream()
                    .map(activityLogMapper::toResponseDto)
                    .collect(Collectors.toList());
        }

        throw new RuntimeException("Access denied");
    }
}