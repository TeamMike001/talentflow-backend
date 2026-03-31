package LearnX.com.example.LearnX.Model;

import jakarta.persistence.*;


@Entity
@Table(name = "lessons")

public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;

    private String attachmentUrl;

    private int orderIndex;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    public Lesson(Long id, String title, String content, String attachmentUrl, int orderIndex, Course course) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.attachmentUrl = attachmentUrl;
        this.orderIndex = orderIndex;
        this.course = course;
    }

    public Lesson() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}