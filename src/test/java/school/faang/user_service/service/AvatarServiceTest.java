package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.AvatarNotFoundException;
import school.faang.user_service.exception.FileSizeExceededException;
import school.faang.user_service.exception.InvalidFileFormatException;
import school.faang.user_service.service.storage.StorageService;
import school.faang.user_service.validator.AvatarValidator;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AvatarServiceTest {

    @Mock
    private StorageService storageService;

    @Mock
    private UserService userService;

    @Mock
    private AvatarValidator avatarValidator;

    @InjectMocks
    private AvatarService avatarService;

    private Long currentUserId;
    private Long userId;
    private User mockUser;
    private String largeFileName;
    private String smallFileName;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userId = 1L;
        currentUserId = 1L;

        mockUser = User.builder()
                .id(userId)
                .build();

        largeFileName = "avatars/avatar_userId_" + userId + "_large.jpeg";
        smallFileName = "avatars/avatar_userId_" + userId + "_small.jpeg";
    }

    @Test
    void uploadUserAvatar_Success() throws Exception {
        MultipartFile mockMultipartFile = mock(MultipartFile.class);

        when(userService.findUserById(userId)).thenReturn(mockUser);
        doNothing().when(avatarValidator).validateUserAuthorization(currentUserId, userId);
        doNothing().when(avatarValidator).validateAvatarFile(mockMultipartFile);

        when(mockMultipartFile.getContentType()).thenReturn("image/jpeg");

        BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = image.createGraphics();
        graphics2D.setColor(Color.BLUE);
        graphics2D.fillRect(0, 0, 10, 10);
        graphics2D.dispose();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpeg", outputStream);
        byte[] imageByteArray = outputStream.toByteArray();

        when(mockMultipartFile.getInputStream()).thenAnswer(invocation -> new ByteArrayInputStream(imageByteArray));

        avatarService.uploadUserAvatar(userId, currentUserId, mockMultipartFile);

        verify(storageService, times(1)).uploadFile(eq(largeFileName), any(byte[].class), eq("image/jpeg"));
        verify(storageService, times(1)).uploadFile(eq(smallFileName), any(byte[].class), eq("image/jpeg"));
        verify(userService).saveUser(mockUser);
        assertNotNull(mockUser.getUserProfilePic());
        assertEquals(largeFileName, mockUser.getUserProfilePic().getFileId());
        assertEquals(smallFileName, mockUser.getUserProfilePic().getSmallFileId());
    }

    @Test
    void uploadUserAvatar_FileSizeExceeded() {
        MultipartFile mockFile = mock(MultipartFile.class);

        when(mockFile.getSize()).thenReturn(6 * 1024 * 1024L);

        doThrow(new FileSizeExceededException("File size should not exceed 5 MB"))
                .when(avatarValidator).validateAvatarFile(mockFile);

        Exception exception = assertThrows(FileSizeExceededException.class, () -> {
            avatarService.uploadUserAvatar(userId, currentUserId, mockFile);
        });

        assertEquals("File size should not exceed 5 MB", exception.getMessage());
    }

    @Test
    void deleteUserAvatar_Success() {
        UserProfilePic mockAvatar = new UserProfilePic();
        mockAvatar.setFileId(largeFileName);
        mockAvatar.setSmallFileId(smallFileName);

        mockUser.setUserProfilePic(mockAvatar);

        when(userService.findUserById(userId)).thenReturn(mockUser);
        doNothing().when(avatarValidator).validateUserAuthorization(currentUserId, userId);

        avatarService.deleteUserAvatar(userId, currentUserId);

        verify(storageService).deleteFile(largeFileName);
        verify(storageService).deleteFile(smallFileName);
        verify(userService).saveUser(mockUser);
        assertNull(mockUser.getUserProfilePic());
    }

    @Test
    void deleteUserAvatar_AvatarNotFound() {
        when(userService.findUserById(userId)).thenReturn(mockUser);
        doNothing().when(avatarValidator).validateUserAuthorization(currentUserId, userId);

        Exception exception = assertThrows(AvatarNotFoundException.class, () -> {
            avatarService.deleteUserAvatar(userId, currentUserId);
        });

        assertEquals("User with ID " + userId + " does not have an avatar to delete", exception.getMessage());
    }

    @Test
    void uploadUserAvatar_InvalidFileFormat() {
        MultipartFile mockFile = mock(MultipartFile.class);

        doThrow(new InvalidFileFormatException("Uploaded file has invalid type"))
                .when(avatarValidator).validateAvatarFile(mockFile);

        Exception exception = assertThrows(InvalidFileFormatException.class, () -> {
            avatarService.uploadUserAvatar(userId, currentUserId, mockFile);
        });

        assertEquals("Uploaded file has invalid type", exception.getMessage());
    }
}
