package LearnX.com.example.LearnX.service;

import LearnX.com.example.LearnX.Enum.Role;
import LearnX.com.example.LearnX.Model.ChatMessage;
import LearnX.com.example.LearnX.Model.PrivateMessage;
import LearnX.com.example.LearnX.Model.User;
import LearnX.com.example.LearnX.Repository.ChatMessageRepository;
import LearnX.com.example.LearnX.Repository.PrivateMessageRepository;
import LearnX.com.example.LearnX.dtos.ChatMessageResponse;
import LearnX.com.example.LearnX.dtos.PrivateMessageResponse;
import LearnX.com.example.LearnX.dtos.UserStatusDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {
    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    private final ChatMessageRepository chatMessageRepository;
    private final PrivateMessageRepository privateMessageRepository;
    private final UserService userService;

    public ChatService(ChatMessageRepository chatMessageRepository,
                       PrivateMessageRepository privateMessageRepository,
                       UserService userService) {
        this.chatMessageRepository = chatMessageRepository;
        this.privateMessageRepository = privateMessageRepository;
        this.userService = userService;
    }

    // ========== GROUP CHAT METHODS ==========

    @Transactional
    public ChatMessageResponse saveGroupMessage(ChatMessage message, User sender) {
        try {
            logger.info("Saving group message from user: {} (ID: {})", sender.getEmail(), sender.getId());
            logger.info("Message content: {}", message.getContent());

            message.setUser(sender);
            message.setChatType("group");
            message.setCreatedAt(LocalDateTime.now());

            ChatMessage saved = chatMessageRepository.save(message);
            logger.info("✅ Message saved successfully with ID: {}", saved.getId());

            return toGroupResponseDto(saved, sender.getId());

        } catch (Exception e) {
            logger.error("❌ Failed to save message: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save message: " + e.getMessage(), e);
        }
    }

    public List<ChatMessageResponse> getGroupMessages() {
        try {
            User currentUser = userService.getCurrentUser();
            List<ChatMessage> messages = chatMessageRepository.findRecentGroupMessages();
            logger.info("Retrieved {} group messages", messages.size());

            return messages.stream()
                    .sorted((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()))
                    .map(msg -> toGroupResponseDto(msg, currentUser.getId()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting group messages: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    private ChatMessageResponse toGroupResponseDto(ChatMessage message, Long currentUserId) {
        User user = message.getUser();
        String content = message.getContent();

        String userName = user.getName() != null ? user.getName() : user.getEmail().split("@")[0];
        char firstChar = userName.charAt(0);
        String avatar = "https://ui-avatars.com/api/?background=2563EB&color=fff&name=" + firstChar;

        return new ChatMessageResponse(
                message.getId(),
                user.getId(),
                userName,
                avatar,
                content,
                message.getFileUrl(),
                message.getMessageType() != null ? message.getMessageType() : "text",
                message.getCreatedAt(),
                user.getId().equals(currentUserId),
                "group"
        );
    }

    // ========== PRIVATE CHAT METHODS ==========

    @Transactional
    public PrivateMessageResponse savePrivateMessage(User sender, Long recipientId, String content, String fileUrl, String messageType) {
        try {
            logger.info("Saving private message from user: {} to recipient: {}", sender.getId(), recipientId);

            User recipient = userService.getUserEntityById(recipientId);
            PrivateMessage message = new PrivateMessage();
            message.setContent(content);
            message.setSender(sender);
            message.setRecipient(recipient);
            message.setFileUrl(fileUrl);
            message.setMessageType(messageType != null ? messageType : "text");
            message.setCreatedAt(LocalDateTime.now());

            PrivateMessage saved = privateMessageRepository.save(message);
            logger.info("✅ Private message saved successfully with ID: {}", saved.getId());

            return toPrivateResponseDto(saved, sender.getId());

        } catch (Exception e) {
            logger.error("❌ Failed to save private message: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save private message: " + e.getMessage(), e);
        }
    }

    public List<PrivateMessageResponse> getConversation(Long otherUserId) {
        try {
            User currentUser = userService.getCurrentUser();
            List<PrivateMessage> messages = privateMessageRepository.findConversation(currentUser.getId(), otherUserId);
            logger.info("Retrieved {} private messages with user {}", messages.size(), otherUserId);

            return messages.stream()
                    .map(msg -> toPrivateResponseDto(msg, currentUser.getId()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting conversation: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public List<User> getChatPartners() {
        try {
            User currentUser = userService.getCurrentUser();
            List<User> partners = privateMessageRepository.findChatPartners(currentUser.getId());
            logger.info("Retrieved {} chat partners", partners.size());
            return partners;
        } catch (Exception e) {
            logger.error("Error getting chat partners: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    private PrivateMessageResponse toPrivateResponseDto(PrivateMessage message, Long currentUserId) {
        User sender = message.getSender();
        User recipient = message.getRecipient();
        String content = message.getContent();

        String senderName = sender.getName() != null ? sender.getName() : sender.getEmail().split("@")[0];
        String recipientName = recipient.getName() != null ? recipient.getName() : recipient.getEmail().split("@")[0];

        return new PrivateMessageResponse(
                message.getId(),
                sender.getId(),
                senderName,
                sender.getRole(),  // This is the Role enum
                recipient.getId(),
                recipientName,
                content,
                message.getFileUrl(),
                message.getMessageType() != null ? message.getMessageType() : "text",
                message.getCreatedAt(),
                sender.getId().equals(currentUserId)
        );
    }

    // ========== USER STATUS METHODS ==========

    public List<UserStatusDto> getActiveUsers() {
        try {
            List<User> allUsers = userService.getAllUsersEntities();
            List<UserStatusDto> activeUsers = allUsers.stream()
                    .filter(User::isOnline)
                    .map(user -> {
                        String userName = user.getName() != null ? user.getName() : user.getEmail().split("@")[0];
                        char firstChar = userName.charAt(0);
                        String avatar = "https://ui-avatars.com/api/?background=2563EB&color=fff&name=" + firstChar;
                        return new UserStatusDto(
                                user.getId(),
                                userName,
                                avatar,
                                true,
                                user.getLastActiveAt()
                        );
                    })
                    .collect(Collectors.toList());

            logger.info("Found {} active users", activeUsers.size());
            return activeUsers;
        } catch (Exception e) {
            logger.error("Error getting active users: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public UserStatusDto getUserStatus(Long userId) {
        try {
            User user = userService.getUserEntityById(userId);
            String userName = user.getName() != null ? user.getName() : user.getEmail().split("@")[0];
            char firstChar = userName.charAt(0);
            String avatar = "https://ui-avatars.com/api/?background=2563EB&color=fff&name=" + firstChar;

            return new UserStatusDto(
                    user.getId(),
                    userName,
                    avatar,
                    user.isOnline(),
                    user.getLastActiveAt()
            );
        } catch (Exception e) {
            logger.error("Error getting user status for ID {}: {}", userId, e.getMessage());
            return null;
        }
    }

    // ========== RECENT MESSAGES (Combined) ==========

    public List<ChatMessageResponse> getRecentMessages() {
        try {
            User currentUser = userService.getCurrentUser();
            List<ChatMessageResponse> allMessages = new ArrayList<>();

            // Add group messages
            List<ChatMessage> groupMessages = chatMessageRepository.findRecentGroupMessages();
            allMessages.addAll(groupMessages.stream()
                    .map(msg -> toGroupResponseDto(msg, currentUser.getId()))
                    .collect(Collectors.toList()));

            // Add private messages where user is involved
            List<PrivateMessage> privateMessages = privateMessageRepository.findRecentPrivateMessages(currentUser.getId());
            allMessages.addAll(privateMessages.stream()
                    .map(msg -> toPrivateMessageResponseDto(msg, currentUser.getId()))
                    .collect(Collectors.toList()));

            // Sort by timestamp descending (newest first) and limit to 50
            allMessages.sort((a, b) -> b.timestamp().compareTo(a.timestamp()));
            return allMessages.stream().limit(50).collect(Collectors.toList());

        } catch (Exception e) {
            logger.error("Error getting recent messages: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    private ChatMessageResponse toPrivateMessageResponseDto(PrivateMessage message, Long currentUserId) {
        User sender = message.getSender();
        String content = message.getContent();

        String senderName = sender.getName() != null ? sender.getName() : sender.getEmail().split("@")[0];
        char firstChar = senderName.charAt(0);
        String avatar = "https://ui-avatars.com/api/?background=2563EB&color=fff&name=" + firstChar;

        return new ChatMessageResponse(
                message.getId(),
                sender.getId(),
                senderName,
                avatar,
                content,
                message.getFileUrl(),
                message.getMessageType() != null ? message.getMessageType() : "text",
                message.getCreatedAt(),
                sender.getId().equals(currentUserId),
                "private"
        );
    }
}