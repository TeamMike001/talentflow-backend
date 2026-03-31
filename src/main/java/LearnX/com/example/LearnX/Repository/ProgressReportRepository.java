package LearnX.com.example.LearnX.Repository;

import LearnX.com.example.LearnX.Model.ProgressReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProgressReportRepository extends JpaRepository<ProgressReport, Long> {
    List<ProgressReport> findByUserId(Long userId);
}