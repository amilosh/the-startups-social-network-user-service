package school.faang.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;
import school.faang.user_service.dto.ProcessResultDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.UserService;
import school.faang.user_service.validator.UserValidator;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapper userMapper;
    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private UserController userController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private String csvContent;
    private MockMultipartFile file;

    @BeforeEach
    void setUp() throws IOException {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
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
    void getUsersByIdsShouldReturnUserDtosWhenUsersExist() throws Exception {
        List<UserDto> userDtos = Arrays.asList(
                UserDto.builder().id(1L).username("John Doe").build(),
                UserDto.builder().id(2L).username("Jane Doe").build()
        );

        when(userService.getUsersByIds(Arrays.asList(1L, 2L))).thenReturn(userDtos);

        mockMvc.perform(get("/users/ids")
                        .param("ids", "1", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].username").value("John Doe"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].username").value("Jane Doe"));

        verify(userService, times(1)).getUsersByIds(Arrays.asList(1L, 2L));
        verifyNoMoreInteractions(userService);
    }

    @Test
    void getUsersByIdsShouldReturnEmptyListWhenNoUsersExist() throws Exception {
        when(userService.getUsersByIds(Arrays.asList(3L, 4L))).thenReturn(List.of());

        mockMvc.perform(get("/users/ids")
                        .param("ids", "3", "4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(userService, times(1)).getUsersByIds(Arrays.asList(3L, 4L));
        verifyNoMoreInteractions(userService);
    }
}