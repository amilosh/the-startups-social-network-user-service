package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.service.AvatarService;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AvatarControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private AvatarController avatarController;

    @Mock
    private AvatarService avatarService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(avatarController).build();
    }

    @Test
    void uploadUserAvatar_Success() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "avatar",
                "avatar.jpeg",
                MediaType.IMAGE_JPEG_VALUE,
                new byte[]{1, 2, 3}
        );

        mockMvc.perform(multipart("/users/{userId}/avatar", 1L)
                        .file(mockFile))
                .andExpect(status().isCreated())
                .andExpect(content().string("Avatar uploaded successfully"));

        verify(avatarService).uploadUserAvatar(eq(1L), any(MultipartFile.class));
    }

    @Test
    void deleteUserAvatar_Success() throws Exception {
        mockMvc.perform(delete("/users/{userId}/avatar", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("Avatar deleted successfully"));

        verify(avatarService).deleteUserAvatar(1L);
    }
}
