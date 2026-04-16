package LearnX.com.example.LearnX.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String subtitle;
    private String category;
    private String subcategory;
    private String topic;
    private String language;
    private String level;
    private String duration;
    private String thumbnailUrl;
    private String trailerUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "course_teaches", joinColumns = @JoinColumn(name = "course_id"))
    @Column(name = "item")
    private List<String> teaches = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "course_audience", joinColumns = @JoinColumn(name = "course_id"))
    @Column(name = "item")
    private List<String> audience = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "course_requirements", joinColumns = @JoinColumn(name = "course_id"))
    @Column(name = "item")
    private List<String> requirements = new ArrayList<>();

    private String welcomeMessage;
    private String congratsMessage;

    @ManyToOne
    @JoinColumn(name = "instructor_id")
    private User instructor;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "course_additional_instructors",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "instructor_id")
    )
    private List<User> additionalInstructors = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Section> sections = new ArrayList<>();

    private boolean published = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Default constructor
    public Course() {}

    // Convenience constructor (optional)
    public Course(Long id, String title, String subtitle, String category, String subcategory,
                  String topic, String language, String level, String duration, String thumbnailUrl,
                  String trailerUrl, String description, List<String> teaches, List<String> audience,
                  List<String> requirements, String welcomeMessage, String congratsMessage,
                  User instructor, List<User> additionalInstructors, List<Section> sections,
                  boolean published, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.category = category;
        this.subcategory = subcategory;
        this.topic = topic;
        this.language = language;
        this.level = level;
        this.duration = duration;
        this.thumbnailUrl = thumbnailUrl;
        this.trailerUrl = trailerUrl;
        this.description = description;
        this.teaches = teaches;
        this.audience = audience;
        this.requirements = requirements;
        this.welcomeMessage = welcomeMessage;
        this.congratsMessage = congratsMessage;
        this.instructor = instructor;
        this.additionalInstructors = additionalInstructors;
        this.sections = sections;
        this.published = published;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and setters
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

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getTrailerUrl() {
        return trailerUrl;
    }

    public void setTrailerUrl(String trailerUrl) {
        this.trailerUrl = trailerUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getTeaches() {
        return teaches;
    }

    public void setTeaches(List<String> teaches) {
        this.teaches = teaches;
    }

    public List<String> getAudience() {
        return audience;
    }

    public void setAudience(List<String> audience) {
        this.audience = audience;
    }

    public List<String> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<String> requirements) {
        this.requirements = requirements;
    }

    public String getWelcomeMessage() {
        return welcomeMessage;
    }

    public void setWelcomeMessage(String welcomeMessage) {
        this.welcomeMessage = welcomeMessage;
    }

    public String getCongratsMessage() {
        return congratsMessage;
    }

    public void setCongratsMessage(String congratsMessage) {
        this.congratsMessage = congratsMessage;
    }

    public User getInstructor() {
        return instructor;
    }

    public void setInstructor(User instructor) {
        this.instructor = instructor;
    }

    public List<User> getAdditionalInstructors() {
        return additionalInstructors;
    }

    public void setAdditionalInstructors(List<User> additionalInstructors) {
        this.additionalInstructors = additionalInstructors;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}