package LearnX.com.example.LearnX.Repository;

import LearnX.com.example.LearnX.Model.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findByUserId(Long userId);
    List<ActivityLog> findByLessonId(Long lessonId);
}