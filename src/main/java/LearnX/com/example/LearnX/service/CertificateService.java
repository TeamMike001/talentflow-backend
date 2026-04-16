package LearnX.com.example.LearnX.service;

import LearnX.com.example.LearnX.Model.Enrollment;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import LearnX.com.example.LearnX.Enum.Role;
import LearnX.com.example.LearnX.Model.Certificate;
import LearnX.com.example.LearnX.Model.Course;
import LearnX.com.example.LearnX.Model.User;
import LearnX.com.example.LearnX.Repository.CertificateRepository;
import LearnX.com.example.LearnX.Repository.EnrollmentRepository;
import LearnX.com.example.LearnX.dtos.CertificateDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CertificateService {
    private final CertificateRepository certificateRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserService userService;
    private final CourseService courseService;

    @Value("${certificate.storage.path}")
    private String storagePath;

    public CertificateService(CertificateRepository certificateRepository,
                              EnrollmentRepository enrollmentRepository,
                              UserService userService,
                              CourseService courseService) {
        this.certificateRepository = certificateRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.userService = userService;
        this.courseService = courseService;
    }

    @Transactional
    public void generateCertificateIfCompleted(Long courseId) {
        User student = userService.getCurrentUser();
        if (student.getRole() != Role.STUDENT) return;

        // Already exists?
        if (certificateRepository.findByStudentIdAndCourseId(student.getId(), courseId).isPresent())
            return;

        // Check progress – get the first enrollment (should be only one)
        List<Enrollment> enrollments = enrollmentRepository.findByStudentIdAndCourseId(student.getId(), courseId);
        int progress = 0;
        if (!enrollments.isEmpty()) {
            progress = enrollments.get(0).getProgressPercentage() != null ? enrollments.get(0).getProgressPercentage() : 0;
        }
        if (progress < 100) return;

        Course course = courseService.getCourseEntityById(courseId);
        generatePdf(student, course);
    }

    private void generatePdf(User student, Course course) {
        try {
            Path dir = Paths.get(storagePath);
            if (!Files.exists(dir)) Files.createDirectories(dir);
            String fileName = "cert_" + student.getId() + "_" + course.getId() + ".pdf";
            Path filePath = dir.resolve(fileName);

            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(filePath.toFile()));
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            Paragraph title = new Paragraph("Certificate of Completion", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("This certificate is awarded to:", normalFont));
            document.add(new Paragraph(student.getName(), titleFont));
            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("For successfully completing the course:", normalFont));
            document.add(new Paragraph(course.getTitle(), titleFont));
            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("Date: " + LocalDateTime.now().toLocalDate(), normalFont));

            document.close();

            Certificate cert = new Certificate();
            cert.setStudent(student);
            cert.setCourse(course);
            cert.setFilePath(filePath.toString());
            cert.setIssuedAt(LocalDateTime.now());
            certificateRepository.save(cert);
        } catch (DocumentException | IOException e) {
            throw new RuntimeException("Failed to generate certificate", e);
        }
    }

    public Resource downloadCertificate(Long courseId) {
        User student = userService.getCurrentUser();
        Certificate cert = certificateRepository.findByStudentIdAndCourseId(student.getId(), courseId)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));
        File file = new File(cert.getFilePath());
        if (!file.exists()) throw new RuntimeException("Certificate file missing");
        return new FileSystemResource(file);
    }

    public List<CertificateDto> getMyCertificates() {
        User student = userService.getCurrentUser();
        return certificateRepository.findByStudentId(student.getId()).stream()
                .map(c -> new CertificateDto(c.getId(), c.getCourse().getId(), c.getCourse().getTitle(), c.getIssuedAt()))
                .collect(Collectors.toList());
    }
}