package LearnX.com.example.LearnX.Repository;

import LearnX.com.example.LearnX.Model.PrivateMessage;
import LearnX.com.example.LearnX.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PrivateMessageRepository extends JpaRepository<PrivateMessage, Long> {

    @Query("SELECT m FROM PrivateMessage m WHERE (m.sender.id = :userId1 AND m.recipient.id = :userId2) OR (m.sender.id = :userId2 AND m.recipient.id = :userId1) ORDER BY m.createdAt ASC")
    List<PrivateMessage> findConversation(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    @Query("SELECT DISTINCT CASE WHEN m.sender.id = :userId THEN m.recipient ELSE m.sender END FROM PrivateMessage m WHERE m.sender.id = :userId OR m.recipient.id = :userId")
    List<User> findChatPartners(@Param("userId") Long userId);

    @Query("SELECT m FROM PrivateMessage m WHERE m.sender.id = :userId OR m.recipient.id = :userId ORDER BY m.createdAt DESC")
    List<PrivateMessage> findRecentPrivateMessages(@Param("userId") Long userId);
}