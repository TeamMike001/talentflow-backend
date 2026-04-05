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

    // Student enrolls in a course
    @PostMapping
    public ResponseEntity<EnrollmentResponseDto> enroll(@RequestBody EnrollmentRequestDto request) {
        return ResponseEntity.ok(enrollmentService.enrollInCourse(request));
    }

    // Get my enrollments (for logged-in student)
    @GetMapping("/my")
    public ResponseEntity<List<EnrollmentResponseDto>> getMyEnrollments() {
        return ResponseEntity.ok(enrollmentService.getMyEnrollments());
    }

    // Get all enrollments for a specific course (Instructor/Admin only)
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<EnrollmentResponseDto>> getEnrollmentsByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByCourse(courseId));
    }
}