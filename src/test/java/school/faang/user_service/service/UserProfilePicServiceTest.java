package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.repository.UserRepository;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

public class UserProfilePicServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private S3Service s3Service;

    @InjectMocks
    private UserProfilePicService userProfilePicService;

    public UserProfilePicServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUploadUserProfilePic() throws IOException {
        Long userId = 1L;
        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "test data".getBytes());

        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(s3Service.saveResizedImages(any(), anyString())).thenReturn(new String[]{"key1", "key2"});

        User result = userProfilePicService.uploadUserProfilePic(userId, file);

        assertEquals("key1", result.getUserProfilePic().getFileId());
        assertEquals("key2", result.getUserProfilePic().getSmallFileId());
        verify(userRepository, times(1)).findById(userId);
        verify(s3Service, times(1)).saveResizedImages(file, "user-profile-pics");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testDeleteUserProfilePic() throws IOException {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId("fileId");
        user.setUserProfilePic(userProfilePic);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userProfilePicService.deleteUserProfilePic(userId);

        assertNull(user.getUserProfilePic());
        verify(s3Service, times(1)).deleteFile("user-profile-pics", "fileId");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testGetUserProfilePic() throws IOException {
        Long userId = 1L;
        InputStream mockInputStream = new ByteArrayInputStream("test data".getBytes());

        User user = new User();
        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId("fileId");
        user.setUserProfilePic(userProfilePic);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(s3Service.downloadFile("user-profile-pics", "fileId")).thenReturn(mockInputStream);

        InputStream result = userProfilePicService.getUserProfilePic(userId);

        assertNotNull(result);
        assertArrayEquals("test data".getBytes(), result.readAllBytes());
        verify(s3Service, times(1)).downloadFile("user-profile-pics", "fileId");
    }
}
