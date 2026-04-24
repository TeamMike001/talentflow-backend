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

        String contentType = file.getContentType();
        if (!isAllowedFileType(contentType, file.getOriginalFilename())) {
            throw new RuntimeException("Unsupported file type. Allowed: images, videos, PDF, and common document formats");
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

    private boolean isAllowedFileType(String contentType, String originalFilename) {
        if (contentType != null) {
            String normalizedType = contentType.toLowerCase();
            if (normalizedType.startsWith("image/") || normalizedType.startsWith("video/")) {
                return true;
            }

            if (normalizedType.contains("pdf")
                    || normalizedType.contains("text/")
                    || normalizedType.contains("msword")
                    || normalizedType.contains("officedocument")
                    || normalizedType.contains("document")
                    || normalizedType.contains("presentation")
                    || normalizedType.contains("spreadsheet")) {
                return true;
            }
        }

        if (originalFilename == null) {
            return false;
        }

        String lowerName = originalFilename.toLowerCase();
        return lowerName.endsWith(".pdf")
                || lowerName.endsWith(".doc")
                || lowerName.endsWith(".docx")
                || lowerName.endsWith(".ppt")
                || lowerName.endsWith(".pptx")
                || lowerName.endsWith(".txt")
                || lowerName.endsWith(".rtf")
                || lowerName.endsWith(".mp4")
                || lowerName.endsWith(".mov")
                || lowerName.endsWith(".avi")
                || lowerName.endsWith(".mkv")
                || lowerName.endsWith(".webm");
    }
}
