package LearnX.com.example.LearnX.service;

import LearnX.com.example.LearnX.Enum.Role;
import LearnX.com.example.LearnX.Model.Course;
import LearnX.com.example.LearnX.Model.Lesson;
import LearnX.com.example.LearnX.Model.User;
import LearnX.com.example.LearnX.Repository.CourseRepository;
import LearnX.com.example.LearnX.Repository.LessonRepository;
import LearnX.com.example.LearnX.dtos.LessonRequestDto;
import LearnX.com.example.LearnX.dtos.LessonResponseDto;
import LearnX.com.example.LearnX.mapper.LessonMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LessonService {

    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;
    private final LessonMapper lessonMapper;
    private final UserService userService;

    public LessonService(LessonRepository lessonRepository,
                         CourseRepository courseRepository,
                         LessonMapper lessonMapper,
                         UserService userService) {
        this.lessonRepository = lessonRepository;
        this.courseRepository = courseRepository;
        this.lessonMapper = lessonMapper;
        this.userService = userService;
    }

    @Transactional
    public LessonResponseDto addLessonToCourse(Long courseId, LessonRequestDto requestDto) {
        User currentUser = userService.getCurrentUser();

        if (currentUser.getRole() != Role.INSTRUCTOR && currentUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("Access denied: Only instructors and admins can add lessons");
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));

        // Only the course owner (instructor) or Admin can add lessons to this course
        if (currentUser.getRole() != Role.ADMIN &&
                !course.getInstructor().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied: You can only add lessons to your own courses");
        }

        Lesson lesson = lessonMapper.toEntity(requestDto);
        lesson.setCourse(course);

        Lesson savedLesson = lessonRepository.save(lesson);
        return lessonMapper.toResponseDto(savedLesson);
    }

    // ====================== GET ALL LESSONS OF A COURSE ======================
    public List<LessonResponseDto> getLessonsByCourseId(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        User currentUser = userService.getCurrentUser();

        // Students can only see lessons if course is published
        if (currentUser.getRole() == Role.STUDENT && !course.isPublished()) {
            throw new RuntimeException("Access denied: This course is not published yet");
        }

        return lessonRepository.findByCourseIdOrderByOrderIndexAsc(courseId)
                .stream()
                .map(lessonMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    // ====================== GET SINGLE LESSON ======================
    public LessonResponseDto getLessonById(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        User currentUser = userService.getCurrentUser();

        if (currentUser.getRole() == Role.STUDENT && !lesson.getCourse().isPublished()) {
            throw new RuntimeException("Access denied: Course is not published");
        }

        return lessonMapper.toResponseDto(lesson);
    }

    @Transactional
    public LessonResponseDto updateLesson(Long lessonId, LessonRequestDto requestDto) {
        User currentUser = userService.getCurrentUser();

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        // Only owner or Admin
        if (currentUser.getRole() != Role.ADMIN &&
                !lesson.getCourse().getInstructor().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied: You can only edit lessons in your own courses");
        }

        lesson.setTitle(requestDto.title());
        lesson.setContent(requestDto.content());
        lesson.setAttachmentUrl(requestDto.attachmentUrl());
        lesson.setOrderIndex(requestDto.orderIndex());

        Lesson updated = lessonRepository.save(lesson);
        return lessonMapper.toResponseDto(updated);
    }

    // ====================== DELETE LESSON ======================
    @Transactional
    public void deleteLesson(Long lessonId) {
        User currentUser = userService.getCurrentUser();

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        if (currentUser.getRole() != Role.ADMIN &&
                !lesson.getCourse().getInstructor().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied: You can only delete lessons from your own courses");
        }

        lessonRepository.delete(lesson);
    }
}