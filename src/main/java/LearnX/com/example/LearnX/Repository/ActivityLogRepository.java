package LearnX.com.example.LearnX.Repository;

import LearnX.com.example.LearnX.Model.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    List<ActivityLog> findByUserId(Long userId);

    List<ActivityLog> findByLessonId(Long lessonId);

    boolean existsByUserIdAndLessonIdAndCompletedTrue(Long userId, Long lessonId);


    @Query("SELECT COUNT(a) FROM ActivityLog a WHERE a.user.id = :userId AND a.lesson.section.course.id = :courseId AND a.completed = true")
    long countCompletedLessonsByUserAndCourse(@Param("userId") Long userId, @Param("courseId") Long courseId);

    @Query("SELECT a FROM ActivityLog a WHERE a.user.id = :userId AND a.lesson.section.course.id = :courseId")
    List<ActivityLog> findByUserIdAndCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);

    @Query("SELECT a FROM ActivityLog a WHERE a.user.id = :userId AND a.lesson.section.course.id = :courseId AND a.completed = true")
    List<ActivityLog> findCompletedByUserIdAndCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);
}