package LearnX.com.example.LearnX.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.util.Base64;
import java.util.Collections;

@Component
public class AuthChannelInterceptor implements ChannelInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(AuthChannelInterceptor.class);
    
    @Value("${jwt.secret}")
    private String secretKey;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            logger.info("WebSocket CONNECT - Auth header present: {}", authHeader != null);
            
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                try {
                    String email = extractEmailFromToken(token);
                    if (email != null) {
                        logger.info("Authenticated WebSocket user: {}", email);
                        
                        // Create authentication object
                        UserDetails userDetails = User.withUsername(email)
                                .password("")
                                .authorities(Collections.emptyList())
                                .build();
                        
                        Authentication auth = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        
                        SecurityContextHolder.getContext().setAuthentication(auth);
                        accessor.setUser(auth);
                    } else {
                        logger.error("Failed to extract email from token");
                    }
                } catch (Exception e) {
                    logger.error("Error authenticating WebSocket: {}", e.getMessage());
                }
            }
        }
        return message;
    }
    
    private String extractEmailFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return null;
            }
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
}