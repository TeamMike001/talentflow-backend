package LearnX.com.example.LearnX.service;

import LearnX.com.example.LearnX.Model.*;
import LearnX.com.example.LearnX.Repository.CourseProgressRepository;
import LearnX.com.example.LearnX.Repository.EnrollmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CourseProgressService {
    
    private final CourseProgressRepository progressRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LectureRepository lectureRepository;
    private final UserService userService;
    private final CertificateService certificateService;
    private final CourseService courseService;
    
    public CourseProgressService(CourseProgressRepository progressRepository,
                                 EnrollmentRepository enrollmentRepository,
                                 LectureRepository lectureRepository,
                                 UserService userService,
                                 CertificateService certificateService, CourseService courseService) {
        this.progressRepository = progressRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.lectureRepository = lectureRepository;
        this.userService = userService;
        this.certificateService = certificateService;
        this.courseService = courseService;
    }
    
    @Transactional
    public void markLectureAsWatched(Long courseId, Long lectureId) {
        User student = userService.getCurrentUser();
        
        // Get or create progress record
        CourseProgress progress = progressRepository
            .findByStudentIdAndCourseId(student.getId(), courseId)
            .orElseGet(() -> {
                CourseProgress newProgress = new CourseProgress();
                newProgress.setStudent(student);
                Course course = courseService.getCourseEntityById(courseId);
                newProgress.setCourse(course);
                
                // Get total lectures in course
                int totalLectures = lectureRepository.countByCourseId(courseId);
                newProgress.setTotalLecturesInCourse(totalLectures);
                newProgress.setProgressPercentage(0);
                newProgress.setCompleted(false);
                newProgress.setTotalLecturesWatched(0);
                return progressRepository.save(newProgress);
            });
        
        // Check if this lecture is already watched
        if (isLectureWatched(progress, lectureId)) {
            return;
        }
        
        // Mark lecture as watched (you'd need a separate entity for watched lectures)
        markLectureAsWatchedInProgress(progress, lectureId);
        
        // Update progress
        int watchedCount = progress.getTotalLecturesWatched() + 1;
        int totalLectures = progress.getTotalLecturesInCourse();
        int newProgressPercentage = (watchedCount * 100) / totalLectures;
        
        progress.setTotalLecturesWatched(watchedCount);
        progress.setProgressPercentage(newProgressPercentage);
        progress.setLastWatchedAt(LocalDateTime.now());
        
        // Check if completed (100%)
        if (newProgressPercentage >= 100 && !progress.isCompleted()) {
            progress.setCompleted(true);
            progress.setCompletedAt(LocalDateTime.now());
            progressRepository.save(progress);
            
            // Update enrollment progress
            updateEnrollmentProgress(student.getId(), courseId, 100);
            
            // Generate certificate automatically
            certificateService.generateCertificateIfCompleted(courseId);
        } else {
            progressRepository.save(progress);
            
            // Update enrollment progress
            updateEnrollmentProgress(student.getId(), courseId, newProgressPercentage);
        }
    }
    
    private boolean isLectureWatched(CourseProgress progress, Long lectureId) {
        // You'd need a WatchedLecture entity to track individual lectures
        // For now, we'll assume a method that checks if lecture is already watched
        return false;
    }
    
    private void markLectureAsWatchedInProgress(CourseProgress progress, Long lectureId) {
        // Track individual lecture completion
        // This would involve creating a WatchedLecture record
    }
    
    private void updateEnrollmentProgress(Long studentId, Long courseId, int progress) {
        List<Enrollment> enrollments = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId);
        if (!enrollments.isEmpty()) {
            Enrollment enrollment = enrollments.get(0);
            enrollment.setProgressPercentage(progress);
            enrollmentRepository.save(enrollment);
        }
    }
    
    public int getCourseProgress(Long courseId) {
        User student = userService.getCurrentUser();
        return progressRepository.findByStudentIdAndCourseId(student.getId(), courseId)
            .map(CourseProgress::getProgressPercentage)
            .orElse(0);
    }
}