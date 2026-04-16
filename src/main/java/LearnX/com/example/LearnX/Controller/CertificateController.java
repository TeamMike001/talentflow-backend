package LearnX.com.example.LearnX.Controller;

import LearnX.com.example.LearnX.dtos.CertificateDto;
import LearnX.com.example.LearnX.service.CertificateService;
import org.springframework.core.io.Resource;          // ✅ correct import
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/certificates")
public class CertificateController {
    private final CertificateService certificateService;

    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @GetMapping
    public ResponseEntity<List<CertificateDto>> getMyCertificates() {
        return ResponseEntity.ok(certificateService.getMyCertificates());
    }

    @GetMapping("/{courseId}/download")
    public ResponseEntity<Resource> downloadCertificate(@PathVariable Long courseId) {
        Resource resource = certificateService.downloadCertificate(courseId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"certificate.pdf\"")
                .body(resource);
    }
}