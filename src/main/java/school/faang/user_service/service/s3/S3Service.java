package school.faang.user_service.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.validator.s3.S3Validator;

import java.io.InputStream;

import static org.apache.hc.core5.http.ContentType.IMAGE_SVG;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 amazonS3;
    private final S3Validator s3Validator;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Value("${services.s3.avatarMaxSize}")
    private Integer picMaxSize;

    @Value("${services.s3.avatarContentType}")
    private String contentType;

    public void uploadFile(MultipartFile file, User user) {
        s3Validator.validateFileSize(file, picMaxSize);
        s3Validator.validateContentType(file, contentType);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(IMAGE_SVG.getMimeType());
        try {
            amazonS3.putObject(bucketName, createFileKey(user), file.getInputStream(), metadata);
        } catch (Exception e) {
            log.error("Failed to get byte stream to read from file {}", file.getName());
            throw new IllegalStateException(e);
        }
    }

    public InputStream getFile(String fileKey) {
        S3Object s3Object = amazonS3.getObject(bucketName, fileKey);

        return s3Object.getObjectContent();
    }

    private String createFileKey(User user) {
        if (user.getUserProfilePic() == null) {
            user.setUserProfilePic(new UserProfilePic());
        }
        String key = user.getUserProfilePic().getFileId();
        if (key == null || key.isEmpty()) {
            key = String.format("%d%d", System.currentTimeMillis(), user.getId());
            user.getUserProfilePic().setFileId(key);
        }

        return key;
    }
}
