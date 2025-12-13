package HeoJin.demoBlog.image.service;

import HeoJin.demoBlog.global.exception.refactor.ExternalServiceException;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Profile("!test")
public class ImageService {

    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile file, String folder) {
        try {
            // 폴더가 null이거나 빈 문자열이면 기본 폴더 사용
            if (folder == null || folder.trim().isEmpty()) {
                folder = "test";
            }

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folder,
                            "resource_type", "image",
                            "format", "avif",  // AVIF 로 변환
                            "quality", "auto"   // 품질 자동
                    ));

            String imageUrl = uploadResult.get("secure_url").toString();
            return imageUrl;

        } catch (Exception e) {
            throw new ExternalServiceException(extractStatusCode(e), "이미지 업로드 실패", e);
        }
    }

    public List<Map<String, Object>> getImageList(String folder) {
        try {
            // 폴더가 null이거나 빈 문자열이면 기본 폴더 사용
            if (folder == null || folder.trim().isEmpty()) {
                folder = "blog-images";
            }

            Map result = cloudinary.search()
                    .expression("folder:" + folder)
                    .sortBy("created_at", "desc")
                    .maxResults(100)
                    .execute();

            List<Map> resources = (List<Map>) result.get("resources");
            List<Map<String, Object>> imageList = new ArrayList<>();

            for (Map resource : resources) {
                Map<String, Object> imageInfo = Map.of(
                        "publicId", resource.get("public_id").toString(),
                        "secureUrl", resource.get("secure_url").toString(),
                        "originalFilename", resource.getOrDefault("original_filename", "unknown"),
                        "createdAt", resource.get("created_at").toString(),
                        "format", resource.get("format").toString(),
                        "bytes", resource.get("bytes"),
                        "width", resource.get("width"),
                        "height", resource.get("height")
                );
                imageList.add(imageInfo);
            }

            return imageList;

        } catch (Exception e) {
            throw new ExternalServiceException(extractStatusCode(e), "이미지 리스트 조회 실패", e);
        }
    }

    // 파일 삭제 로직
    public boolean deleteImage(String publicId) {
        try {
            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            String resultStatus = result.get("result").toString();

            // ok or not found (이미 없는 경우도 성공?으로)
            return "ok".equals(resultStatus) || "not found".equals(resultStatus);

        } catch (Exception e) {
            throw new ExternalServiceException(extractStatusCode(e), "이미지 삭제 실패", e);
        }
    }

    private int extractStatusCode(Exception e) {
        if (e == null || e.getMessage() == null) {
            return HttpServletResponse.SC_INTERNAL_SERVER_ERROR; // 500
        }

        String message = e.getMessage().toLowerCase();

        // 400 Bad Request 관련
        if (message.contains("bad request") ||
                message.contains("invalid") ||
                message.contains("file size") ||
                message.contains("unsupported format") ||
                message.contains("malformed")) {
            return HttpServletResponse.SC_BAD_REQUEST; // 400
        }

        // 401 Authorization required 관련
        if (message.contains("unauthorized") ||
                message.contains("authentication") ||
                message.contains("invalid api key") ||
                message.contains("api key")) {
            return HttpServletResponse.SC_UNAUTHORIZED; // 401
        }

        // 403 Not allowed 관련
        if (message.contains("forbidden") ||
                message.contains("not allowed") ||
                message.contains("permission denied")) {
            return HttpServletResponse.SC_FORBIDDEN; // 403
        }

        // 404 Not found 관련
        if (message.contains("not found") ||
                message.contains("resource not found")) {
            return HttpServletResponse.SC_NOT_FOUND; // 404
        }

        // 409 Already exists 관련
        if (message.contains("already exists") ||
                message.contains("conflict")) {
            return HttpServletResponse.SC_CONFLICT; // 409
        }

        // 420 Rate limited 관련 (Cloudinary 특별 코드)
        if (message.contains("rate limit") ||
                message.contains("too many requests")) {
            return 420; // Rate limited
        }

        // 기본값: 500 Internal Server Error
        return HttpServletResponse.SC_INTERNAL_SERVER_ERROR; // 500
    }
}