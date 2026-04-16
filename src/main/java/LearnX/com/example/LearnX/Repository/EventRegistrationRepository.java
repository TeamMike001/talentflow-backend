package LearnX.com.example.LearnX.Repository;

import LearnX.com.example.LearnX.Model.EventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
    boolean existsByEventIdAndUserId(Long eventId, Long userId);
    List<EventRegistration> findByUserId(Long userId);
    long countByEventId(Long eventId);
    Optional<EventRegistration> findByEventIdAndUserId(Long eventId, Long userId);
}