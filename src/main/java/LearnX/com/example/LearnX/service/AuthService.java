package LearnX.com.example.LearnX.service;

import LearnX.com.example.LearnX.Model.User;
import LearnX.com.example.LearnX.Model.UserPrincipal;
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

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

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

    public AuthResponseDto login(LoginRequestDto request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        User user = principal.getUser();

        user.setLastActiveAt(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtUtil.generateToken(user);
        UserResponseDto userDto = userMapper.toResponseDto(user);
        return new AuthResponseDto(token, userDto);
    }

    public AuthResponseDto register(UserRegistrationDto dto) {
        if (userRepository.findByEmail(dto.email()).isPresent()) {
            throw new RuntimeException("Email already in use");
        }

        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.password()));
        user = userRepository.save(user);

        String token = jwtUtil.generateToken(user);
        UserResponseDto userDto = userMapper.toResponseDto(user);
        return new AuthResponseDto(token, userDto);
    }
}