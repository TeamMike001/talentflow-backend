package LearnX.com.example.LearnX.config;

import LearnX.com.example.LearnX.Model.User;
import LearnX.com.example.LearnX.Repository.UserRepository;
import LearnX.com.example.LearnX.dtos.UserStatusDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class WebSocketEventListener {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        try {
            StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
            String authHeader = headerAccessor.getFirstNativeHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String email = extractEmailFromToken(token);

                if (email != null) {
                    Optional<User> userOpt = userRepository.findByEmail(email);
                    if (userOpt.isPresent()) {
                        User user = userOpt.get();
                        user.setOnline(true);
                        user.setLastActiveAt(LocalDateTime.now());
                        userRepository.save(user);
                        logger.info("✅ User {} is now ONLINE", user.getEmail());
                        broadcastOnlineUsers();
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error handling connect event: {}", e.getMessage());
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        try {
            StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
            String authHeader = headerAccessor.getFirstNativeHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String email = extractEmailFromToken(token);

                if (email != null) {
                    Optional<User> userOpt = userRepository.findByEmail(email);
                    if (userOpt.isPresent()) {
                        User user = userOpt.get();
                        user.setOnline(false);
                        user.setLastActiveAt(LocalDateTime.now());
                        userRepository.save(user);
                        logger.info("❌ User {} is now OFFLINE", user.getEmail());
                        broadcastOnlineUsers();
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error handling disconnect event: {}", e.getMessage());
        }
    }

    private String extractEmailFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return null;
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            return extractValueFromJson(payload, "email");
        } catch (Exception e) {
            logger.error("Error extracting email: {}", e.getMessage());
            return null;
        }
    }

    private String extractValueFromJson(String json, String key) {
        String searchKey = "\"" + key + "\"";
        int keyIndex = json.indexOf(searchKey);
        if (keyIndex == -1) return null;
        int colonIndex = json.indexOf(":", keyIndex);
        if (colonIndex == -1) return null;
        int startIndex = colonIndex + 1;
        while (startIndex < json.length() && (json.charAt(startIndex) == ' ' || json.charAt(startIndex) == '\"')) {
            if (json.charAt(startIndex) == '\"') startIndex++;
            break;
        }
        int endIndex = startIndex;
        while (endIndex < json.length() && json.charAt(endIndex) != '\"' && json.charAt(endIndex) != ',' && json.charAt(endIndex) != '}') {
            endIndex++;
        }
        return json.substring(startIndex, endIndex);
    }

    private void broadcastOnlineUsers() {
        try {
            List<User> allUsers = userRepository.findAll();
            List<UserStatusDto> onlineUsers = allUsers.stream()
                    .filter(User::isOnline)
                    .map(user -> {
                        String userName = user.getName() != null ? user.getName() : user.getEmail().split("@")[0];
                        char firstChar = userName.charAt(0);
                        String avatar = "https://ui-avatars.com/api/?background=2563EB&color=fff&name=" + firstChar;
                        return new UserStatusDto(
                                user.getId(),
                                userName,
                                avatar,
                                true,
                                user.getLastActiveAt()
                        );
                    })
                    .collect(Collectors.toList());

            List<UserStatusDto> allUsersWithStatus = allUsers.stream()
                    .map(user -> {
                        String userName = user.getName() != null ? user.getName() : user.getEmail().split("@")[0];
                        char firstChar = userName.charAt(0);
                        String avatar = "https://ui-avatars.com/api/?background=" + (user.isOnline() ? "2563EB" : "9CA3AF") + "&color=fff&name=" + firstChar;
                        return new UserStatusDto(
                                user.getId(),
                                userName,
                                avatar,
                                user.isOnline(),
                                user.getLastActiveAt()
                        );
                    })
                    .collect(Collectors.toList());

            messagingTemplate.convertAndSend("/topic/users/status", allUsersWithStatus);
            logger.info("📡 Broadcasted {} users ({} online)", allUsersWithStatus.size(), onlineUsers.size());
        } catch (Exception e) {
            logger.error("Error broadcasting online users: {}", e.getMessage());
        }
    }
}