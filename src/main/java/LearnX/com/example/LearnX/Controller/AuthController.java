package LearnX.com.example.LearnX.Controller;

import LearnX.com.example.LearnX.dtos.AuthResponseDto;
import LearnX.com.example.LearnX.dtos.LoginRequestDto;
import LearnX.com.example.LearnX.dtos.UserRegistrationDto;
import LearnX.com.example.LearnX.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginRequestDto loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@RequestBody UserRegistrationDto request) {
        return ResponseEntity.ok(authService.register(request));
    }
}