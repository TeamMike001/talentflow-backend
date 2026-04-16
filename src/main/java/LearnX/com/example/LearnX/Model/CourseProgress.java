package LearnX.com.example.LearnX.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "course_progress")
public class CourseProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;
    
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
    
    @ManyToOne
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;
    
    private int progressPercentage;
    private boolean completed;
    private int totalLecturesWatched;
    private int totalLecturesInCourse;
    private LocalDateTime lastWatchedAt;
    private LocalDateTime completedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Lecture getLecture() {
        return lecture;
    }

    public void setLecture(Lecture lecture) {
        this.lecture = lecture;
    }

    public int getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(int progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public int getTotalLecturesWatched() {
        return totalLecturesWatched;
    }

    public void setTotalLecturesWatched(int totalLecturesWatched) {
        this.totalLecturesWatched = totalLecturesWatched;
    }

    public int getTotalLecturesInCourse() {
        return totalLecturesInCourse;
    }

    public void setTotalLecturesInCourse(int totalLecturesInCourse) {
        this.totalLecturesInCourse = totalLecturesInCourse;
    }

    public LocalDateTime getLastWatchedAt() {
        return lastWatchedAt;
    }

    public void setLastWatchedAt(LocalDateTime lastWatchedAt) {
        this.lastWatchedAt = lastWatchedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}