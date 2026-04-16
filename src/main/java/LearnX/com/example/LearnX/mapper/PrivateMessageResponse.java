package LearnX.com.example.LearnX.mapper;

import java.time.LocalDateTime;

public record PrivateMessageResponse(
    Long id,
    Long senderId,
    String senderName,
    String senderRole,
    Long recipientId,
    String recipientName,
    String content,
    String fileUrl,
    String messageType,
    LocalDateTime timestamp,
    boolean isSelf
) {}