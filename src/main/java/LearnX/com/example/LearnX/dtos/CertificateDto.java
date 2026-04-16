package LearnX.com.example.LearnX.dtos;


import java.time.LocalDateTime;

public record CertificateDto(Long id, Long courseId, String courseTitle, LocalDateTime issuedAt) {}