package LearnX.com.example.LearnX.Repository;

import LearnX.com.example.LearnX.Model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    List<Submission> findByAssignmentId(Long assignmentId);

    List<Submission> findByStudentId(Long studentId);

    boolean existsByAssignmentIdAndStudentId(Long assignmentId, Long studentId);
}