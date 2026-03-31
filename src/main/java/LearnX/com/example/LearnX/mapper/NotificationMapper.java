package LearnX.com.example.LearnX.mapper;

import LearnX.com.example.LearnX.Model.Notification;
import LearnX.com.example.LearnX.dtos.NotificationResponseDto;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationResponseDto toResponseDto(Notification notification) {
        return new NotificationResponseDto(
                notification.getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}