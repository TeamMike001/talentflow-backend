package LearnX.com.example.LearnX.dtos;

import java.time.LocalDateTime;

public record ChatMessageResponse(
        Long id,
        Long userId,
        String userName,
        String userAvatar,
        String content,
        String fileUrl,
        String messageType,
        LocalDateTime timestamp,
        boolean self,
        String chatType
) {}