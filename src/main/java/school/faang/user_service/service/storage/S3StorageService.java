package school.faang.user_service.service.storage;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.exception.StorageException;

import java.io.ByteArrayInputStream;

@Service
@RequiredArgsConstructor
public class S3StorageService implements StorageService {

    private final AmazonS3 amazonS3;

    @Value("${storage.type}")
    private String storageType;

    @Value("${storage.bucketName}")
    private String bucketName;


    @Override
    public void uploadFile(String fileName, byte[] content, String contentType) {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType);
            metadata.setContentLength(content.length);

            ByteArrayInputStream byteArray = new ByteArrayInputStream(content);

            amazonS3.putObject(new PutObjectRequest(bucketName, fileName, byteArray, metadata));
        } catch (RuntimeException error) {
            throw new StorageException("Failed to upload file: " + fileName, error);
        }
    }

    @Override
    public void deleteFile(String fileName) {
        try {
            amazonS3.deleteObject(bucketName, fileName);
        } catch (Exception error) {
            throw new StorageException("Failed to delete file: " + fileName, error);
        }
    }

    @Override
    public boolean ifFileExists(String fileName) {
        return amazonS3.doesObjectExist(bucketName, fileName);
    }
}
