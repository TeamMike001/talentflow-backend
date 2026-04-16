    package LearnX.com.example.LearnX.Repository;

    import LearnX.com.example.LearnX.Model.Submission;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.data.jpa.repository.Query;
    import org.springframework.data.repository.query.Param;

    import java.util.List;
    import java.util.Optional;

    public interface SubmissionRepository extends JpaRepository<Submission, Long> {

        List<Submission> findByAssignmentId(Long assignmentId);

        List<Submission> findByStudentId(Long studentId);

        boolean existsByAssignmentIdAndStudentId(Long assignmentId, Long studentId);
        Optional<Submission> findByAssignmentIdAndStudentId(Long assignmentId, Long studentId);



        // Find all submissions for a specific course
        @Query("SELECT s FROM Submission s WHERE s.assignment.course.id = :courseId")
        List<Submission> findByCourseId(@Param("courseId") Long courseId);

        List<Submission> findByAssignmentIdAndIsGraded(Long assignmentId, boolean isGraded);

        @Query("SELECT s FROM Submission s WHERE s.student.id = :studentId ORDER BY s.submittedAt DESC")
        List<Submission> findAllByStudentIdOrderBySubmittedAtDesc(@Param("studentId") Long studentId);

        @Query("SELECT s FROM Submission s WHERE s.assignment.course.instructor.id = :instructorId ORDER BY s.submittedAt DESC")
        List<Submission> findAllByInstructorIdOrderBySubmittedAtDesc(@Param("instructorId") Long instructorId);

        long countByAssignmentId(Long assignmentId);

        long countByAssignmentIdAndIsGraded(Long assignmentId, boolean isGraded);

        @Query("SELECT s FROM Submission s WHERE s.student.id = :studentId AND s.assignment.course.id = :courseId")
        List<Submission> findByStudentIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);
    }