package LearnX.com.example.LearnX.Controller;

import LearnX.com.example.LearnX.Model.Submission;
import LearnX.com.example.LearnX.Model.User;
import LearnX.com.example.LearnX.Repository.SubmissionRepository;
import LearnX.com.example.LearnX.dtos.SubmissionRequestDto;
import LearnX.com.example.LearnX.dtos.SubmissionResponseDto;
import LearnX.com.example.LearnX.mapper.SubmissionMapper;
import LearnX.com.example.LearnX.service.FileUploadService;
import LearnX.com.example.LearnX.service.SubmissionService;
import LearnX.com.example.LearnX.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class SubmissionController {

    private final SubmissionService submissionService;
    private final FileUploadService fileUploadService;
    private final UserService userService;
    private final SubmissionRepository submissionRepository;
    private final SubmissionMapper submissionMapper;

    public SubmissionController(SubmissionService submissionService,
                                FileUploadService fileUploadService,
                                UserService userService,
                                SubmissionRepository submissionRepository,
                                SubmissionMapper submissionMapper) {
        this.submissionService = submissionService;
        this.fileUploadService = fileUploadService;
        this.userService = userService;
        this.submissionRepository = submissionRepository;
        this.submissionMapper = submissionMapper;
    }

    // 1. Upload file first → get URL
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String folder) {
        try {
            String url = fileUploadService.uploadFile(file, folder);
            return ResponseEntity.ok(url);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Upload failed: " + e.getMessage());
        }
    }

    // 2. Student submits assignment (text + attachmentUrl from step 1)
    @PostMapping("/assignments/{assignmentId}/submit")
    public ResponseEntity<SubmissionResponseDto> submitAssignment(
            @PathVariable Long assignmentId,
            @RequestBody SubmissionRequestDto request) {
        return ResponseEntity.ok(submissionService.submitAssignment(assignmentId, request));
    }

    // 3. Get my submission for a specific assignment
    @GetMapping("/assignments/{assignmentId}/my-submission")
    public ResponseEntity<?> getMySubmission(@PathVariable Long assignmentId) {
        try {
            User currentUser = userService.getCurrentUser();
            Optional<Submission> submission = submissionRepository
                    .findByAssignmentIdAndStudentId(assignmentId, currentUser.getId());

            if (submission.isPresent()) {
                return ResponseEntity.ok(submissionMapper.toResponseDto(submission.get()));
            }
            return ResponseEntity.ok(null);
        } catch (Exception e) {
            return ResponseEntity.ok(null);
        }
    }

    // 4. Grade a submission (instructor)
    @PutMapping("/submissions/{submissionId}/grade")
    public ResponseEntity<SubmissionResponseDto> gradeSubmission(
            @PathVariable Long submissionId,
            @RequestParam Integer score,
            @RequestParam(required = false) String feedback) {
        return ResponseEntity.ok(submissionService.gradeSubmission(submissionId, score, feedback));
    }

    // 5. Get all submissions for an assignment (instructor)
    @GetMapping("/assignments/{assignmentId}/submissions")
    public ResponseEntity<List<SubmissionResponseDto>> getSubmissionsByAssignment(@PathVariable Long assignmentId) {
        return ResponseEntity.ok(submissionService.getSubmissionsByAssignment(assignmentId));
    }

    // 6. Get all my submissions (student)
    @GetMapping("/assignments/my-submissions")
    public ResponseEntity<List<SubmissionResponseDto>> getMySubmissions() {
        return ResponseEntity.ok(submissionService.getMySubmissions());
    }
}