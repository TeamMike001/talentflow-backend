package LearnX.com.example.LearnX.Repository;

import LearnX.com.example.LearnX.Model.EventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {

    boolean existsByEventIdAndUserId(Long eventId, Long userId);

    List<EventRegistration> findByUserId(Long userId);

    // FIXED: Changed from "Arrays" to "List<EventRegistration>"
    List<EventRegistration> findByEventId(Long eventId);

    long countByEventId(Long eventId);

    Optional<EventRegistration> findByEventIdAndUserId(Long eventId, Long userId);

    @Query("SELECT COUNT(er) FROM EventRegistration er WHERE er.event.id = :eventId")
    long countRegistrationsByEventId(@Param("eventId") Long eventId);

    @Query("SELECT er FROM EventRegistration er WHERE er.event.id = :eventId ORDER BY er.registeredAt DESC")
    List<EventRegistration> findRegistrationsByEventIdOrderByDate(@Param("eventId") Long eventId);
}