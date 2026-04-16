package LearnX.com.example.LearnX.Controller;

import LearnX.com.example.LearnX.dtos.CourseCreateDto;
import LearnX.com.example.LearnX.dtos.CourseResponseDto;
import LearnX.com.example.LearnX.service.CourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    public ResponseEntity<CourseResponseDto> createCourse(@RequestBody CourseCreateDto request) {
        return ResponseEntity.ok(courseService.createCourse(request));
    }
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Course API is working!");
    }

    @GetMapping("/published")
    public ResponseEntity<List<CourseResponseDto>> getPublishedCourses() {
        return ResponseEntity.ok(courseService.getPublishedCourses());
    }

    @GetMapping
    public ResponseEntity<List<CourseResponseDto>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseDto> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }
    @GetMapping("/my-courses")
    public ResponseEntity<List<CourseResponseDto>> getMyCourses() {
        return ResponseEntity.ok(courseService.getMyCourses());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseResponseDto> updateCourse(@PathVariable Long id,
                                                          @RequestBody CourseCreateDto request) {
        return ResponseEntity.ok(courseService.updateCourse(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}