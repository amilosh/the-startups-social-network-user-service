package school.faang.user_service.config.minio;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.SetBucketPolicyArgs;
import io.minio.errors.MinioException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class MinioConfig {

    @Value("${minio.url}")
    private String minioUrl;

    @Value("${minio.accessKey}")
    private String accessKey;

    @Value("${minio.secretKey}")
    private String secretKey;

    @Value("${minio.publicPolicy}")
    private String publicPolicy;

    @Value("${minio.bucketName}")
    private String bucketName;

    @Bean
    public MinioClient minioClient() {
        MinioClient client = MinioClient.builder()
                .endpoint(minioUrl)
                .credentials(accessKey, secretKey)
                .build();

        createBucketIfNotExists(client);
        applyPublicPolicy(client);

        return client;
    }

    private void createBucketIfNotExists(MinioClient client) {
        try {
            boolean bucketExists = client.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!bucketExists) {
                client.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("Bucket created: {}", bucketName);
            } else {
                log.info("Bucket already exists: {}", bucketName);
            }
        } catch (MinioException e) {
            log.error("Error creating bucket: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
        }
    }

    private void applyPublicPolicy(MinioClient client) {
        try {
            client.setBucketPolicy(
                    SetBucketPolicyArgs.builder()
                            .bucket(bucketName)
                            .config(publicPolicy)
                            .build()
            );
            log.info("Public policy applied to bucket: {}", bucketName);
        } catch (MinioException e) {
            log.error("Error applying public policy: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
        }
    }
}
