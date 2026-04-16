package LearnX.com.example.LearnX.service;

import LearnX.com.example.LearnX.Enum.Role;
import LearnX.com.example.LearnX.Model.Course;
import LearnX.com.example.LearnX.Model.Enrollment;
import LearnX.com.example.LearnX.Model.User;
import LearnX.com.example.LearnX.Model.UserPrincipal;
import LearnX.com.example.LearnX.Repository.CourseRepository;
import LearnX.com.example.LearnX.Repository.EnrollmentRepository;
import LearnX.com.example.LearnX.Repository.UserRepository;
import LearnX.com.example.LearnX.dtos.UpdateUserDto;
import LearnX.com.example.LearnX.dtos.UserDto;
import LearnX.com.example.LearnX.dtos.UserResponseDto;
import LearnX.com.example.LearnX.mapper.UserMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       UserMapper userMapper,
                       CourseRepository courseRepository,
                       EnrollmentRepository enrollmentRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("No authenticated user");
        }

        Object principal = auth.getPrincipal();
        String email;

        if (principal instanceof UserPrincipal) {
            email = ((UserPrincipal) principal).getUsername();
        }
        else if (principal instanceof OAuth2User) {
            email = ((OAuth2User) principal).getAttribute("email");
        }
        else if (principal instanceof String) {
            email = (String) principal;
        }
        else {
            throw new RuntimeException("Unsupported principal type: " + principal.getClass());
        }

        if (email == null || email.isEmpty()) {
            throw new RuntimeException("Email not found from principal");
        }

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found for email: " + email));
    }

    public User toggleUserEnabled(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(!user.isEnabled());
        return userRepository.save(user);
    }

    public UserResponseDto updateUserRole(Long id, Role newRole) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(newRole);
        return userMapper.toResponseDto(userRepository.save(user));
    }

    public List<Map<String, Object>> getAllCoursesWithEnrollments() {
        List<Course> courses = courseRepository.findAll();
        return courses.stream().map(course -> {
            Map<String, Object> courseData = new HashMap<>();
            courseData.put("id", course.getId());
            courseData.put("title", course.getTitle());
            courseData.put("description", course.getDescription());
            courseData.put("published", course.isPublished());
            courseData.put("instructor", course.getInstructor() != null ? course.getInstructor().getName() : "Unknown");

            List<Enrollment> enrollments = enrollmentRepository.findByCourseId(course.getId());
            courseData.put("studentCount", enrollments.size());
            courseData.put("students", enrollments.stream()
                    .map(e -> Map.of(
                            "id", e.getStudent().getId(),
                            "name", e.getStudent().getName(),
                            "email", e.getStudent().getEmail(),
                            "progress", e.getProgressPercentage()
                    ))
                    .collect(Collectors.toList()));
            return courseData;
        }).collect(Collectors.toList());
    }

    public List<Map<String, Object>> getCourseStudents(Long courseId) {
        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(courseId);
        return enrollments.stream()
                .map(e -> Map.<String, Object>of(
                        "id", e.getStudent().getId(),
                        "name", e.getStudent().getName(),
                        "email", e.getStudent().getEmail(),
                        "enrolledAt", e.getEnrolledAt(),
                        "progress", e.getProgressPercentage()
                ))
                .collect(Collectors.toList());
    }

    public Map<String, Object> getAdminStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalStudents", userRepository.countByRole(Role.STUDENT));
        stats.put("totalInstructors", userRepository.countByRole(Role.INSTRUCTOR));
        stats.put("totalCourses", courseRepository.count());
        stats.put("totalEnrollments", enrollmentRepository.count());
        return stats;
    }

    public List<UserResponseDto> getAllUsers() {
        User current = getCurrentUser();
        if (current.getRole() != Role.ADMIN) {
            throw new RuntimeException("Access denied: Only admin can view all users");
        }
        return userRepository.findAll().stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<UserResponseDto> getAllStudents() {
        User current = getCurrentUser();
        if (current.getRole() != Role.ADMIN) {
            throw new RuntimeException("Access denied: Only admin can view students");
        }
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.STUDENT)
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserDtoById(Long id) {
        User user = getUserEntityById(id);
        String userName = user.getName() != null ? user.getName() : user.getEmail().split("@")[0];
        char firstChar = userName.charAt(0);
        String avatar = "https://ui-avatars.com/api/?background=2563EB&color=fff&name=" + firstChar;
        String lastActiveAtStr = null;
        if (user.getLastActiveAt() != null) {
            lastActiveAtStr = user.getLastActiveAt().toString();
        }
        return new UserDto(
                user.getId(),
                userName,
                user.getEmail(),
                user.getRole(),
                avatar,
                user.isOnline(),
                lastActiveAtStr
        );
    }

    public List<UserResponseDto> getAllInstructors() {
        User current = getCurrentUser();
        if (current.getRole() != Role.ADMIN) {
            throw new RuntimeException("Access denied: Only admin can view instructors");
        }
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.INSTRUCTOR)
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }


    public UserResponseDto getUserById(Long id) {
        User current = getCurrentUser();
        User target = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (current.getRole() != Role.ADMIN && !current.getId().equals(target.getId())) {
            throw new RuntimeException("Access denied: You can only view your own profile");
        }
        return userMapper.toResponseDto(target);
    }


    @Transactional
    public UserResponseDto updateUser(Long id, UpdateUserDto updateDto) {
        User current = getCurrentUser();
        User target = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (current.getRole() != Role.ADMIN && !current.getId().equals(target.getId())) {
            throw new RuntimeException("Access denied: You can only update your own profile");
        }
        if (updateDto.name() != null && !updateDto.name().isEmpty()) {
            target.setName(updateDto.name());
        }
        if (updateDto.email() != null && !updateDto.email().isEmpty()) {
            String newEmail = updateDto.email();
            userRepository.findByEmail(newEmail).ifPresent(existing -> {
                if (!existing.getId().equals(target.getId())) {
                    throw new RuntimeException("Email already in use by another user");
                }
            });
            target.setEmail(newEmail);
        }
        if (updateDto.password() != null && !updateDto.password().isEmpty()) {
            target.setPassword(passwordEncoder.encode(updateDto.password()));
        }
        return userMapper.toResponseDto(userRepository.save(target));
    }

    @Transactional
    public void deleteUser(Long id) {
        User current = getCurrentUser();
        User target = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (current.getRole() != Role.ADMIN && !current.getId().equals(target.getId())) {
            throw new RuntimeException("Access denied: You can only delete your own account");
        }
        userRepository.delete(target);
    }

    @Transactional
    public void deleteOwnAccount() {
        User current = getCurrentUser();
        userRepository.delete(current);
    }

    public UserResponseDto getCurrentUserProfile() {
        User current = getCurrentUser();
        return userMapper.toResponseDto(current);
    }

    public List<User> getUsersByIds(List<Long> ids) {
        return userRepository.findAllById(ids);
    }

    public List<User> getAllUsersEntities() {
        return userRepository.findAll();
    }

    @Transactional
    public void updateUserLastActive(User user) {
        user.setLastActiveAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found for email: " + email));
    }

    public User getUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }
}