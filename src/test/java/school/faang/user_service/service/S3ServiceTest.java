package school.faang.user_service.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.util.ImageUtils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class S3ServiceTest {

    @InjectMocks
    private S3Service s3Service;

    @Mock
    private AmazonS3 s3Client;

    @Mock
    private ImageUtils imageUtils;

    @Value("${services.s3.bucketName}")
    private String bucket;

    private final String key = "key";

    @Test
    void testUploadImage() {
        MultipartFile file = new MockMultipartFile("file", "test.jpg",
                "image/jpeg", new byte[5000]);
        when(s3Client.putObject(any(PutObjectRequest.class))).thenReturn(new PutObjectResult());

        s3Service.uploadImage(file, "package", "image",
                new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB));

        verify(s3Client).putObject(any(PutObjectRequest.class));
    }

    @Test
    void tesGetFile() {
        InputStream mockInputStream = new ByteArrayInputStream("Mock file content".getBytes());
        S3ObjectInputStream s3ObjectInputStream = new S3ObjectInputStream(mockInputStream, null);

        S3Object s3Object = new S3Object();
        s3Object.setObjectContent(s3ObjectInputStream);

        when(s3Client.getObject(bucket, key)).thenReturn(s3Object);

        s3Service.getFile(key);

        verify(s3Client).getObject(bucket, key);
    }

    @Test
    void testDeleteFile() {
        s3Service.deleteFiles(key);

        verify(s3Client).deleteObjects(any());
    }
}
