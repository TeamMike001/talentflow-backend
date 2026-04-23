package LearnX.com.example.LearnX.Controller;

import LearnX.com.example.LearnX.Model.ChatMessage;
import LearnX.com.example.LearnX.Model.User;
import LearnX.com.example.LearnX.dtos.*;
import LearnX.com.example.LearnX.service.ChatService;
import LearnX.com.example.LearnX.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class CommunityController {

    private final ChatService chatService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    public CommunityController(ChatService chatService, UserService userService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/api/community/messages")
    public ResponseEntity<List<ChatMessageResponse>> getCommunityMessages() {
        return ResponseEntity.ok(chatService.getGroupMessages());
    }

    @MessageMapping("/community.send")
    @SendTo("/topic/community")
    public ChatMessageResponse sendCommunityMessage(ChatMessageRequest request, Principal principal) {
        User sender = userService.findByEmail(principal.getName());

        ChatMessage message = new ChatMessage();
        message.setContent(request.content());
        message.setMessageType(request.messageType() != null ? request.messageType() : "text");
        message.setUser(sender);
        message.setCreatedAt(LocalDateTime.now());
        message.setChatType("group");

        // Save message with tagged users
        List<String> taggedUsers = request.taggedUsers() != null ? request.taggedUsers() : new ArrayList<>();
        ChatMessageResponse response = chatService.saveGroupMessage(message, sender, taggedUsers);

        return response;
    }

    @GetMapping("/api/community/users")
    public ResponseEntity<List<UserStatusDto>> getCommunityUsers() {
        List<User> users = userService.getAllUsersEntities();
        List<UserStatusDto> userStatuses = users.stream()
                .filter(user -> user.getRole() != null)
                .map(user -> {
                    String avatarUrl = "https://ui-avatars.com/api/?background=" +
                            (user.getRole().name().equals("INSTRUCTOR") ? "2563EB" : "16A34A") +
                            "&color=fff&name=" + (user.getName() != null ? user.getName().charAt(0) : 'U');

                    return new UserStatusDto(
                            user.getId(),
                            user.getName(),
                            user.getEmail(),
                            user.getRole().name(),
                            user.isOnline(),
                            user.getLastActiveAt(),
                            avatarUrl
                    );
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(userStatuses);
    }

    @GetMapping("/api/community/conversation/{userId}")
    public ResponseEntity<List<PrivateMessageResponse>> getPrivateConversation(@PathVariable Long userId) {
        return ResponseEntity.ok(chatService.getConversation(userId));
    }

    @MessageMapping("/private.send")
    public void sendPrivateMessage(PrivateMessageRequest request, Principal principal) {
        User sender = userService.findByEmail(principal.getName());

        PrivateMessageResponse response = chatService.savePrivateMessage(
                sender,
                request.recipientId(),
                request.content(),
                request.fileUrl(),
                request.messageType() != null ? request.messageType() : "text"
        );

        // Send to recipient
        messagingTemplate.convertAndSendToUser(
                String.valueOf(request.recipientId()),
                "/queue/private",
                response
        );

        // Send back to sender
        messagingTemplate.convertAndSendToUser(
                String.valueOf(sender.getId()),
                "/queue/private",
                response
        );
    }
}