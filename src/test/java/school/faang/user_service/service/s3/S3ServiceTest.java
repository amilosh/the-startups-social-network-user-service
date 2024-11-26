package school.faang.user_service.service.s3;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class S3ServiceTest {
    @Mock
    private AmazonS3 amazonS3;

    @InjectMocks
    private S3Service s3Service;

    private String bucketName = "test-bucket";
    private String key = "test-key";
    private String presignedUrl = "https://presigned-url.com";

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(s3Service, "bucketName", bucketName);
    }

    @Test
    public void generatePresignedUrlShouldReturnUrlWhenCalled() {
        URL mockedUrl ;

        try {
            mockedUrl=new URL(presignedUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }


        when(amazonS3.generatePresignedUrl(any(GeneratePresignedUrlRequest.class))).thenReturn(mockedUrl);

        String result = s3Service.generatePresignedUrl(key);

        assertEquals(presignedUrl, result);

        verify(amazonS3).generatePresignedUrl(argThat(request ->
                request.getBucketName().equals(bucketName) &&
                        request.getKey().equals(key) &&
                        request.getMethod().equals(HttpMethod.GET) &&
                        request.getExpiration().after(Date.from(Instant.now()))
        ));
    }
}