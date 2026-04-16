package LearnX.com.example.LearnX.Repository;

import LearnX.com.example.LearnX.Model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByPublishedTrueOrderByEventDateAsc();
    List<Event> findAllByOrderByCreatedAtDesc();
    List<Event> findByEventDateAfter(LocalDateTime date);

    List<Event> findByEventDateBefore(LocalDateTime date);

    @Query("SELECT e FROM Event e ORDER BY e.eventDate ASC")
    List<Event> findAllOrderByDateAsc();
}