package LearnX.com.example.LearnX.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.Base64;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketAuthInterceptor.class);

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            logger.info("WebSocket CONNECT request received, Auth header: {}", authHeader != null ? "Present" : "Missing");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                try {
                    Long userId = extractUserIdFromToken(token);
                    if (userId != null) {
                        logger.info("User ID {} authenticated for WebSocket", userId);
                        // Store user info in session
                        accessor.getSessionAttributes().put("userId", userId);
                    } else {
                        logger.error("Failed to extract userId from token");
                    }
                } catch (Exception e) {
                    logger.error("Error authenticating WebSocket: {}", e.getMessage());
                }
            }
        }
        return message;
    }

    private Long extractUserIdFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return null;
            }
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            String userIdStr = extractValueFromJson(payload, "userId");
            return userIdStr != null ? Long.parseLong(userIdStr) : null;
        } catch (Exception e) {
            logger.error("Error extracting userId: {}", e.getMessage());
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
}