package school.faang.user_service.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.util.ImageUtils;

import java.awt.image.BufferedImage;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 s3Client;
    private final ImageUtils imageUtils;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    public String uploadImage(MultipartFile originalFile, String folderName, String fileName, BufferedImage img) {
        log.debug("Trying to upload image to S3");

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(originalFile.getContentType());
        String key = String.format("%s/%s", folderName, fileName);

        PutObjectRequest request = new PutObjectRequest(bucketName, key,
                imageUtils.bufferedImageToInputStream(img, originalFile), objectMetadata);
        s3Client.putObject(request);

        return key;
    }

    public InputStreamResource getFile(String key) {
        log.debug("Trying to get image from S3");
        return new InputStreamResource(s3Client.getObject(bucketName, key).getObjectContent());
    }

    public void deleteFiles(String... keys) {
        log.debug("Trying to delete files from S3");
        DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName)
                .withKeys(keys);
        s3Client.deleteObjects(deleteObjectsRequest);
    }
}
