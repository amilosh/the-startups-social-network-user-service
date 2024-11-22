package school.faang.user_service.service.user;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvatarServiceTest {
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private AmazonS3 s3Client;
    @InjectMocks
    private AvatarService avatarService;
    @Value("${minio.avatarBucket}")
    private String bucketName = "test-bucket";
    @Value("${dicebear.api.url}")
    private String dicebearApiUrl = "http://localhost/dicebear";

    @BeforeEach
    public void setUp() {
        avatarService.setBucketName(bucketName);
        avatarService.setDicebearApiUrl(dicebearApiUrl);
    }

    @Test
    void testGenerateRandomAvatarGenerated() throws MalformedURLException {
        String seed = "test-seed";
        String filename = "avatar.svg";
        String avatarSvg = "<svg>...</svg>";
        when(restTemplate.getForObject(any(String.class), eq(String.class))).thenReturn(avatarSvg);
        when(s3Client.doesBucketExistV2(bucketName)).thenReturn(true);
        when(s3Client.getUrl(bucketName, filename)).thenReturn(new java.net.URL("http://localhost/"
                + bucketName + "/" + filename));
        String result = avatarService.generateRandomAvatar(seed, filename);
        assertEquals("http://localhost/" + bucketName + "/" + filename, result);
        verify(s3Client).putObject(any(PutObjectRequest.class));
    }

    @Test
    void testGenerateRandomAvatarWithAvatarNotGenerated() {
        String seed = "test-seed";
        String filename = "avatar.svg";
        when(restTemplate.getForObject(any(String.class), eq(String.class))).thenReturn(null);
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                avatarService.generateRandomAvatar(seed, filename));
        assertEquals("Could not generate an avatar", exception.getMessage());
    }

    @Test
    void testSaveAvatarSaved() throws Exception {
        String svg = "<svg>...</svg>";
        String filename = "avatar.svg";
        when(s3Client.doesBucketExistV2(bucketName)).thenReturn(true);
        when(s3Client.getUrl(bucketName, filename))
                .thenReturn(new java.net.URL("http://localhost/" + bucketName + "/" + filename));
        String result = avatarService.saveAvatar(svg, filename);
        assertEquals("http://localhost/" + bucketName + "/" + filename, result);
        verify(s3Client).putObject(argThat(request ->
                request.getKey().equals(filename) &&
                        request.getBucketName().equals(bucketName) &&
                        request.getCannedAcl().equals(CannedAccessControlList.PublicRead)));
    }

    @Test
    void testSaveAvatarWithFailedToSave() {
        String svg = "<svg>...</svg>";
        String filename = "avatar.svg";
        doThrow(new RuntimeException("S3 error")).when(s3Client).putObject(any(PutObjectRequest.class));
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                avatarService.saveAvatar(svg, filename));
        assertEquals("Failed to save an avatar to minio", exception.getMessage());
    }
}