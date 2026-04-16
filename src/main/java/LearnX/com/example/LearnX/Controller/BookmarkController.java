package LearnX.com.example.LearnX.Controller;

import LearnX.com.example.LearnX.service.BookmarkResponseDto;
import LearnX.com.example.LearnX.service.BookmarkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookmarks")
public class BookmarkController {
    private final BookmarkService bookmarkService;

    public BookmarkController(BookmarkService bookmarkService) { this.bookmarkService = bookmarkService; }

    @PostMapping("/{courseId}")
    public ResponseEntity<BookmarkResponseDto> addBookmark(@PathVariable Long courseId) {
        return ResponseEntity.ok(bookmarkService.addBookmark(courseId));
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> removeBookmark(@PathVariable Long courseId) {
        bookmarkService.removeBookmark(courseId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<BookmarkResponseDto>> getMyBookmarks() {
        return ResponseEntity.ok(bookmarkService.getMyBookmarks());
    }
}