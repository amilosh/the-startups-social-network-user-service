package school.faang.user_service.service;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

@Service
@RequiredArgsConstructor
public class MinioService {
    private final MinioClient minioClient;

    @Value("${storage.bucketName}")
    private String bucketName;

    @Value("${minio.url}")
    private String minioUrl;

    public String uploadSvgFileToMinio(String fileName, byte[] content) {
        try {
            ensureBucketExists(bucketName);
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(new ByteArrayInputStream(content), content.length, -1)
                            .contentType("image/svg+xml")
                            .build()
            );

            return minioUrl + "/" + bucketName + "/" + fileName;

        } catch (Exception e) {
            throw new RuntimeException("Error uploading file to Minio", e);
        }
    }

    private void ensureBucketExists(String bucketName) {
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error checking or creating bucket", e);
        }
    }
}
