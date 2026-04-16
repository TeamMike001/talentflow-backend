package LearnX.com.example.LearnX.config;

import LearnX.com.example.LearnX.Model.User;
import LearnX.com.example.LearnX.Repository.UserRepository;
import LearnX.com.example.LearnX.mapper.UserMapper;
import LearnX.com.example.LearnX.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${frontend.oauth.redirect.url}")
    private String frontendRedirectUrl;

    public OAuth2SuccessHandler(UserRepository userRepository, JwtUtil jwtUtil, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.userMapper = userMapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OidcUser oidcUser = (OidcUser) oauthToken.getPrincipal();

        String email = oidcUser.getAttribute("email");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found after OAuth"));

        user.setLastActiveAt(LocalDateTime.now());
        userRepository.save(user);

        String jwtToken = jwtUtil.generateToken(user);

        String redirectUrl = "http://localhost:3000/auth/oauth2/success?token=" + jwtToken;

        System.out.println("✅ OAuth Success - Redirecting to: " + redirectUrl);
        response.sendRedirect(redirectUrl);
    }
}