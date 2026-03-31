package LearnX.com.example.LearnX.Model;

import jakarta.persistence.*;


import java.time.LocalDateTime;

@Entity
@Table(name = "submissions")

public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "assignment_id")
    private Assignment assignment;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    private String content;
    private LocalDateTime submittedAt = LocalDateTime.now();
    private Integer score;
    private String feedback;

    private LocalDateTime gradedAt;

    public Submission(Long id, Assignment assignment, User student, String content, LocalDateTime submittedAt, Integer score, String feedback, LocalDateTime gradedAt) {
        this.id = id;
        this.assignment = assignment;
        this.student = student;
        this.content = content;
        this.submittedAt = submittedAt;
        this.score = score;
        this.feedback = feedback;
        this.gradedAt = gradedAt;

    }

    public Submission() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public LocalDateTime getGradedAt() {
        return gradedAt;
    }

    public void setGradedAt(LocalDateTime gradedAt) {
        this.gradedAt = gradedAt;
    }

}