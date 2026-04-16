package LearnX.com.example.LearnX.service;

import LearnX.com.example.LearnX.Model.Bookmark;
import LearnX.com.example.LearnX.Model.Course;
import LearnX.com.example.LearnX.Model.User;
import LearnX.com.example.LearnX.Repository.BookmarkRepository;
import LearnX.com.example.LearnX.Repository.CourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final CourseRepository courseRepository;
    private final UserService userService;

    public BookmarkService(BookmarkRepository bookmarkRepository,
                           CourseRepository courseRepository,
                           UserService userService) {
        this.bookmarkRepository = bookmarkRepository;
        this.courseRepository = courseRepository;
        this.userService = userService;
    }

    @Transactional
    public BookmarkResponseDto addBookmark(Long courseId) {
        User user = userService.getCurrentUser();
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (bookmarkRepository.existsByUserIdAndCourseId(user.getId(), courseId)) {
            throw new RuntimeException("Already bookmarked");
        }

        Bookmark bookmark = new Bookmark();
        bookmark.setUser(user);
        bookmark.setCourse(course);
        bookmark = bookmarkRepository.save(bookmark);
        return new BookmarkResponseDto(bookmark.getId(), course.getId(), course.getTitle(), bookmark.getCreatedAt());
    }

    @Transactional
    public void removeBookmark(Long courseId) {
        User user = userService.getCurrentUser();
        Bookmark bookmark = bookmarkRepository.findByUserIdAndCourseId(user.getId(), courseId)
                .orElseThrow(() -> new RuntimeException("Bookmark not found"));
        bookmarkRepository.delete(bookmark);
    }

    public List<BookmarkResponseDto> getMyBookmarks() {
        User user = userService.getCurrentUser();
        return bookmarkRepository.findByUserId(user.getId()).stream()
                .map(b -> new BookmarkResponseDto(b.getId(), b.getCourse().getId(), b.getCourse().getTitle(), b.getCreatedAt()))
                .collect(Collectors.toList());
    }
}