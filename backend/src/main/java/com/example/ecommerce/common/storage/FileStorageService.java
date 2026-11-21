package com.example.ecommerce.common.storage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.ecommerce.common.exception.BadRequestException;

@Service
public class FileStorageService {

    private static final String URL_PREFIX = "/uploads/";

    private static final Map<String, String> ALLOWED_TYPES = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp");

    private final Path root;

    public FileStorageService(@Value("${app.upload.dir:uploads}") String uploadDir) {
        this.root = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    public String storeImage(MultipartFile file, String subDir) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Vui lòng chọn file ảnh");
        }
        String extension = ALLOWED_TYPES.get(file.getContentType());
        if (extension == null) {
            throw new BadRequestException("Chỉ chấp nhận ảnh JPG, PNG hoặc WEBP");
        }

        try {
            byte[] header = readHeader(file);
            if (!matchesImageSignature(header, extension)) {
                throw new BadRequestException("Nội dung file không phải ảnh hợp lệ");
            }

            Path dir = root.resolve(subDir).normalize();
            Files.createDirectories(dir);
            String fileName = UUID.randomUUID() + extension;
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, dir.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            }
            return URL_PREFIX + subDir + "/" + fileName;
        } catch (IOException e) {
            throw new IllegalStateException("Không lưu được file", e);
        }
    }

    public void deleteByUrl(String url) {
        if (url == null || !url.startsWith(URL_PREFIX)) {
            return;
        }
        Path target = root.resolve(url.substring(URL_PREFIX.length())).normalize();
        if (!target.startsWith(root)) {
            return;
        }
        try {
            Files.deleteIfExists(target);
        } catch (IOException ignored) {
            // xóa file thất bại không làm hỏng nghiệp vụ
        }
    }

    private byte[] readHeader(MultipartFile file) throws IOException {
        try (InputStream in = file.getInputStream()) {
            return in.readNBytes(12);
        }
    }

    // Kiểm tra vài byte đầu của file để chắc nó đúng là ảnh (Content-Type do client gửi nên có thể bị giả)
    private boolean matchesImageSignature(byte[] h, String extension) {
        if (h.length < 12) {
            return false;
        }
        switch (extension) {
            case ".jpg":
                return (h[0] & 0xFF) == 0xFF && (h[1] & 0xFF) == 0xD8 && (h[2] & 0xFF) == 0xFF;
            case ".png":
                return (h[0] & 0xFF) == 0x89 && h[1] == 'P' && h[2] == 'N' && h[3] == 'G';
            case ".webp":
                return h[0] == 'R' && h[1] == 'I' && h[2] == 'F' && h[3] == 'F'
                        && h[8] == 'W' && h[9] == 'E' && h[10] == 'B' && h[11] == 'P';
            default:
                return false;
        }
    }
}
