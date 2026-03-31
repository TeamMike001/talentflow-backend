package LearnX.com.example.LearnX.Repository;

import LearnX.com.example.LearnX.Model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientIdOrderByCreatedAtDesc(Long userId);
    List<Notification> findByRecipientIdAndIsReadFalse(Long userId);
}