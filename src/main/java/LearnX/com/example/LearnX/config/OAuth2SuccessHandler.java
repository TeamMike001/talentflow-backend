package LearnX.com.example.LearnX.config;

import LearnX.com.example.LearnX.Model.User;
import LearnX.com.example.LearnX.Repository.UserRepository;
import LearnX.com.example.LearnX.dtos.AuthResponseDto;
import LearnX.com.example.LearnX.dtos.UserResponseDto;
import LearnX.com.example.LearnX.mapper.UserMapper;
import LearnX.com.example.LearnX.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OAuth2SuccessHandler(UserRepository userRepository, JwtUtil jwtUtil, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.userMapper = userMapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        System.out.println(">>> OAuth2SuccessHandler triggered - Returning JSON with JWT <<<");

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OidcUser oidcUser = (OidcUser) oauthToken.getPrincipal();

        String email = oidcUser.getAttribute("email");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found after OAuth"));

        // Update last active time
        user.setLastActiveAt(LocalDateTime.now());
        userRepository.save(user);

        // Generate JWT token
        String token = jwtUtil.generateToken(user);

        // Convert to DTO
        UserResponseDto userDto = userMapper.toResponseDto(user);

        // Build response
        AuthResponseDto authResponse = new AuthResponseDto(token, userDto);

        // Send JSON response to frontend
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(objectMapper.writeValueAsString(authResponse));

        System.out.println("JSON + JWT sent successfully for email: " + email);
    }
}