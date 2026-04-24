package LearnX.com.example.LearnX.service;

import LearnX.com.example.LearnX.Enum.Role;
import LearnX.com.example.LearnX.Model.Course;
import LearnX.com.example.LearnX.Model.Lecture;
import LearnX.com.example.LearnX.Model.Section;
import LearnX.com.example.LearnX.Model.User;
import LearnX.com.example.LearnX.Repository.CourseRepository;
import LearnX.com.example.LearnX.dtos.CourseCreateDto;
import LearnX.com.example.LearnX.dtos.CourseResponseDto;
import LearnX.com.example.LearnX.dtos.LectureDto;
import LearnX.com.example.LearnX.dtos.SectionDto;
import LearnX.com.example.LearnX.mapper.CourseMapper;
import LearnX.com.example.LearnX.mapper.NotificationMapper;
import LearnX.com.example.LearnX.mapper.UserMapper;
import LearnX.com.example.LearnX.Repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CourseServiceTest {

    @Test
    void updateCoursePreservesExistingLectureFieldsWhenOnlyMediaFieldsAreProvided() {
        CourseRepository courseRepository = mock(CourseRepository.class);
        CourseMapper courseMapper = new CourseMapper(new UserMapper());
        TestUserService userService = new TestUserService();
        NotificationService notificationService = new NotificationService(
                mock(NotificationRepository.class),
                new NotificationMapper(),
                userService
        );

        CourseService courseService = new CourseService(courseRepository, courseMapper, userService, notificationService);

        User instructor = new User();
        instructor.setId(10L);
        instructor.setRole(Role.INSTRUCTOR);

        Course course = new Course();
        course.setId(7L);
        course.setInstructor(instructor);
        course.setSections(new ArrayList<>());

        Section section = new Section();
        section.setId(11L);
        section.setName("Section A");
        section.setOrderIndex(1);
        section.setCourse(course);
        section.setLectures(new ArrayList<>());
        course.getSections().add(section);

        Lecture lecture = new Lecture();
        lecture.setId(21L);
        lecture.setName("Lecture One");
        lecture.setOrderIndex(1);
        lecture.setCaption("Old caption");
        lecture.setDescription("Old description");
        lecture.setSection(section);
        section.getLectures().add(lecture);

        CourseCreateDto request = new CourseCreateDto(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                List.of(new SectionDto(
                        11L,
                        null,
                        0,
                        List.of(new LectureDto(
                                21L,
                                null,
                                0,
                                "https://cdn.example.com/videos/lesson.mp4",
                                "https://cdn.example.com/notes/lesson.pdf",
                                null,
                                null,
                                null
                        ))
                )),
                null,
                null,
                null,
                false
        );

        userService.setCurrentUser(instructor);
        when(courseRepository.findById(7L)).thenReturn(Optional.of(course));
        when(courseRepository.save(any(Course.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CourseResponseDto response = courseService.updateCourse(7L, request);

        ArgumentCaptor<Course> savedCourseCaptor = ArgumentCaptor.forClass(Course.class);
        org.mockito.Mockito.verify(courseRepository).save(savedCourseCaptor.capture());

        Course savedCourse = savedCourseCaptor.getValue();
        Section savedSection = savedCourse.getSections().get(0);
        Lecture savedLecture = savedSection.getLectures().get(0);

        assertEquals("Section A", savedSection.getName());
        assertEquals(1, savedSection.getOrderIndex());
        assertEquals("Lecture One", savedLecture.getName());
        assertEquals(1, savedLecture.getOrderIndex());
        assertEquals("Old caption", savedLecture.getCaption());
        assertEquals("Old description", savedLecture.getDescription());
        assertEquals("https://cdn.example.com/videos/lesson.mp4", savedLecture.getVideoUrl());
        assertEquals("https://cdn.example.com/notes/lesson.pdf", savedLecture.getNotes());
        assertNotNull(savedLecture.getSection());
        assertEquals(21L, savedLecture.getId());
        assertEquals("https://cdn.example.com/videos/lesson.mp4", response.sections().get(0).lectures().get(0).videoUrl());
    }

    @Test
    void updateCourseMatchesExistingLectureByNameWhenIdsAreMissing() {
        CourseRepository courseRepository = mock(CourseRepository.class);
        CourseMapper courseMapper = new CourseMapper(new UserMapper());
        TestUserService userService = new TestUserService();
        NotificationService notificationService = new NotificationService(
                mock(NotificationRepository.class),
                new NotificationMapper(),
                userService
        );

        CourseService courseService = new CourseService(courseRepository, courseMapper, userService, notificationService);

        User instructor = new User();
        instructor.setId(10L);
        instructor.setRole(Role.INSTRUCTOR);

        Course course = new Course();
        course.setId(7L);
        course.setInstructor(instructor);
        course.setSections(new ArrayList<>());

        Section section = new Section();
        section.setId(11L);
        section.setName("Uploads");
        section.setOrderIndex(1);
        section.setCourse(course);
        section.setLectures(new ArrayList<>());
        course.getSections().add(section);

        Lecture lecture = new Lecture();
        lecture.setId(21L);
        lecture.setName("Welcome Video");
        lecture.setOrderIndex(1);
        lecture.setSection(section);
        section.getLectures().add(lecture);

        CourseCreateDto request = new CourseCreateDto(
                null, null, null, null, null, null, null, null, null, null,
                null, null, null, null,
                List.of(new SectionDto(
                        null,
                        "Uploads",
                        1,
                        List.of(new LectureDto(
                                null,
                                "Welcome Video",
                                1,
                                "https://cdn.example.com/videos/welcome.mp4",
                                null,
                                null,
                                null,
                                null
                        ))
                )),
                null, null, null, false
        );

        userService.setCurrentUser(instructor);
        when(courseRepository.findById(eq(7L))).thenReturn(Optional.of(course));
        when(courseRepository.save(any(Course.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CourseResponseDto response = courseService.updateCourse(7L, request);

        assertEquals(1, course.getSections().size());
        assertEquals(1, course.getSections().get(0).getLectures().size());
        assertEquals(21L, course.getSections().get(0).getLectures().get(0).getId());
        assertEquals("https://cdn.example.com/videos/welcome.mp4", course.getSections().get(0).getLectures().get(0).getVideoUrl());
        assertEquals("https://cdn.example.com/videos/welcome.mp4", response.sections().get(0).lectures().get(0).videoUrl());
    }

    private static class TestUserService extends UserService {
        private User currentUser;

        TestUserService() {
            super(null, null, null, null, null);
        }

        void setCurrentUser(User currentUser) {
            this.currentUser = currentUser;
        }

        @Override
        public User getCurrentUser() {
            return currentUser;
        }
    }
}
