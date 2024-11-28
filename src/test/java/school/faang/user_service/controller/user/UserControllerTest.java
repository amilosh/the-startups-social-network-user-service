package school.faang.user_service.controller.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.service.user.UserService;

import static org.junit.Assert.assertArrayEquals;
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

    @Mock
    private UserContext userContext;

    private MockMvc mockMvc;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testAddAvatar() throws Exception {
        MockMultipartFile avatarFile = new MockMultipartFile(
                "file",
                "avatar.svg",
                "image/svg+xml",
                "test-avatar-data".getBytes()
        );
        when(userContext.getUserId()).thenReturn(1L);
        mockMvc.perform(multipart("/api/v1/users/avatar")
                        .file(avatarFile))
                .andExpect(status().isOk());

        verify(userService, times(1)).addAvatar(1L, avatarFile);
    }

    @Test
    public void testGetAvatar() throws Exception {
        byte[] avatarBytes = "image".getBytes();
        String contentType = "image/svg+xml";

        long userId = 1L;
        when(userService.getAvatar(userId)).thenReturn(avatarBytes);
        when(userContext.getUserId()).thenReturn(1L);

        MvcResult result = mockMvc.perform(get("/api/v1/users/avatar")
                        .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andReturn();

        byte[] actualAvatar = result.getResponse().getContentAsByteArray();
        assertArrayEquals(avatarBytes, actualAvatar);
        verify(userService, times(1)).getAvatar(userId);
    }

    @Test
    public void testDeletePost() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file",
                "file.csv",
                "text/plain", "hello".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/users/upload-file").file(file))
                .andExpect(status().isOk());
    }
}
