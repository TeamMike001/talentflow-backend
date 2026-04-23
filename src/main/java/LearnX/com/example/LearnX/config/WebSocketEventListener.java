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
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    private final ObjectMapper objectMapper = new ObjectMapper();

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        try {
            StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
            String authHeader = headerAccessor.getFirstNativeHeader("Authorization");

            logger.info("🔌 WebSocket connection attempt - Auth header present: {}", authHeader != null);

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String email = extractEmailFromToken(token);

                logger.info("📧 Extracted email from token: {}", email);

                if (email != null) {
                    Optional<User> userOpt = userRepository.findByEmail(email);
                    if (userOpt.isPresent()) {
                        User user = userOpt.get();
                        user.setOnline(true);
                        user.setLastActiveAt(LocalDateTime.now());
                        userRepository.save(user);
                        logger.info("✅ User {} ({}) is now ONLINE", user.getEmail(), user.getName());
                        broadcastUserStatus();
                    } else {
                        logger.warn("⚠️ User not found with email: {}", email);
                    }
                }
            } else {
                logger.warn("⚠️ No valid Authorization header found");
            }
        } catch (Exception e) {
            logger.error("❌ Error handling connect event: {}", e.getMessage(), e);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        try {
            StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

            // Try to get user ID from session attributes
            String userId = (String) headerAccessor.getSessionAttributes().get("userId");

            if (userId != null) {
                Optional<User> userOpt = userRepository.findById(Long.parseLong(userId));
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    user.setOnline(false);
                    user.setLastActiveAt(LocalDateTime.now());
                    userRepository.save(user);
                    logger.info("❌ User {} ({}) is now OFFLINE", user.getEmail(), user.getName());
                    broadcastUserStatus();
                    return;
                }
            }

            // Fallback: try to get from Authorization header
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
                        logger.info("❌ User {} ({}) is now OFFLINE", user.getEmail(), user.getName());
                        broadcastUserStatus();
                    }
                }
            }
        } catch (Exception e) {
            logger.error("❌ Error handling disconnect event: {}", e.getMessage(), e);
        }
    }

    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
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
                        // Store user ID in session for disconnect handling
                        headerAccessor.getSessionAttributes().put("userId", String.valueOf(user.getId()));
                        logger.info("📡 User {} subscribed to channel", user.getEmail());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("❌ Error handling subscribe event: {}", e.getMessage(), e);
        }
    }

    private String extractEmailFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                logger.warn("Invalid token format: expected 3 parts, got {}", parts.length);
                return null;
            }

            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            logger.debug("Decoded payload: {}", payload);

            return extractValueFromJson(payload, "email");
        } catch (IllegalArgumentException e) {
            logger.error("Error decoding token: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            logger.error("Error extracting email from token: {}", e.getMessage());
            return null;
        }
    }

    private String extractValueFromJson(String json, String key) {
        try {
            // Simple JSON parsing for the specific key
            String searchKey = "\"" + key + "\":";
            int keyIndex = json.indexOf(searchKey);
            if (keyIndex == -1) {
                // Try with spaces
                searchKey = "\"" + key + "\" :";
                keyIndex = json.indexOf(searchKey);
                if (keyIndex == -1) return null;
            }

            int colonIndex = json.indexOf(":", keyIndex);
            if (colonIndex == -1) return null;

            int startIndex = colonIndex + 1;
            // Skip whitespace
            while (startIndex < json.length() && json.charAt(startIndex) == ' ') {
                startIndex++;
            }

            // Check if value is a string
            boolean isString = json.charAt(startIndex) == '"';
            if (isString) {
                startIndex++; // Skip opening quote
                int endIndex = json.indexOf("\"", startIndex);
                if (endIndex == -1) return null;
                return json.substring(startIndex, endIndex);
            } else {
                // For non-string values (numbers, booleans)
                int endIndex = startIndex;
                while (endIndex < json.length() && json.charAt(endIndex) != ',' && json.charAt(endIndex) != '}') {
                    endIndex++;
                }
                return json.substring(startIndex, endIndex).trim();
            }
        } catch (Exception e) {
            logger.error("Error extracting value for key {}: {}", key, e.getMessage());
            return null;
        }
    }

    private void broadcastUserStatus() {
        try {
            List<User> allUsers = userRepository.findAll();

            List<UserStatusDto> userStatuses = allUsers.stream()
                    .map(user -> {
                        String avatarUrl = "https://ui-avatars.com/api/?background=" +
                                (user.isOnline() ? (user.getRole().name().equals("INSTRUCTOR") ? "2563EB" : "16A34A") : "9CA3AF") +
                                "&color=fff&name=" + (user.getName() != null && !user.getName().isEmpty() ? user.getName().charAt(0) : 'U');

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

            long onlineCount = userStatuses.stream().filter(UserStatusDto::online).count();
            logger.info("📡 Broadcasting user status - Total: {}, Online: {}", userStatuses.size(), onlineCount);

            messagingTemplate.convertAndSend("/topic/users/status", userStatuses);

        } catch (Exception e) {
            logger.error("❌ Error broadcasting user status: {}", e.getMessage(), e);
        }
    }
}