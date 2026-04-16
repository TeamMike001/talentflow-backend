package LearnX.com.example.LearnX.Repository;

import LearnX.com.example.LearnX.Model.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    Optional<Certificate> findByStudentIdAndCourseId(Long studentId, Long courseId);
    List<Certificate> findByStudentId(Long studentId);
}