package LearnX.com.example.LearnX.service;

import LearnX.com.example.LearnX.Enum.Role;
import LearnX.com.example.LearnX.Model.Assignment;
import LearnX.com.example.LearnX.Model.Course;
import LearnX.com.example.LearnX.Model.User;
import LearnX.com.example.LearnX.Repository.AssignmentRepository;
import LearnX.com.example.LearnX.Repository.CourseRepository;
import LearnX.com.example.LearnX.dtos.AssignmentRequestDto;
import LearnX.com.example.LearnX.dtos.AssignmentResponseDto;
import LearnX.com.example.LearnX.mapper.AssignmentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;
    private final AssignmentMapper assignmentMapper;
    private final UserService userService;

    public AssignmentService(AssignmentRepository assignmentRepository,
                             CourseRepository courseRepository,
                             AssignmentMapper assignmentMapper,
                             UserService userService) {
        this.assignmentRepository = assignmentRepository;
        this.courseRepository = courseRepository;
        this.assignmentMapper = assignmentMapper;
        this.userService = userService;
    }

    // ====================== CREATE ASSIGNMENT (Instructor/Admin only) ======================
    @Transactional
    public AssignmentResponseDto createAssignment(Long courseId, AssignmentRequestDto request) {
        User currentUser = userService.getCurrentUser();

        if (currentUser.getRole() != Role.INSTRUCTOR && currentUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("Only instructors and admins can create assignments");
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Only course owner or Admin can create assignment
        if (currentUser.getRole() != Role.ADMIN &&
                !course.getInstructor().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only create assignments for your own courses");
        }

        Assignment assignment = assignmentMapper.toEntity(request);
        assignment.setCourse(course);

        Assignment savedAssignment = assignmentRepository.save(assignment);
        return assignmentMapper.toResponseDto(savedAssignment);
    }

    // ====================== GET ALL ASSIGNMENTS FOR A COURSE ======================
    public List<AssignmentResponseDto> getAssignmentsByCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        User currentUser = userService.getCurrentUser();

        // Students can only see assignments if course is published
        if (currentUser.getRole() == Role.STUDENT && !course.isPublished()) {
            throw new RuntimeException("Access denied: Course is not published");
        }

        return assignmentRepository.findByCourseId(courseId)
                .stream()
                .map(assignmentMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    // ====================== GET SINGLE ASSIGNMENT ======================
    public AssignmentResponseDto getAssignmentById(Long assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        User currentUser = userService.getCurrentUser();

        if (currentUser.getRole() == Role.STUDENT && !assignment.getCourse().isPublished()) {
            throw new RuntimeException("Access denied: Course is not published");
        }

        return assignmentMapper.toResponseDto(assignment);
    }

    // ====================== UPDATE ASSIGNMENT ======================
    @Transactional
    public AssignmentResponseDto updateAssignment(Long assignmentId, AssignmentRequestDto request) {
        User currentUser = userService.getCurrentUser();

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        if (currentUser.getRole() != Role.ADMIN &&
                !assignment.getCourse().getInstructor().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only update your own assignments");
        }

        assignment.setTitle(request.title());
        assignment.setDescription(request.description());
        assignment.setDueDate(request.dueDate());
        assignment.setMaxScore(request.maxScore());

        Assignment updated = assignmentRepository.save(assignment);
        return assignmentMapper.toResponseDto(updated);
    }

    // ====================== DELETE ASSIGNMENT ======================
    @Transactional
    public void deleteAssignment(Long assignmentId) {
        User currentUser = userService.getCurrentUser();

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        if (currentUser.getRole() != Role.ADMIN &&
                !assignment.getCourse().getInstructor().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only delete your own assignments");
        }

        assignmentRepository.delete(assignment);
    }
}