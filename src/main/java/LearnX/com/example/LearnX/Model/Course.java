package LearnX.com.example.LearnX.Model;

import LearnX.com.example.LearnX.Enum.TaskStatus;
import jakarta.persistence.*;


import java.time.LocalDateTime;

@Entity
@Table(name = "Course")

public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    private String thumbnail;
    private String title;

    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "instructor_id")
    private User instructor;
    private LocalDateTime createdAt;
    private boolean published = false;

    public Course(Long id, String description, String thumbnail, String title, TaskStatus status, User instructor, LocalDateTime createdAt, boolean published) {
        this.id = id;
        this.description = description;
        this.thumbnail = thumbnail;
        this.title = title;
        this.status = status;
        this.instructor = instructor;
        this.createdAt = createdAt;
        this.published = published;
    }
    public Course() {}


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public User getInstructor() {
        return instructor;
    }

    public void setInstructor(User instructor) {
        this.instructor = instructor;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }
}