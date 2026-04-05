package LearnX.com.example.LearnX.Repository;

import LearnX.com.example.LearnX.Model.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    boolean existsByUserIdAndLessonIdAndCompletedTrue(Long userId, Long lessonId);

    List<ActivityLog> findByLessonId(Long lessonId);

    List<ActivityLog> findByUserId(Long userId);

    @Query("SELECT a FROM ActivityLog a WHERE a.user.id = :userId AND a.lesson.course.id = :courseId AND a.completed = true")
    List<ActivityLog> findCompletedByUserIdAndCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);

    @Query("SELECT COUNT(a) FROM ActivityLog a WHERE a.user.id = :userId AND a.lesson.course.id = :courseId AND a.completed = true")
    long countCompletedLessonsByUserAndCourse(@Param("userId") Long userId, @Param("courseId") Long courseId);
    List<ActivityLog> findByUserIdAndCourseId(Long userId, Long courseId);
}