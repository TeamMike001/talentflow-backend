package LearnX.com.example.LearnX.Controller;

import LearnX.com.example.LearnX.Model.User;
import LearnX.com.example.LearnX.dtos.PrivateMessageRequest;
import LearnX.com.example.LearnX.dtos.PrivateMessageResponse;
import LearnX.com.example.LearnX.service.ChatService;
import LearnX.com.example.LearnX.service.NotificationService;
import LearnX.com.example.LearnX.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.time.LocalDateTime;
import java.security.Principal;

@Controller
public class PrivateMessageController {
    private static final Logger logger = LoggerFactory.getLogger(PrivateMessageController.class);
    
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;
    private final UserService userService;
    private final NotificationService notificationService;

    public PrivateMessageController(SimpMessagingTemplate messagingTemplate,
                                    ChatService chatService,
                                    UserService userService,
                                    NotificationService notificationService) {
        this.messagingTemplate = messagingTemplate;
        this.chatService = chatService;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @MessageMapping("/private.send")
    public void sendPrivateMessage(PrivateMessageRequest request, Principal principal) {
        try {
            String email = principal.getName();
            User sender = userService.findByEmail(email);
            
            logger.info("📨 Private message from {} (ID: {}) to recipient ID: {}", 
                sender.getEmail(), sender.getId(), request.recipientId());
            
            // Save the message to database
            PrivateMessageResponse response = chatService.savePrivateMessage(
                sender, 
                request.recipientId(), 
                request.content(), 
                request.fileUrl(), 
                request.messageType()
            );
            
            logger.info("✅ Message saved with ID: {}", response.id());
            
            // Send to recipient
            messagingTemplate.convertAndSendToUser(
                String.valueOf(request.recipientId()),
                "/queue/private",
                response
            );
            
            // Also send back to sender for confirmation
            messagingTemplate.convertAndSendToUser(
                String.valueOf(sender.getId()),
                "/queue/private",
                response
            );
            
            // Send notification to recipient
            User recipient = userService.getUserEntityById(request.recipientId());
            notificationService.sendNotification(
                recipient,
                "New message from " + sender.getName(),
                request.content() != null ? request.content().substring(0, Math.min(50, request.content().length())) : "Sent you a file"
            );
            
            logger.info("✅ Private message sent successfully to user {}", request.recipientId());
            
        } catch (Exception e) {
            logger.error("Error sending private message: {}", e.getMessage(), e);
        }
    }
}