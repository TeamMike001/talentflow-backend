package LearnX.com.example.LearnX.Repository;

import LearnX.com.example.LearnX.Model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByCourseIdOrderByOrderIndex(Long courseId);
}