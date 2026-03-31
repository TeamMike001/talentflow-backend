package LearnX.com.example.LearnX.service;

import LearnX.com.example.LearnX.Model.User;
import LearnX.com.example.LearnX.Model.UserPrincipal;
import LearnX.com.example.LearnX.Repository.UserRepository;

import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    public MyUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        User user = optionalUser.get();

        // Check if account is locked
        if (user.isLocked()) {
            throw new LockedException("Account is locked. Please contact admin.");
        }

        // Check if account is enabled
        if (!user.isEnabled()) {
            throw new LockedException("Account is disabled.");
        }

        return new UserPrincipal(user);
    }
}