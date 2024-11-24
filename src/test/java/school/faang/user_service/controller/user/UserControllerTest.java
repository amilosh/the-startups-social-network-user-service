package school.faang.user_service.controller.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.service.user.UserService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testAddAvatar() throws Exception {
        long userId = 1L;
        MockMultipartFile file = new MockMultipartFile("file", "avatar.png", "image/png", "image content".getBytes());

        when(userService.addAvatar(userId, file)).thenReturn("Avatar uploaded successfully");

        mockMvc.perform(multipart("/api/v1/users/1/avatar")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().string("Avatar uploaded successfully"));

        verify(userService, times(1)).addAvatar(userId, file);
    }

    @Test
    public void testGetAvatar_WithExistingProfilePic() throws Exception {
        long userId = 1L;
        byte[] mockImageData = "imageData".getBytes();
        ResponseEntity<byte[]> responseEntity = ResponseEntity.ok(mockImageData);

        when(userService.getAvatar(userId)).thenReturn(responseEntity);

        mockMvc.perform(get("/api/v1/users/1/avatar"))
                .andExpect(status().isOk())
                .andExpect(content().bytes(mockImageData));

        verify(userService, times(1)).getAvatar(userId);
    }

    @Test
    public void testGetAvatar_WithNoProfilePic() throws Exception {
        long userId = 1L;
        byte[] defaultImageData = "defaultImageData".getBytes();
        ResponseEntity<byte[]> responseEntity = ResponseEntity.ok(defaultImageData);

        when(userService.getAvatar(userId)).thenReturn(responseEntity);

        mockMvc.perform(get("/api/v1/users/1/avatar"))
                .andExpect(status().isOk())
                .andExpect(content().bytes(defaultImageData));

        verify(userService, times(1)).getAvatar(userId);
    }
}
