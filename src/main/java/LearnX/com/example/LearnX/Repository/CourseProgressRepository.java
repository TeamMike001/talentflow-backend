package LearnX.com.example.LearnX.Repository;

import LearnX.com.example.LearnX.Model.CourseProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

public interface CourseProgressRepository extends JpaRepository<CourseProgress, Long> {
    Optional<CourseProgress> findByStudentIdAndCourseId(Long studentId, Long courseId);
    
    List<CourseProgress> findByStudentId(Long studentId);
    
    @Modifying
    @Transactional
    @Query("UPDATE CourseProgress cp SET cp.progressPercentage = :progress, cp.totalLecturesWatched = :watched, cp.lastWatchedAt = CURRENT_TIMESTAMP WHERE cp.id = :id")
    void updateProgress(@Param("id") Long id, @Param("progress") int progress, @Param("watched") int watched);
}