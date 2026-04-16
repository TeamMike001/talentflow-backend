package LearnX.com.example.LearnX.service;

import LearnX.com.example.LearnX.Model.Notification;
import LearnX.com.example.LearnX.Model.User;
import LearnX.com.example.LearnX.Repository.NotificationRepository;
import LearnX.com.example.LearnX.dtos.NotificationResponseDto;
import LearnX.com.example.LearnX.mapper.NotificationMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final UserService userService;

    public NotificationService(NotificationRepository notificationRepository,
                               NotificationMapper notificationMapper,
                               UserService userService) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
        this.userService = userService;
    }



    public void sendNotification(User recipient, String title, String message) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setRecipient(recipient);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    public List<NotificationResponseDto> getMyNotifications() {
        User currentUser = userService.getCurrentUser();
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(currentUser.getId())
                .stream()
                .map(notificationMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<NotificationResponseDto> getUnreadNotifications() {
        User currentUser = userService.getCurrentUser();
        return notificationRepository.findByRecipientIdAndIsReadFalse(currentUser.getId())
                .stream()
                .map(notificationMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Mark a notification as read
     */
    @Transactional
    public void markAsRead(Long notificationId) {
        User currentUser = userService.getCurrentUser();

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        // Security: Only the recipient can mark their own notification as read
        if (!notification.getRecipient().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied: You can only mark your own notifications as read");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    /**
     * Mark all notifications as read for current user
     */
    @Transactional
    public void markAllAsRead() {
        User currentUser = userService.getCurrentUser();
        List<Notification> unreadNotifications = notificationRepository.findByRecipientIdAndIsReadFalse(currentUser.getId());

        for (Notification notification : unreadNotifications) {
            notification.setRead(true);
        }
        notificationRepository.saveAll(unreadNotifications);
    }

    /**
     * Delete a notification (optional)
     */
    @Transactional
    public void deleteNotification(Long notificationId) {
        User currentUser = userService.getCurrentUser();

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getRecipient().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied");
        }

        notificationRepository.delete(notification);
    }
}