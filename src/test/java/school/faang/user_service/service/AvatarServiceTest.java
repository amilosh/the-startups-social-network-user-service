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
import school.faang.user_service.service.storage.StorageService;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AvatarServiceTest {

    @Mock
    private StorageService storageService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AvatarService avatarService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void uploadUserAvatar_Success() throws Exception {
        System.out.println("Supported ImageIO formats: " + Arrays.toString(ImageIO.getReaderFormatNames()));

        Long userId = 1L;
        MultipartFile mockMultipartFile = mock(MultipartFile.class);
        User mockUser = User.builder()
                .id(userId)
                .build();

        when(userService.findUserById(userId)).thenReturn(mockUser);
        when(mockMultipartFile.getSize()).thenReturn(1024L);

        BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = image.createGraphics();
        graphics2D.setColor(Color.BLUE);
        graphics2D.fillRect(0, 0, 10, 10);
        graphics2D.dispose();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpeg", outputStream);
        byte[] imageByteArray = outputStream.toByteArray();

        System.out.println("Image byte array length: " + imageByteArray.length);

        when(mockMultipartFile.getInputStream()).thenAnswer(invocation -> new ByteArrayInputStream(imageByteArray));

        avatarService.uploadUserAvatar(userId, mockMultipartFile);

        verify(storageService, times(2)).uploadFile(anyString(), any(byte[].class), anyString());
        verify(userService).saveUser(mockUser);
        assertNotNull(mockUser.getUserProfilePic());
    }

    @Test
    void uploadUserAvatar_FileSizeExceeded() throws Exception {
        Long userId = 1L;
        MultipartFile mockFile = mock(MultipartFile.class);

        when(mockFile.getSize()).thenReturn(6 * 1024 * 1024L);

        Exception exception = assertThrows(FileSizeExceededException.class, () -> {
            avatarService.uploadUserAvatar(userId, mockFile);
        });

        assertEquals("File size should not exceed 5 Mb", exception.getMessage());
    }

    @Test
    void deleteUserAvatar_Success() throws Exception {
        Long userId = 1L;

        UserProfilePic mockAvatar = new UserProfilePic();
        mockAvatar.setFileId("largeFile.jpeg");
        mockAvatar.setSmallFileId("smallFile.jpeg");

        User mockUser = User.builder()
                .id(userId)
                .userProfilePic(mockAvatar)
                .build();

        when(userService.findUserById(userId)).thenReturn(mockUser);

        avatarService.deleteUserAvatar(userId);

        verify(storageService).deleteFile("largeFile.jpeg");
        verify(storageService).deleteFile("smallFile.jpeg");
        verify(userService).saveUser(mockUser);
        assertNull(mockUser.getUserProfilePic());
    }

    @Test
    void deleteUserAvatar_AvatarNotFound() throws Exception {
        Long userId = 1L;

        User mockUser = User.builder()
                .id(userId)
                .build();

        when(userService.findUserById(userId)).thenReturn(mockUser);

        Exception exception = assertThrows(AvatarNotFoundException.class, () -> {
            avatarService.deleteUserAvatar(userId);
        });

        assertEquals("User does not have an avatar to delete", exception.getMessage());
    }
}
