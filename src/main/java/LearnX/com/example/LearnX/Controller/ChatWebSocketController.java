package LearnX.com.example.LearnX.Controller;

import LearnX.com.example.LearnX.Model.ChatMessage;
import LearnX.com.example.LearnX.Model.User;
import LearnX.com.example.LearnX.dtos.ChatMessageRequest;
import LearnX.com.example.LearnX.dtos.ChatMessageResponse;
import LearnX.com.example.LearnX.service.ChatService;
import LearnX.com.example.LearnX.service.NotificationService;
import LearnX.com.example.LearnX.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import java.security.Principal;
import java.time.LocalDateTime;

@Controller
public class ChatWebSocketController {
    private static final Logger logger = LoggerFactory.getLogger(ChatWebSocketController.class);

    @Autowired
    private ChatService chatService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private NotificationService notificationService;

    @MessageMapping("/group.send")
    @SendTo("/topic/group")
    public ChatMessageResponse sendGroupMessage(ChatMessageRequest request, Principal principal, SimpMessageHeaderAccessor headerAccessor) {
        try {
            String email = principal != null ? principal.getName() : null;
            logger.info("📨 Received group message from: {}", email);
            logger.info("Message content: {}", request.content());
            
            if (email == null) {
                logger.error("No principal found in WebSocket message");
                return null;
            }
            
            // Get user by email
            User user = userService.findByEmail(email);
            if (user == null) {
                logger.error("User not found for email: {}", email);
                return null;
            }

            logger.info("User found: {} (ID: {})", user.getEmail(), user.getId());
            
            // Update user activity
            user.setLastActiveAt(LocalDateTime.now());
            user.setOnline(true);
            userService.updateUserLastActive(user);

            // Create and save message
            ChatMessage message = new ChatMessage();
            message.setContent(request.content());
            message.setFileUrl(request.fileUrl());
            message.setMessageType(request.messageType() != null ? request.messageType() : "text");

            ChatMessageResponse response = chatService.saveGroupMessage(message, user);
            logger.info("✅ Message saved and broadcasted: {}", response.content());
            
            return response;

        } catch (Exception e) {
            logger.error("Error sending group message: {}", e.getMessage(), e);
            return null;
        }
    }
}