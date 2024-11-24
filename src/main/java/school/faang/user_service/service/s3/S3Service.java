package school.faang.user_service.service.s3;

import com.amazonaws.services.s3.model.ObjectMetadata;

import java.io.InputStream;

public interface S3Service {
    void uploadFile(String key, InputStream fileInputStream, ObjectMetadata metadata);

    void deleteFile(String key);

    InputStream downloadFile(String key);
}
