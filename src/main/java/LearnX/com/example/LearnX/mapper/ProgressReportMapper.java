package LearnX.com.example.LearnX.mapper;

import LearnX.com.example.LearnX.Model.ProgressReport;
import LearnX.com.example.LearnX.dtos.ProgressReportResponseDto;
import org.springframework.stereotype.Component;

@Component
public class ProgressReportMapper {

    public ProgressReportResponseDto toResponseDto(ProgressReport report) {
        return new ProgressReportResponseDto(
                report.getId(),
                report.getReportText(),
                report.getProgressPercent(),
                report.getCreatedAt(),
                report.getUser() != null ? report.getUser().getId() : null
        );
    }
}