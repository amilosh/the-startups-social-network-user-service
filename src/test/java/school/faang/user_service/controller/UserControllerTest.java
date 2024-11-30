package school.faang.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.request.UsersDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.UserService;
import school.faang.user_service.validator.UserValidator;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
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
}
