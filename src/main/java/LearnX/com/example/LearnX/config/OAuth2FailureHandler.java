package LearnX.com.example.LearnX.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class OAuth2FailureHandler implements AuthenticationFailureHandler {

    @Value("${frontend.oauth.failure.url:https://talentflow-frontend-theta.vercel.app/login}")
    private String frontendFailureUrl;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        String redirectUrl = UriComponentsBuilder
                .fromUriString(frontendFailureUrl)
                .queryParam("error", "oauth_login_failed")
                .build()
                .toUriString();

        System.err.println("OAuth login failed: " + exception.getMessage());
        response.sendRedirect(redirectUrl);
    }
}
