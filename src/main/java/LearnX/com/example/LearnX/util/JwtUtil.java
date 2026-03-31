package LearnX.com.example.LearnX.util;


import LearnX.com.example.LearnX.Model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

    @Value("${JWT_SECRET}")
    private String JWT_SECRET;

    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000L; // 24 hours

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(JWT_SECRET.getBytes());
    }

    // Main method - Recommended
    public String generateToken(User user) {
        return Jwts.builder()
                .claim("userId", user.getId().toString())
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())
                .claim("jti", UUID.randomUUID().toString())   // For logout/blacklist
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Validate token
    public Claims validateToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            throw new RuntimeException("Token expired or invalid");
        }
    }

    public String getUserId(String token) {
        return validateToken(token).get("userId", String.class);
    }

    public String getEmail(String token) {
        return validateToken(token).getSubject();
    }

    public String getRole(String token) {
        return validateToken(token).get("role", String.class);
    }

    public String getJti(String token) {
        return validateToken(token).get("jti", String.class);
    }
}