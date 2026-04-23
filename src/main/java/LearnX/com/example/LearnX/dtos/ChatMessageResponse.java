package LearnX.com.example.LearnX.dtos;

import java.time.LocalDateTime;
import java.util.List;

public record ChatMessageResponse(
        Long id,
        String content,
        String messageType,
        Long senderId,
        String senderName,
        String senderRole,
        LocalDateTime createdAt,
        String avatarUrl,
        List<String> taggedUsers,
        boolean hasTag
) {
    // Constructor for group messages without tags
    public ChatMessageResponse(Long id, String content, String messageType, Long senderId,
                               String senderName, String senderRole, LocalDateTime createdAt, String avatarUrl) {
        this(id, content, messageType, senderId, senderName, senderRole, createdAt, avatarUrl, null, false);
    }

    // Constructor with tags
    public ChatMessageResponse(Long id, String content, String messageType, Long senderId,
                               String senderName, String senderRole, LocalDateTime createdAt,
                               String avatarUrl, List<String> taggedUsers) {
        this(id, content, messageType, senderId, senderName, senderRole, createdAt, avatarUrl, taggedUsers, taggedUsers != null && !taggedUsers.isEmpty());
    }
}