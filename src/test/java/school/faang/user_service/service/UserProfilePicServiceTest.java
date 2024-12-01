package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.User;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.mapper.UserProfilePicMapper;
import school.faang.user_service.util.ImageUtils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserProfilePicServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private S3Service s3Service;
    @Mock
    private UserProfilePicMapper userProfilePicMapper;
    @Mock
    private ImageUtils imageUtils;
    @Spy
    private UserProfilePicMapper mapper = Mappers.getMapper(UserProfilePicMapper.class);

    private User user;
    private MultipartFile multipartFile;
    private BufferedImage bufferedImage;


    @BeforeEach
    public void setUp() {
        user = User.builder()
                .id(1L)
                .build();
        user.addUserProfilePic("file1", "smallFile1");
        multipartFile = new MockMultipartFile("file", "test.jpg",
                "image/jpeg", new byte[5000]);
        bufferedImage = new BufferedImage(5000, 5000, BufferedImage.TYPE_INT_RGB);
    }

    @Test
    void testUpdateUserProfilePic() {
        when(userService.getUserById(any())).thenReturn(user);
        when(s3Service.uploadImage(any(), any(), any(), any())).thenReturn("key");
        when(userService.updateUser(user)).thenReturn(user);
        when(imageUtils.resizeImage(any(), anyInt())).thenReturn(bufferedImage);
        when(imageUtils.convertMultiPartFileToBufferedImage(multipartFile)).thenReturn(bufferedImage);

        userService.updateUserProfilePicture(user.getId(), multipartFile);

        verify(imageUtils, times(1)).convertMultiPartFileToBufferedImage(multipartFile);
        verify(imageUtils, times(2)).resizeImage(any(), anyInt());
        verify(userService).updateUser(user);
        verify(s3Service, times(2)).uploadImage(any(), any(), any(), any());
        verify(userService).getUserById(any());
    }

    @Test
    void testUpdateUserProfilePicWithFileNotImage() {
        when(userService.getUserById(any())).thenReturn(user);
        MultipartFile file = new MockMultipartFile("file", "test.jpg",
                "text/plain", new byte[10000]);

        assertThrows(DataValidationException.class, () ->
                userService.updateUserProfilePicture(user.getId(), file));

        verify(userService).getUserById(any());
    }

    @Test
    void testGetUserAvatar() {
        when(userService.getUserById(anyLong())).thenReturn(user);
        when(s3Service.getFile(any())).thenReturn(new InputStreamResource(new ByteArrayInputStream(new byte[0])));

        userService.getUserAvatar(1L);

        verify(userService).getUserById(anyLong());
        verify(s3Service).getFile(any());
    }

    @Test
    void testDeleteUserAvatarPic() {
        String fileId = user.getUserProfilePic().getFileId();
        String smallFileId = user.getUserProfilePic().getSmallFileId();
        when(userService.getUserById(anyLong())).thenReturn(user);
        when(userService.updateUser(user)).thenReturn(user);

        userService.deleteUserAvatar(user.getId());

        verify(userService).getUserById(anyLong());
        verify(userService).updateUser(user);
        verify(s3Service).deleteFiles(fileId, smallFileId);
        assertNull(user.getUserProfilePic());
    }
}
