package LearnX.com.example.LearnX.Model;

import jakarta.persistence.*;


import java.time.LocalDateTime;

@Entity
@Table(name = "enrollments")

public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    private LocalDateTime enrolledAt = LocalDateTime.now();

    public Enrollment(Long id, User student, Course course, LocalDateTime enrolledAt) {
        this.id = id;
        this.student = student;
        this.course = course;
        this.enrolledAt = enrolledAt;
    }
    public Enrollment() {}

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

    public LocalDateTime getEnrolledAt() {
        return enrolledAt;
    }

    public void setEnrolledAt(LocalDateTime enrolledAt) {
        this.enrolledAt = enrolledAt;
    }
}