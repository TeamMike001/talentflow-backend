package LearnX.com.example.LearnX.dtos;

import LearnX.com.example.LearnX.Enum.Role;
import java.time.LocalDateTime;

public record PrivateMessageResponse(
        Long id,
        Long senderId,
        String senderName,
        Role senderRole,
        Long recipientId,
        String recipientName,
        String content,
        String fileUrl,
        String messageType,
        LocalDateTime timestamp,
        boolean isSelf
) {}