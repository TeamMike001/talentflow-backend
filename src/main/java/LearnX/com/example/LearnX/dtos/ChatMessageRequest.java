package LearnX.com.example.LearnX.dtos;

import java.util.List;

public record ChatMessageRequest(
        String content,
        String fileUrl,
        String messageType,
        List<String> taggedUsers
) {}