package LearnX.com.example.LearnX.mapper;

import LearnX.com.example.LearnX.Model.ActivityLog;
import LearnX.com.example.LearnX.dtos.ActivityLogResponseDto;
import org.springframework.stereotype.Component;

@Component
public class ActivityLogMapper {

    public ActivityLogResponseDto toResponseDto(ActivityLog log) {
        return new ActivityLogResponseDto(
                log.getId(),
                log.getAction(),
                log.getCreatedAt(),
                log.isCompleted(),
                log.getLesson() != null ? log.getLesson().getId() : null,
                log.getUser() != null ? log.getUser().getId() : null
        );
    }
}