package school.faang.user_service.service.storage;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

@Service
@RequiredArgsConstructor
public class S3StorageService implements StorageService {

    private final AmazonS3 amazonS3;

    @Value("${storage.type}")
    private String storageType;

    @Value("${minio.bucketName}")
    private String minioBucketName;

    @Value("${aws.bucketName}")
    private String awsBucketName;

    private String getBucketName() {
        if ("aws".equalsIgnoreCase(storageType)) {
            return awsBucketName;
        } else if ("minio".equalsIgnoreCase(storageType)) {
            return minioBucketName;
        } else {
            throw new IllegalArgumentException("Invalid storage.type: " + storageType);
        }
    }

    @Override
    public void uploadFile(String fileName, byte[] content, String contentType) {
        String bucketName = getBucketName();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        metadata.setContentLength(content.length);

        ByteArrayInputStream byteArray = new ByteArrayInputStream(content);

        amazonS3.putObject(new PutObjectRequest(bucketName, fileName, byteArray, metadata));
    }

    @Override
    public void deleteFile(String fileName) {
        String bucketName = getBucketName();
        amazonS3.deleteObject(bucketName, fileName);
    }

    @Override
    public Resource downloadFile(String fileName) {
        String bucketName = getBucketName();
        S3Object s3Object = amazonS3.getObject(bucketName, fileName);
        return new InputStreamResource(s3Object.getObjectContent());
    }

    @Override
    public boolean fileExists(String fileName) {
        String bucketName = getBucketName();
        return amazonS3.doesObjectExist(bucketName, fileName);
    }
}
