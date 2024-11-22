package school.faang.user_service.service.minio;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.exception.MinioException;
import school.faang.user_service.properties.MinioProperties;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static school.faang.user_service.exception.ErrorMessage.MINIO_DELETED_ERROR;
import static school.faang.user_service.exception.ErrorMessage.MINIO_DOWNLOAD_ERROR;
import static school.faang.user_service.exception.ErrorMessage.MINIO_UPLOAD_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {
    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    public void uploadFile(Long userId, String fileName, byte[] data, String contentType) {
        log.info("User with id {} - uploading file {}", userId, fileName);
        try (InputStream inputStream = new ByteArrayInputStream(data)) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(fileName)
                            .stream(inputStream, data.length, -1)
                            .contentType(contentType)
                            .build()
            );
            log.info("File {} uploaded successfully for user {}", fileName, userId);
        } catch (Exception e) {
            log.error("Error uploading file {} for user {}", fileName, userId, e);
            throw new MinioException(String.format(MINIO_UPLOAD_ERROR, fileName), e);
        }
    }

    public byte[] downloadFile(String objectKey) {
        log.info("Downloading file with key {}", objectKey);
        try (InputStream inputStream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(minioProperties.getBucketName())
                        .object(objectKey)
                        .build()
        )) {
            byte[] data = inputStream.readAllBytes();
            log.info("File {} downloaded successfully", objectKey);
            return data;
        } catch (Exception e) {
            log.error("Error downloading file {}", objectKey, e);
            throw new MinioException(String.format(MINIO_DOWNLOAD_ERROR, objectKey), e);
        }
    }

    public void deleteFile(String objectKey) {
        log.info("Deleting file with key {}", objectKey);
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(objectKey)
                            .build()
            );
            log.info("File {} deleted successfully", objectKey);
        } catch (Exception e) {
            log.error("Error deleting file {}", objectKey, e);
            throw new MinioException(String.format(MINIO_DELETED_ERROR, objectKey), e);
        }
    }
}
