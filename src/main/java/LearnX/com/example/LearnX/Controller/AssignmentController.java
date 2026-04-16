package LearnX.com.example.LearnX.Controller;

import LearnX.com.example.LearnX.dtos.AssignmentRequestDto;
import LearnX.com.example.LearnX.dtos.AssignmentResponseDto;
import LearnX.com.example.LearnX.dtos.SubmissionResponseDto;
import LearnX.com.example.LearnX.service.AssignmentService;
import LearnX.com.example.LearnX.service.SubmissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses/{courseId}/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final SubmissionService submissionService;

    public AssignmentController(AssignmentService assignmentService, SubmissionService submissionService) {
        this.assignmentService = assignmentService;
        this.submissionService = submissionService;
    }

    @PostMapping
    public ResponseEntity<AssignmentResponseDto> createAssignment(
            @PathVariable Long courseId,
            @RequestBody AssignmentRequestDto request) {
        return ResponseEntity.ok(assignmentService.createAssignment(courseId, request));
    }
    @GetMapping("/{assignmentId}/my-submission")
    public ResponseEntity<SubmissionResponseDto> getMySubmission(@PathVariable Long assignmentId) {
        return ResponseEntity.ok(submissionService.getMySubmissionForAssignment(assignmentId));
    }

    @GetMapping
    public ResponseEntity<List<AssignmentResponseDto>> getAssignmentsByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(assignmentService.getAssignmentsByCourse(courseId));
    }

    @GetMapping("/{assignmentId}")
    public ResponseEntity<AssignmentResponseDto> getAssignmentById(
            @PathVariable Long assignmentId) {
        return ResponseEntity.ok(assignmentService.getAssignmentById(assignmentId));
    }

    @PutMapping("/{assignmentId}")
    public ResponseEntity<AssignmentResponseDto> updateAssignment(
            @PathVariable Long assignmentId,
            @RequestBody AssignmentRequestDto request) {
        return ResponseEntity.ok(assignmentService.updateAssignment(assignmentId, request));
    }

    @DeleteMapping("/{assignmentId}")
    public ResponseEntity<Void> deleteAssignment(@PathVariable Long assignmentId) {
        assignmentService.deleteAssignment(assignmentId);
        return ResponseEntity.noContent().build();
    }
}