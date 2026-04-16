package LearnX.com.example.LearnX.Controller;

import LearnX.com.example.LearnX.Model.User;
import LearnX.com.example.LearnX.dtos.NotificationResponseDto;
import LearnX.com.example.LearnX.service.NotificationService;
import LearnX.com.example.LearnX.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    public NotificationController(NotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponseDto>> getMyNotifications() {
        return ResponseEntity.ok(notificationService.getMyNotifications());
    }
    @PostMapping("/send")
    public ResponseEntity<?> sendNotification(@RequestBody Map<String, Object> notificationData) {
        try {
            Long recipientId = Long.parseLong(notificationData.get("recipientId").toString());
            String title = notificationData.get("title").toString();
            String message = notificationData.get("message").toString();
            String type = notificationData.get("type").toString();

            User recipient = userService.getUserEntityById(recipientId);
            notificationService.sendNotification(recipient, title, message);

            return ResponseEntity.ok(Map.of("success", true, "message", "Notification sent"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponseDto>> getUnreadNotifications() {
        return ResponseEntity.ok(notificationService.getUnreadNotifications());
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead() {
        notificationService.markAllAsRead();
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long notificationId) {
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.noContent().build();
    }
}