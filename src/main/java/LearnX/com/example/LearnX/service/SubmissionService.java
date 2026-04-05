package LearnX.com.example.LearnX.service;

import LearnX.com.example.LearnX.Enum.Role;
import LearnX.com.example.LearnX.Model.Assignment;
import LearnX.com.example.LearnX.Model.Submission;
import LearnX.com.example.LearnX.Model.User;
import LearnX.com.example.LearnX.Repository.AssignmentRepository;
import LearnX.com.example.LearnX.Repository.EnrollmentRepository;
import LearnX.com.example.LearnX.Repository.SubmissionRepository;
import LearnX.com.example.LearnX.dtos.SubmissionRequestDto;
import LearnX.com.example.LearnX.dtos.SubmissionResponseDto;
import LearnX.com.example.LearnX.mapper.SubmissionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final SubmissionMapper submissionMapper;
    private final UserService userService;
    private final NotificationService notificationService;   // Injected for notifications

    public SubmissionService(SubmissionRepository submissionRepository,
                             AssignmentRepository assignmentRepository,
                             EnrollmentRepository enrollmentRepository,
                             SubmissionMapper submissionMapper,
                             UserService userService,
                             NotificationService notificationService) {
        this.submissionRepository = submissionRepository;
        this.assignmentRepository = assignmentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.submissionMapper = submissionMapper;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    /**
     * Student submits an assignment (text + optional file via attachmentUrl)
     */
    @Transactional
    public SubmissionResponseDto submitAssignment(Long assignmentId, SubmissionRequestDto request) {
        User student = userService.getCurrentUser();

        if (student.getRole() != Role.STUDENT) {
            throw new RuntimeException("Only students can submit assignments");
        }

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        // 1. Due Date Validation
        if (assignment.getDueDate() != null && LocalDateTime.now().isAfter(assignment.getDueDate())) {
            throw new RuntimeException("Cannot submit: The due date for this assignment has already passed.");
        }

        // 2. Enrollment Check
        boolean isEnrolled = enrollmentRepository.existsByStudentIdAndCourseId(
                student.getId(), assignment.getCourse().getId());

        if (!isEnrolled) {
            throw new RuntimeException("You must be enrolled in this course to submit the assignment.");
        }

        // 3. Prevent duplicate submission
        boolean alreadySubmitted = submissionRepository.existsByAssignmentIdAndStudentId(
                assignmentId, student.getId());

        if (alreadySubmitted) {
            throw new RuntimeException("You have already submitted this assignment.");
        }

        Submission submission = submissionMapper.toEntity(request);
        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setSubmittedAt(LocalDateTime.now());

        Submission savedSubmission = submissionRepository.save(submission);

        return submissionMapper.toResponseDto(savedSubmission);
    }

    /**
     * Get all submissions for a specific assignment (Instructor / Admin only)
     */
    public List<SubmissionResponseDto> getSubmissionsByAssignment(Long assignmentId) {
        User currentUser = userService.getCurrentUser();

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        if (currentUser.getRole() != Role.ADMIN &&
                (currentUser.getRole() != Role.INSTRUCTOR ||
                        !assignment.getCourse().getInstructor().getId().equals(currentUser.getId()))) {
            throw new RuntimeException("Access denied: You can only view submissions for your own assignments.");
        }

        return submissionRepository.findByAssignmentId(assignmentId)
                .stream()
                .map(submissionMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Grade a student's submission + Send Notification
     */
    @Transactional
    public SubmissionResponseDto gradeSubmission(Long submissionId, Integer score, String feedback) {
        User currentUser = userService.getCurrentUser();

        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        // Security check
        if (currentUser.getRole() != Role.ADMIN &&
                (currentUser.getRole() != Role.INSTRUCTOR ||
                        !submission.getAssignment().getCourse().getInstructor().getId().equals(currentUser.getId()))) {
            throw new RuntimeException("Access denied: You can only grade submissions for your own assignments.");
        }

        // Validate score
        if (score < 0 || score > submission.getAssignment().getMaxScore()) {
            throw new RuntimeException("Score must be between 0 and " + submission.getAssignment().getMaxScore());
        }

        submission.setScore(score);
        submission.setFeedback(feedback != null ? feedback : "");
        submission.setGradedAt(LocalDateTime.now());

        Submission gradedSubmission = submissionRepository.save(submission);

        // === SEND NOTIFICATION TO STUDENT ===
        String title = "Assignment Graded";
        String message = "Your submission for '" + submission.getAssignment().getTitle() +
                "' has been graded. Score: " + score + "/" + submission.getAssignment().getMaxScore();

        notificationService.sendNotification(submission.getStudent(), title, message);

        return submissionMapper.toResponseDto(gradedSubmission);
    }


    public List<SubmissionResponseDto> getMySubmissions() {
        User student = userService.getCurrentUser();

        if (student.getRole() != Role.STUDENT) {
            throw new RuntimeException("Only students can view their submissions.");
        }

        return submissionRepository.findByStudentId(student.getId())
                .stream()
                .map(submissionMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}