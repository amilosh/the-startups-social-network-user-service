package school.faang.user_service.storage;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.service.storage.S3StorageService;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class S3StorageServiceTest {

    @Mock
    private AmazonS3 amazonS3;

    @InjectMocks
    private S3StorageService s3StorageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(s3StorageService, "storageType", "aws");
        ReflectionTestUtils.setField(s3StorageService, "awsBucketName", "testbucket");
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
    void downloadFile_Success() throws Exception {
        String fileName = "testFile.jpeg";
        S3Object s3Object = new S3Object();
        s3Object.setObjectContent(new S3ObjectInputStream(
                new ByteArrayInputStream(new byte[]{1, 2, 3}), null)
        );

        when(amazonS3.getObject("testbucket", fileName)).thenReturn(s3Object);

        Resource resource = s3StorageService.downloadFile(fileName);

        assertNotNull(resource);
    }

    @Test
    void fileExists_True() throws Exception {
        String fileName = "testFile.jpeg";
        when(amazonS3.doesObjectExist("testbucket", fileName)).thenReturn(true);

        boolean exists = s3StorageService.fileExists(fileName);

        assertTrue(exists);
    }

    @Test
    void fileExists_False() throws Exception {
        String fileName = "testFile.jpeg";
        when(amazonS3.doesObjectExist("testbucket", fileName)).thenReturn(false);

        boolean exists = s3StorageService.fileExists(fileName);

        assertFalse(exists);
    }
}
