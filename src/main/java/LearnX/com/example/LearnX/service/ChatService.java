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
    private final NotificationService notificationService;

    public ChatService(ChatMessageRepository chatMessageRepository,
                       PrivateMessageRepository privateMessageRepository,
                       UserService userService,
                       NotificationService notificationService) {
        this.chatMessageRepository = chatMessageRepository;
        this.privateMessageRepository = privateMessageRepository;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    // ========== GROUP CHAT METHODS WITH TAGGING ==========

    @Transactional
    public ChatMessageResponse saveGroupMessage(ChatMessage message, User sender, List<String> taggedUserEmails) {
        try {
            logger.info("Saving group message from user: {} (ID: {})", sender.getEmail(), sender.getId());
            logger.info("Message content: {}", message.getContent());
            logger.info("Tagged users: {}", taggedUserEmails);

            message.setUser(sender);
            message.setChatType("group");
            message.setCreatedAt(LocalDateTime.now());

            // Process tagged users
            List<Long> mentionedUserIds = new ArrayList<>();
            List<String> taggedUserNames = new ArrayList<>();
            if (taggedUserEmails != null && !taggedUserEmails.isEmpty()) {
                for (String email : taggedUserEmails) {
                    try {
                        User taggedUser = userService.findByEmail(email);
                        if (taggedUser != null) {
                            mentionedUserIds.add(taggedUser.getId());
                            taggedUserNames.add(taggedUser.getName());
                        }
                    } catch (Exception e) {
                        logger.warn("Could not find user with email: {}", email);
                    }
                }
                message.setMentionedUserIds(mentionedUserIds);
            }

            ChatMessage saved = chatMessageRepository.save(message);
            logger.info("✅ Message saved successfully with ID: {}", saved.getId());

            return toGroupResponseDto(saved, sender.getId(), taggedUserNames);

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
        return toGroupResponseDto(message, currentUserId, null);
    }

    private ChatMessageResponse toGroupResponseDto(ChatMessage message, Long currentUserId, List<String> taggedUserNames) {
        User user = message.getUser();
        String avatarUrl = "https://ui-avatars.com/api/?background=2563EB&color=fff&name=" +
                (user.getName() != null && !user.getName().isEmpty() ? user.getName().charAt(0) : 'U');

        // Get tagged user names if not provided
        if (taggedUserNames == null && message.getMentionedUserIds() != null) {
            taggedUserNames = new ArrayList<>();
            for (Long userId : message.getMentionedUserIds()) {
                try {
                    User taggedUser = userService.getUserEntityById(userId);
                    taggedUserNames.add(taggedUser.getName());
                } catch (Exception e) {
                    logger.warn("Could not find user with ID: {}", userId);
                }
            }
        }

        return new ChatMessageResponse(
                message.getId(),
                message.getContent(),
                message.getMessageType() != null ? message.getMessageType() : "text",
                user.getId(),
                user.getName() != null ? user.getName() : "User",
                user.getRole().name(),
                message.getCreatedAt(),
                avatarUrl,
                taggedUserNames != null ? taggedUserNames : new ArrayList<>()
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

        return new PrivateMessageResponse(
                message.getId(),
                sender.getId(),
                sender.getName(),
                sender.getRole(),
                recipient.getId(),
                recipient.getName(),
                message.getContent(),
                message.getFileUrl(),
                message.getMessageType(),
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
                        String avatarUrl = "https://ui-avatars.com/api/?background=" +
                                (user.getRole() == Role.INSTRUCTOR ? "2563EB" : "16A34A") +
                                "&color=fff&name=" + (user.getName() != null ? user.getName().charAt(0) : 'U');
                        return new UserStatusDto(
                                user.getId(),
                                user.getName(),
                                user.getEmail(),
                                user.getRole().name(),
                                user.isOnline(),
                                user.getLastActiveAt(),
                                avatarUrl
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
            String avatarUrl = "https://ui-avatars.com/api/?background=" +
                    (user.getRole() == Role.INSTRUCTOR ? "2563EB" : "16A34A") +
                    "&color=fff&name=" + (user.getName() != null ? user.getName().charAt(0) : 'U');

            return new UserStatusDto(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole().name(),
                    user.isOnline(),
                    user.getLastActiveAt(),
                    avatarUrl
            );
        } catch (Exception e) {
            logger.error("Error getting user status for ID {}: {}", userId, e.getMessage());
            return null;
        }
    }
}