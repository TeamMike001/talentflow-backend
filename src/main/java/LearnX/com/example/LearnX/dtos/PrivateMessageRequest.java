package LearnX.com.example.LearnX.dtos;

public record PrivateMessageRequest(Long recipientId, String content, String fileUrl, String messageType) {}
