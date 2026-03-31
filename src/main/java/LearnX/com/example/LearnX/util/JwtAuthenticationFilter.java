package LearnX.com.example.LearnX.util;

import LearnX.com.example.LearnX.Repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final TokenBlacklist blacklist;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, TokenBlacklist blacklist, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.blacklist = blacklist;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                Claims claims = jwtUtil.validateToken(token);
                String email = claims.getSubject();

                // Update last active time (optional)
                userRepository.findByEmail(email).ifPresent(user -> {
                    user.setLastActiveAt(LocalDateTime.now());
                    userRepository.save(user);
                });

                String jti = jwtUtil.getJti(token);
                if (blacklist.isBlacklisted(jti)) {
                    throw new RuntimeException("Token blacklisted");
                }

                // ✅ Extract role from token claims
                String role = claims.get("role", String.class);
                List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + role)
                );

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(email, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (Exception e) {
                response.setStatus(401);
                response.getWriter().write("{\"error\": \"Invalid or expired token\"}");
                return;
            }
        }
        chain.doFilter(request, response);
    }
}