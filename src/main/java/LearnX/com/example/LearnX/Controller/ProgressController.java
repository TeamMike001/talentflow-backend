package LearnX.com.example.LearnX.Controller;

import LearnX.com.example.LearnX.service.CourseProgressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {
    
    private final CourseProgressService progressService;
    
    public ProgressController(CourseProgressService progressService) {
        this.progressService = progressService;
    }
    
    @PostMapping("/courses/{courseId}/lectures/{lectureId}/watch")
    public ResponseEntity<?> markLectureWatched(
            @PathVariable Long courseId,
            @PathVariable Long lectureId) {
        progressService.markLectureAsWatched(courseId, lectureId);
        return ResponseEntity.ok(Map.of("success", true));
    }
    
    @GetMapping("/courses/{courseId}")
    public ResponseEntity<Map<String, Integer>> getCourseProgress(@PathVariable Long courseId) {
        int progress = progressService.getCourseProgress(courseId);
        return ResponseEntity.ok(Map.of("progress", progress));
    }
}