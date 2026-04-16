package LearnX.com.example.LearnX.Controller;

import LearnX.com.example.LearnX.Model.ChatMessage;
import LearnX.com.example.LearnX.Model.User;
import LearnX.com.example.LearnX.dtos.ChatMessageRequest;
import LearnX.com.example.LearnX.dtos.ChatMessageResponse;
import LearnX.com.example.LearnX.dtos.PrivateMessageResponse;
import LearnX.com.example.LearnX.dtos.UserStatusDto;
import LearnX.com.example.LearnX.service.ChatService;
import LearnX.com.example.LearnX.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
public class ChatRestController {
    private final ChatService chatService;
    private final UserService userService;

    public ChatRestController(ChatService chatService, UserService userService) {
        this.chatService = chatService;
        this.userService = userService;
    }

    @GetMapping("/messages")
    public ResponseEntity<List<ChatMessageResponse>> getGroupMessages() {
        return ResponseEntity.ok(chatService.getGroupMessages());
    }

    @GetMapping("/conversation/{userId}")
    public ResponseEntity<List<PrivateMessageResponse>> getConversation(@PathVariable Long userId) {
        return ResponseEntity.ok(chatService.getConversation(userId));
    }

    @GetMapping("/partners")
    public ResponseEntity<List<UserStatusDto>> getChatPartners() {
        List<User> partners = chatService.getChatPartners();
        List<UserStatusDto> partnerStatuses = partners.stream()
                .map(user -> chatService.getUserStatus(user.getId()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(partnerStatuses);
    }
    @PostMapping("/test/message")
    public ResponseEntity<ChatMessageResponse> testSaveMessage(@RequestBody ChatMessageRequest request) {
        User user = userService.getCurrentUser();
        ChatMessage message = new ChatMessage();
        message.setContent(request.content());
        message.setMessageType("text");
        return ResponseEntity.ok(chatService.saveGroupMessage(message, user));
    }

    @GetMapping("/users/active")
    public ResponseEntity<List<UserStatusDto>> getActiveUsers() {
        return ResponseEntity.ok(chatService.getActiveUsers());
    }

    @GetMapping("/user/{userId}/status")
    public ResponseEntity<UserStatusDto> getUserStatus(@PathVariable Long userId) {
        return ResponseEntity.ok(chatService.getUserStatus(userId));
    }
}