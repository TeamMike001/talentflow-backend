package LearnX.com.example.LearnX.Controller;

import LearnX.com.example.LearnX.dtos.ActivityLogResponseDto;
import LearnX.com.example.LearnX.service.ActivityLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity-logs")
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    public ActivityLogController(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    // Student marks lesson as completed
    @PostMapping("/lessons/{lessonId}/complete")
    public ResponseEntity<ActivityLogResponseDto> markLessonCompleted(@PathVariable Long lessonId) {
        return ResponseEntity.ok(activityLogService.markLessonAsCompleted(lessonId));
    }

    // Student views their own activity logs
    @GetMapping("/my")
    public ResponseEntity<List<ActivityLogResponseDto>> getMyActivityLogs() {
        return ResponseEntity.ok(activityLogService.getMyActivityLogs());
    }

    // Student views their completed lessons in a course
    @GetMapping("/course/{courseId}/completed")
    public ResponseEntity<List<ActivityLogResponseDto>> getCompletedLessonsByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(activityLogService.getCompletedLessonsByCourse(courseId));
    }

    // Instructor / Admin views activity logs for a specific lesson
    @GetMapping("/lessons/{lessonId}")
    public ResponseEntity<List<ActivityLogResponseDto>> getActivityLogsByLesson(@PathVariable Long lessonId) {
        return ResponseEntity.ok(activityLogService.getActivityLogsByLesson(lessonId));
    }

    // NEW: Instructor / Admin can view any student's activity in a course
    @GetMapping("/course/{courseId}/student/{studentId}")
    public ResponseEntity<List<ActivityLogResponseDto>> getActivityLogsByCourseAndStudent(
            @PathVariable Long courseId,
            @PathVariable Long studentId) {
        return ResponseEntity.ok(activityLogService.getActivityLogsByCourseAndStudent(courseId, studentId));
    }
}