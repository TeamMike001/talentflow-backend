package LearnX.com.example.LearnX.Controller;

import LearnX.com.example.LearnX.Model.ChatMessage;
import LearnX.com.example.LearnX.Model.User;
import LearnX.com.example.LearnX.Repository.ChatMessageRepository;
import LearnX.com.example.LearnX.service.UserService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatFileController {


    private final Cloudinary cloudinary;
    private final UserService userService;
    private final ChatMessageRepository chatMessageRepository;

    public ChatFileController(Cloudinary cloudinary, UserService userService, ChatMessageRepository chatMessageRepository) {
        this.cloudinary = cloudinary;
        this.userService = userService;
        this.chatMessageRepository = chatMessageRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String url = uploadResult.get("secure_url").toString();
            return ResponseEntity.ok(Map.of("url", url));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Upload failed"));
        }
    }
    @PostMapping("/test/save")
    public ResponseEntity<String> testSaveMessage(@RequestBody Map<String, String> request) {
        try {
            User user = userService.getCurrentUser();
            ChatMessage message = new ChatMessage();
            message.setContent(request.get("content"));
            message.setMessageType("text");
            message.setChatType("group");
            message.setUser(user);
            message.setCreatedAt(LocalDateTime.now());

            ChatMessage saved = chatMessageRepository.save(message);
            return ResponseEntity.ok("Message saved with ID: " + saved.getId());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}