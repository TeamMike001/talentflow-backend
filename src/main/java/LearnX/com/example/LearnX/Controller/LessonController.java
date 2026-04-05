package LearnX.com.example.LearnX.Controller;

import LearnX.com.example.LearnX.dtos.LessonRequestDto;
import LearnX.com.example.LearnX.dtos.LessonResponseDto;
import LearnX.com.example.LearnX.service.LessonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses/{courseId}/lessons")
public class LessonController {

    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @PostMapping
    public ResponseEntity<LessonResponseDto> addLesson(
            @PathVariable Long courseId,
            @RequestBody LessonRequestDto request) {
        return ResponseEntity.ok(lessonService.addLessonToCourse(courseId, request));
    }

    @GetMapping
    public ResponseEntity<List<LessonResponseDto>> getLessonsByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(lessonService.getLessonsByCourseId(courseId));
    }

    @GetMapping("/{lessonId}")
    public ResponseEntity<LessonResponseDto> getLessonById(
            @PathVariable Long courseId,
            @PathVariable Long lessonId) {
        return ResponseEntity.ok(lessonService.getLessonById(lessonId));
    }

    @PutMapping("/{lessonId}")
    public ResponseEntity<LessonResponseDto> updateLesson(
            @PathVariable Long lessonId,
            @RequestBody LessonRequestDto request) {
        return ResponseEntity.ok(lessonService.updateLesson(lessonId, request));
    }

    @DeleteMapping("/{lessonId}")
    public ResponseEntity<Void> deleteLesson(@PathVariable Long lessonId) {
        lessonService.deleteLesson(lessonId);
        return ResponseEntity.noContent().build();
    }
}