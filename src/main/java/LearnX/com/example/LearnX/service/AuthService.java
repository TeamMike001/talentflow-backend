package LearnX.com.example.LearnX.service;

import LearnX.com.example.LearnX.Model.User;
import LearnX.com.example.LearnX.Repository.UserRepository;
import LearnX.com.example.LearnX.dtos.AuthResponseDto;
import LearnX.com.example.LearnX.dtos.LoginRequestDto;
import LearnX.com.example.LearnX.dtos.UserRegistrationDto;
import LearnX.com.example.LearnX.dtos.UserResponseDto;
import LearnX.com.example.LearnX.mapper.UserMapper;
import LearnX.com.example.LearnX.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final Map<String, String> passwordResetTokens = new HashMap<>();

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       JwtUtil jwtUtil,
                       UserMapper userMapper,
                       PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public AuthResponseDto login(LoginRequestDto request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setLastActiveAt(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtUtil.generateToken(user);
        UserResponseDto userDto = userMapper.toResponseDto(user);
        return new AuthResponseDto(token, userDto);
    }

    @Transactional
    public AuthResponseDto register(UserRegistrationDto dto) {
        // Check if email already exists
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already in use");
        }

        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEnabled(true);

        // Set security questions from registration
        if (dto.getSecurityAnswers() != null) {
            Map<String, String> answers = dto.getSecurityAnswers();

            // Set questions (if provided in the answers map)
            if (answers.containsKey("question1")) {
                user.setSecurityQuestion1(answers.get("question1"));
            }
            if (answers.containsKey("answer1")) {
                user.setSecurityAnswer1(answers.get("answer1"));
            }
            if (answers.containsKey("question2")) {
                user.setSecurityQuestion2(answers.get("question2"));
            }
            if (answers.containsKey("answer2")) {
                user.setSecurityAnswer2(answers.get("answer2"));
            }
            if (answers.containsKey("question3")) {
                user.setSecurityQuestion3(answers.get("question3"));
            }
            if (answers.containsKey("answer3")) {
                user.setSecurityAnswer3(answers.get("answer3"));
            }
        }

        user = userRepository.save(user);

        String token = jwtUtil.generateToken(user);
        UserResponseDto userDto = userMapper.toResponseDto(user);
        return new AuthResponseDto(token, userDto);
    }

    public Map<String, Object> getSecurityQuestions(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> questions = new HashMap<>();
        questions.put("question1", user.getSecurityQuestion1());
        questions.put("question2", user.getSecurityQuestion2());
        questions.put("question3", user.getSecurityQuestion3());

        return questions;
    }

    public boolean verifySecurityAnswers(String email, Map<String, String> answers) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getSecurityAnswer1() != null &&
                user.getSecurityAnswer1().equalsIgnoreCase(answers.get("answer1")) &&
                user.getSecurityAnswer2() != null &&
                user.getSecurityAnswer2().equalsIgnoreCase(answers.get("answer2")) &&
                user.getSecurityAnswer3() != null &&
                user.getSecurityAnswer3().equalsIgnoreCase(answers.get("answer3"));
    }

    public String generatePasswordResetToken(String email) {
        String token = UUID.randomUUID().toString();
        passwordResetTokens.put(email, token);
        return token;
    }

    public void resetPasswordWithToken(String email, String token, String newPassword) {
        String storedToken = passwordResetTokens.get(email);
        if (storedToken == null || !storedToken.equals(token)) {
            throw new RuntimeException("Invalid or expired reset token");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        passwordResetTokens.remove(email);
    }
}