package LearnX.com.example.LearnX.config;


import LearnX.com.example.LearnX.Enum.Role;
import LearnX.com.example.LearnX.Model.User;
import LearnX.com.example.LearnX.Repository.UserRepository;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CustomOAuth2UserService extends OidcUserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser oidcUser = super.loadUser(userRequest);

        String email = oidcUser.getAttribute("email");
        String name = oidcUser.getAttribute("name");

        System.out.println(">>> CustomOAuth2UserService.loadUser() WAS CALLED (OIDC) <<<");
        System.out.println("Google Email : " + email);
        System.out.println("Google Name  : " + name);

        if (email == null) {
            throw new RuntimeException("Email not provided by Google");
        }

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    System.out.println("→ User NOT found. Creating new user for: " + email);
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setName(name != null ? name : "Google User");
                    newUser.setRole(Role.STUDENT);
                    newUser.setEnabled(true);
                    newUser.setLocked(false);
                    newUser.setLastActiveAt(LocalDateTime.now());
                    newUser.setPassword("oauth_" + UUID.randomUUID().toString());
                    User saved = userRepository.save(newUser);
                    System.out.println("✓ NEW USER CREATED! ID = " + saved.getId());
                    return saved;
                });

        user.setLastActiveAt(LocalDateTime.now());
        userRepository.save(user);

        System.out.println("✓ User saved/updated in database. Email = " + user.getEmail());
        System.out.println("=== OAUTH LOGIN COMPLETE ===\n");

        return oidcUser;
    }
}