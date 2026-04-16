package LearnX.com.example.LearnX.Repository;

import LearnX.com.example.LearnX.Model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("SELECT m FROM ChatMessage m WHERE m.chatType = 'group' ORDER BY m.createdAt DESC")
    List<ChatMessage> findRecentGroupMessages();

    @Query(value = "SELECT * FROM chat_message WHERE chat_type = 'group' ORDER BY created_at DESC LIMIT 50", nativeQuery = true)
    List<ChatMessage> findLast50GroupMessages();
}