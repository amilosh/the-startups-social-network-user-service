package school.faang.user_service.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service{
    private final AmazonS3 s3client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Override
    public void uploadFile(String key, InputStream fileInputStream, ObjectMetadata metadata) {
        try {
            s3client.putObject(bucketName, key, fileInputStream, metadata);
        } catch (Exception e) {
            log.error("Error uploading file to storage", e);
            throw new RuntimeException("Error uploading file to storage", e);
        }
    }

    @Override
    public void deleteFile(String key) {
        s3client.deleteObject(bucketName, key);
    }

    @Override
    public InputStream downloadFile(String key) {
        S3Object object = s3client.getObject(bucketName, key);
        return object.getObjectContent();
    }
}
