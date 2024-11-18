package school.faang.user_service.service.avatar;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DiceBearException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ErrorMessage;
import school.faang.user_service.exception.MinioException;
import school.faang.user_service.properties.DiceBearProperties;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.minio.MinioService;
import school.faang.user_service.service.user.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.exception.ErrorMessage.AVATAR_PROCESS_ERROR;

@ExtendWith(MockitoExtension.class)
class AvatarServiceTest {
    private static final Long USER_ID = 666L;

    @InjectMocks
    private AvatarService avatarService;

    @Mock
    private WebClient webClient;

    @Mock
    private MinioService minioService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    private MockWebServer mockWebServer;
    private DiceBearProperties diceBearProperties;

    @BeforeEach
    void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        diceBearProperties = new DiceBearProperties();
        avatarService = new AvatarService(webClient, minioService, diceBearProperties, userRepository, userService);
    }

    @AfterEach
    void shutdownMockServer() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    void getDiceBearAvatarSuccessTest() {
        byte[] avatarData = "random_avatar".getBytes();
        mockWebServer.enqueue(new MockResponse()
                .setBody(new String(avatarData))
                .addHeader("Content-Type", "image/jpeg")
                .setResponseCode(200));

        Optional<byte[]> result = avatarService.getRandomDiceBearAvatar(USER_ID);

        assertTrue(result.isPresent());
        assertEquals(avatarData.length, result.get().length);
    }

    @Test
    void getDiceBearAvatarEmptyContentTest() {
        mockWebServer.enqueue(new MockResponse()
                .setBody("")
                .addHeader("Content-Type", "image/jpeg")
                .setResponseCode(200));

        DiceBearException exception = assertThrows(DiceBearException.class, () -> {
            avatarService.getRandomDiceBearAvatar(USER_ID).orElseThrow(() ->
                    new DiceBearException(ErrorMessage.DICE_BEAR_EMPTY_CONTENT));
        });

        assertEquals(ErrorMessage.DICE_BEAR_EMPTY_CONTENT, exception.getMessage());
    }

    @Test
    void getDiceBearAvatarServerErrorTest() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(400));

        DiceBearException exception = assertThrows(DiceBearException.class, () -> {
            avatarService.getRandomDiceBearAvatar(USER_ID).orElseThrow(() ->
                    new DiceBearException(ErrorMessage.DICE_BEAR_RETRIEVAL_ERROR));
        });

        assertTrue(exception.getMessage().contains("Error retrieving avatar"));
    }

    @Test
    void getDiceBearAvatarServiceUnavailableTest() throws Exception {
        mockWebServer.shutdown();

        DiceBearException exception = assertThrows(DiceBearException.class, () -> {
            avatarService.getRandomDiceBearAvatar(USER_ID);
        });

        assertEquals(ErrorMessage.DICE_BEAR_UNEXPECTED_ERROR, exception.getMessage());
    }

    @Test
    void getUserAvatarSuccessTest()  {
        byte[] avatarData = "user_avatar_data".getBytes();
        UserProfilePic userProfilePic = new UserProfilePic("fileId", "smallFileId");

        when(userRepository.findUserProfilePicByUserId(USER_ID)).thenReturn(Optional.of(userProfilePic));
        when(minioService.downloadFile("fileId")).thenReturn(avatarData);

        byte[] result = avatarService.getUserAvatar(USER_ID);

        assertNotNull(result);
        assertEquals(avatarData.length, result.length);
    }

    @Test
    void getUserAvatarNotFoundTest() {
        when(userRepository.findUserProfilePicByUserId(USER_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            avatarService.getUserAvatar(USER_ID);
        });

        assertEquals("Avatar not found for user ID: " + USER_ID, exception.getMessage());
    }

    @Test
    void uploadUserAvatarSuccessTest() throws IOException {
        Path imagePath = Paths.get("src/test/resources/files/test-image.jpeg");
        byte[] fileData = Files.readAllBytes(imagePath);

        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getBytes()).thenReturn(fileData);
        when(mockFile.getContentType()).thenReturn("image/jpeg");

        User user = new User();
        user.setUserProfilePic(new UserProfilePic(null, null));
        when(userService.getUserEntity(USER_ID)).thenReturn(user);

        UserProfilePic result = avatarService.uploadUserAvatar(USER_ID, mockFile, false);

        assertNotNull(result);
        verify(minioService, times(2)).uploadFile(anyLong(), anyString(), any(byte[].class), eq("image/jpeg"));
        verify(userRepository).updateProfilePic(eq(USER_ID), anyString(), anyString());
    }

    @Test
    void uploadUserAvatarCheckExistsTest() {
        User user = new User();
        user.setUserProfilePic(new UserProfilePic("fileId", "smallFileId"));
        when(userService.getUserEntity(USER_ID)).thenReturn(user);

        MinioException exception = assertThrows(MinioException.class, () -> {
            avatarService.uploadUserAvatar(USER_ID, null, false);
        });

        assertTrue(exception.getMessage().contains("Avatar is already uploaded for user ID"));
    }

    @Test
    void deleteUserAvatarSuccessTest() {
        UserProfilePic userProfilePic = new UserProfilePic("fileId", "smallFileId");
        when(userRepository.findUserProfilePicByUserId(USER_ID)).thenReturn(Optional.of(userProfilePic));

        avatarService.deleteUserAvatar(USER_ID);

        verify(minioService).deleteFile("fileId");
        verify(minioService).deleteFile("smallFileId");
        verify(userRepository).deleteProfilePic(USER_ID);
    }

    @Test
    void deleteUserAvatarNotFoundTest() {
        when(userRepository.findUserProfilePicByUserId(USER_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            avatarService.deleteUserAvatar(USER_ID);
        });

        assertEquals("Avatar not found for user ID: " + USER_ID, exception.getMessage());
    }

    @Test
    void uploadUserAvatarResizeImageExceptionTest() throws IOException {
        Path imagePath = Paths.get("src/test/resources/files/test-image.jpeg");
        byte[] fileData = Files.readAllBytes(imagePath);

        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getBytes()).thenReturn(fileData);
        when(mockFile.getContentType()).thenReturn("image/jpeg");

        AvatarService spyService = spy(avatarService);
        doThrow(new IOException()).when(spyService).resizeImage(any(byte[].class), anyInt());

        User user = new User();
        user.setUserProfilePic(new UserProfilePic(null, null));
        when(userService.getUserEntity(USER_ID)).thenReturn(user);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            spyService.uploadUserAvatar(USER_ID, mockFile, false);
        });

        assertEquals(AVATAR_PROCESS_ERROR, exception.getMessage());
    }

    @Test
    void uploadUserAvatarWithNoFileTest() throws IOException {
        AvatarService spyAvatarService = spy(avatarService);
        Path imagePath = Paths.get("src/test/resources/files/test-image.jpeg");
        byte[] fileData = Files.readAllBytes(imagePath);
        doReturn(Optional.of(fileData)).when(spyAvatarService).getRandomDiceBearAvatar(USER_ID);

        User user = new User();
        user.setId(USER_ID);
        user.setUserProfilePic(new UserProfilePic(null, null));
        when(userService.getUserEntity(USER_ID)).thenReturn(user);

        UserProfilePic result = spyAvatarService.uploadUserAvatar(USER_ID, null, true);

        assertNotNull(result);
        verify(minioService, times(2)).uploadFile(anyLong(), anyString(), any(byte[].class), eq("image/jpeg"));
        verify(userRepository).updateProfilePic(eq(USER_ID), anyString(), anyString());
    }

    @Test
    void uploadUserAvatarIOExceptionWhileReadingFileTest() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getBytes()).thenThrow(new IOException());

        User user = new User();
        user.setUserProfilePic(new UserProfilePic(null, null));
        when(userService.getUserEntity(USER_ID)).thenReturn(user);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            avatarService.uploadUserAvatar(USER_ID, mockFile, false);
        });

        assertTrue(exception.getMessage().contains("Failed to read the uploaded file"));
    }
}
