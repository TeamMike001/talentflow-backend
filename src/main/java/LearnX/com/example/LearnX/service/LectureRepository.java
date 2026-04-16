package LearnX.com.example.LearnX.service;

import LearnX.com.example.LearnX.Model.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LectureRepository extends JpaRepository<Lecture, Long> {
    @Query("SELECT COUNT(l) FROM Lecture l WHERE l.section.course.id = :courseId")
    int countByCourseId(@Param("courseId") Long courseId);
}
