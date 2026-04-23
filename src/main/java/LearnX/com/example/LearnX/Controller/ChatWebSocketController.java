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
import java.util.ArrayList;
import java.util.List;

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
            logger.info("Tagged users: {}", request.taggedUsers());

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

            // Create and save message with tagged users
            ChatMessage message = new ChatMessage();
            message.setContent(request.content());
            message.setFileUrl(request.fileUrl());
            message.setMessageType(request.messageType() != null ? request.messageType() : "text");

            // Pass tagged users to save method
            List<String> taggedUsers = request.taggedUsers() != null ? request.taggedUsers() : new ArrayList<>();
            ChatMessageResponse response = chatService.saveGroupMessage(message, user, taggedUsers);
            logger.info("✅ Message saved and broadcasted: {}", response.content());

            // Send notifications to tagged users
            for (String taggedEmail : taggedUsers) {
                try {
                    User taggedUser = userService.findByEmail(taggedEmail);
                    if (taggedUser != null && !taggedUser.getId().equals(user.getId())) {
                        notificationService.sendNotification(
                                taggedUser,
                                "You were mentioned in a message",
                                user.getName() + " mentioned you in the community chat: " +
                                        (request.content().length() > 50 ? request.content().substring(0, 50) + "..." : request.content())
                        );
                    }
                } catch (Exception e) {
                    logger.warn("Could not send notification to: {}", taggedEmail);
                }
            }

            return response;

        } catch (Exception e) {
            logger.error("Error sending group message: {}", e.getMessage(), e);
            return null;
        }
    }
}