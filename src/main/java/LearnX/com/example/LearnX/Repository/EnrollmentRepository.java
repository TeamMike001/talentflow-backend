package LearnX.com.example.LearnX.Repository;

import LearnX.com.example.LearnX.Model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);
    List<Enrollment> findByStudentId(Long studentId);
    List<Enrollment> findByCourseId(Long courseId);
    // In EnrollmentRepository.java




    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.id = :courseId")
    long countByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT e FROM Enrollment e WHERE e.course.id = :courseId AND e.progressPercentage = 100")
    List<Enrollment> findCompletedEnrollmentsByCourseId(@Param("courseId") Long courseId);
    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId AND e.course.id = :courseId")
    List<Enrollment> findByStudentIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);
}