package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;
import school.faang.user_service.dto.ProcessResultDto;
import school.faang.user_service.dto.UserContactsDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exception.GlobalExceptionHandler;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.UserService;
import school.faang.user_service.validator.UserValidator;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = {UserController.class})
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserController userController;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Spy
    private UserMapper userMapper;
    @MockBean
    private UserValidator userValidator;

    private String csvContent;
    private MockMultipartFile file;

    @BeforeEach
    void setUp() throws IOException {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        String testCsv = IOUtils.toString(ClassLoader.getSystemClassLoader()
                .getSystemResourceAsStream("students2.csv"));
        file = new MockMultipartFile("file", "test.csv", "text/csv", testCsv.getBytes());
    }

    @Test
    void getUserWhenUserExistsShouldReturnUser() throws Exception {
        long userId = 1L;
        UserDto dto = new UserDto();
        dto.setId(userId);

        when(userService.findUserDtoById(userId)).thenReturn(dto);

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userService, times(1)).findUserDtoById(userId);
    }

    @Test
    void testGetAllUsers_NoFilter() throws Exception {
        UserDto userDto1 = UserDto.builder()
                .id(1L)
                .username("JohnDoe")
                .build();

        UserDto userDto2 = UserDto.builder()
                .id(2L)
                .username("JaneSmith")
                .build();

        when(userService.getAllUsers(any(UserFilterDto.class))).thenReturn(Arrays.asList(userDto1, userDto2));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("JohnDoe"))
                .andExpect(jsonPath("$[1].username").value("JaneSmith"));

        verify(userService).getAllUsers(any(UserFilterDto.class));
    }

    @Test
    void testGetPremiumUsers_NoFilter() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .username("PremiumUser")
                .build();

        when(userService.getPremiumUsers(any(UserFilterDto.class))).thenReturn(List.of(userDto));

        mockMvc.perform(get("/users/premium"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].username").value("PremiumUser"));

        verify(userService).getPremiumUsers(any(UserFilterDto.class));
    }

    @Test
    void testGetAllUsers_WithFilter() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .username("JohnDoe")
                .build();

        when(userService.getAllUsers(any(UserFilterDto.class))).thenReturn(List.of(userDto));

        mockMvc.perform(get("/users")
                        .param("namePattern", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].username").value("JohnDoe"));

        verify(userService).getAllUsers(any(UserFilterDto.class));
    }

    @Test
    void testGetPremiumUsers_WithFilter() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .username("PremiumJohn")
                .build();

        when(userService.getPremiumUsers(any(UserFilterDto.class))).thenReturn(List.of(userDto));

        mockMvc.perform(get("/users/premium")
                        .param("namePattern", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].username").value("PremiumJohn"));

        verify(userService).getPremiumUsers(any(UserFilterDto.class));
    }

    @Test
    void testUploadToCsvSuccess() throws Exception {
        ProcessResultDto mockResult = new ProcessResultDto(1, List.of());
        when(userService.importUsersFromCsv(any(InputStream.class))).thenReturn(mockResult);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/users/upload")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.сountSuccessfullySavedUsers").value(1))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    @DisplayName("Get user contacts success")
    void testGetUserContactsSuccess() throws Exception {
        Long userId = 1L;
        UserContactsDto dto = UserContactsDto.builder()
                .id(1L)
                .email("email")
                .phone("phone")
                .build();
        when(userService.getUserContacts(userId)).thenReturn(dto);
        mockMvc.perform(get("/users/{userId}/contacts", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("email"))
                .andExpect(jsonPath("$.phone").value("phone"));
    }

    @Test
    @DisplayName("Get user contacts fail: Negative project id")
    void testGetUserContacts_NegativeProjectId_Fail() throws Exception {
        Long userId = -1L;

        mockMvc.perform(get("/users/{userId}/contacts", userId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("User id should be a positive integer")));
    }

    @Test
    @DisplayName("Get user contacts fail: null project id")
    void testGetUserContacts_nullProjectId_Fail() throws Exception {
        Long userId = null;

        mockMvc.perform(get("/users/{userId}/contacts", userId))
                .andExpect(status().isNotFound());
    }
}