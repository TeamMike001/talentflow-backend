package LearnX.com.example.LearnX.Controller;

import LearnX.com.example.LearnX.dtos.SubmissionRequestDto;
import LearnX.com.example.LearnX.dtos.SubmissionResponseDto;
import LearnX.com.example.LearnX.service.FileUploadService;
import LearnX.com.example.LearnX.service.SubmissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class SubmissionController {

    private final SubmissionService submissionService;
    private final FileUploadService fileUploadService;

    public SubmissionController(SubmissionService submissionService, FileUploadService fileUploadService) {
        this.submissionService = submissionService;
        this.fileUploadService = fileUploadService;
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

    @PutMapping("/submissions/{submissionId}/grade")
    public ResponseEntity<SubmissionResponseDto> gradeSubmission(
            @PathVariable Long submissionId,
            @RequestParam Integer score,
            @RequestParam(required = false) String feedback) {

        return ResponseEntity.ok(submissionService.gradeSubmission(submissionId, score, feedback));
    }

    @GetMapping("/assignments/{assignmentId}/submissions")
    public ResponseEntity<List<SubmissionResponseDto>> getSubmissionsByAssignment(@PathVariable Long assignmentId) {
        return ResponseEntity.ok(submissionService.getSubmissionsByAssignment(assignmentId));
    }

    @GetMapping("/assignments/my-submissions")
    public ResponseEntity<List<SubmissionResponseDto>> getMySubmissions() {
        return ResponseEntity.ok(submissionService.getMySubmissions());
    }
}