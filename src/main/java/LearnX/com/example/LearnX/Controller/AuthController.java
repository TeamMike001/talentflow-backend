package LearnX.com.example.LearnX.Controller;

import LearnX.com.example.LearnX.dtos.AuthResponseDto;
import LearnX.com.example.LearnX.dtos.LoginRequestDto;
import LearnX.com.example.LearnX.dtos.UserRegistrationDto;
import LearnX.com.example.LearnX.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequest) {
        try {
            AuthResponseDto authResponse = authService.login(loginRequest);
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegistrationDto request) {
        try {
            AuthResponseDto authResponse = authService.register(request);
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, Object>> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        Map<String, Object> response = new HashMap<>();

        try {
            if (!authService.emailExists(email)) {
                response.put("success", false);
                response.put("error", "Email not found");
                return ResponseEntity.badRequest().body(response);
            }

            Map<String, Object> securityQuestions = authService.getSecurityQuestions(email);

            response.put("success", true);
            response.put("requiresSecurityQuestions", true);
            response.put("email", email);
            response.put("questions", securityQuestions);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/verify-security-answers")
    public ResponseEntity<Map<String, Object>> verifySecurityAnswers(@RequestBody Map<String, Object> request) {
        String email = (String) request.get("email");
        Map<String, String> answers = (Map<String, String>) request.get("answers");
        Map<String, Object> response = new HashMap<>();

        try {
            boolean isValid = authService.verifySecurityAnswers(email, answers);

            if (isValid) {
                String resetToken = authService.generatePasswordResetToken(email);
                response.put("success", true);
                response.put("resetToken", resetToken);
                response.put("message", "Answers verified. You can now reset your password.");
            } else {
                response.put("success", false);
                response.put("error", "Incorrect answers");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String resetToken = request.get("resetToken");
        String newPassword = request.get("newPassword");
        Map<String, Object> response = new HashMap<>();

        try {
            authService.resetPasswordWithToken(email, resetToken, newPassword);

            response.put("success", true);
            response.put("message", "Password reset successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Logged out successfully");
        return ResponseEntity.ok(response);
    }
}