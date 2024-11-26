package school.faang.user_service.controller.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.user.UserValidator;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @InjectMocks
    private UserController userController;
    @Mock
    private UserService userService;
    @Mock
    private UserValidator userValidator;
    @Mock
    private UserMapper userMapper;
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
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
