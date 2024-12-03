package school.faang.user_service.storage;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.service.storage.S3StorageService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3StorageServiceTest {

    @Mock
    private AmazonS3 amazonS3;

    @InjectMocks
    private S3StorageService s3StorageService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(s3StorageService, "bucketName", "testbucket");
    }


    @Test
    void uploadFile_Success() {
        String fileName = "testFile.jpeg";
        byte[] content = new byte[]{1, 2, 3};
        String contentType = "image/jpeg";

        s3StorageService.uploadFile(fileName, content, contentType);

        ArgumentCaptor<PutObjectRequest> captor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(amazonS3).putObject(captor.capture());

        PutObjectRequest request = captor.getValue();
        assertEquals("testbucket", request.getBucketName());
        assertEquals(fileName, request.getKey());
        assertEquals(contentType, request.getMetadata().getContentType());
    }

    @Test
    void deleteFile_Success() {
        String fileName = "testFile.jpeg";

        s3StorageService.deleteFile(fileName);

        verify(amazonS3).deleteObject("testbucket", fileName);
    }

    @Test
    void fileExists_True() throws Exception {
        String fileName = "testFile.jpeg";
        when(amazonS3.doesObjectExist("testbucket", fileName)).thenReturn(true);

        boolean exists = s3StorageService.ifFileExists(fileName);

        assertTrue(exists);
    }

    @Test
    void fileExists_False() throws Exception {
        String fileName = "testFile.jpeg";
        when(amazonS3.doesObjectExist("testbucket", fileName)).thenReturn(false);

        boolean exists = s3StorageService.ifFileExists(fileName);

        assertFalse(exists);
    }
}
