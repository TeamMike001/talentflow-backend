package LearnX.com.example.LearnX.Controller;

import LearnX.com.example.LearnX.dtos.EnrollmentRequestDto;
import LearnX.com.example.LearnX.dtos.EnrollmentResponseDto;
import LearnX.com.example.LearnX.service.EnrollmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping
    public ResponseEntity<EnrollmentResponseDto> enroll(@RequestBody EnrollmentRequestDto request) {
        return ResponseEntity.ok(enrollmentService.enrollInCourse(request));
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> unenroll(@PathVariable Long courseId) {
        enrollmentService.unenrollFromCourse(courseId);
        return ResponseEntity.noContent().build();

    }


    @GetMapping("/check/{courseId}")
    public ResponseEntity<Boolean> checkEnrollment(@PathVariable Long courseId) {
        return ResponseEntity.ok(enrollmentService.isEnrolled(courseId));
    }

    @GetMapping("/my")
    public ResponseEntity<List<EnrollmentResponseDto>> getMyEnrollments() {
        return ResponseEntity.ok(enrollmentService.getMyEnrollments());
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<EnrollmentResponseDto>> getEnrollmentsByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByCourse(courseId));
    }
}