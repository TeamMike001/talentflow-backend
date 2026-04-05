package LearnX.com.example.LearnX.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class FileUploadService {

    private final Cloudinary cloudinary;

    public FileUploadService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    /**
     * Upload file to Cloudinary and return secure URL
     */
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        // Optional: Validate file type (pdf, doc, images, etc.)
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.startsWith("image/") && 
            !contentType.contains("pdf") && !contentType.contains("document"))) {
            throw new RuntimeException("Unsupported file type. Allowed: images, PDF, documents");
        }

        Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", folder != null ? folder : "learnx/assignments",
                        "resource_type", "auto"   // handles images, pdf, docs etc.
                )
        );

        return (String) uploadResult.get("secure_url");
    }
}