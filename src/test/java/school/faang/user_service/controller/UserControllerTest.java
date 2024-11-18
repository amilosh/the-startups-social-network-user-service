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
import school.faang.user_service.dto.request.UsersDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.UserService;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Spy
    private UserMapper userMapper;

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
        User user = new User();
        user.setId(userId);

        when(userService.findUserById(userId)).thenReturn(user);

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userService, times(1)).findUserById(userId);
    }

    @Test
    void getUsersByIdsWhenUsersExistShouldReturnUserDtos() throws Exception {
        UsersDto ids = new UsersDto();
        ids.setIds(List.of(1L, 2L));

        UserDto userDto1 = new UserDto();
        userDto1.setId(1L);

        UserDto userDto2 = new UserDto();
        userDto2.setId(2L);

        when(userService.getUsersByIds(ids)).thenReturn(List.of(userDto1, userDto2));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));
    }

    @Test
    void getUsersByIdsWhenNoUsersExistShouldReturnEmptyList() throws Exception {
        UsersDto ids = new UsersDto();
        ids.setIds(List.of(1L, 2L));

        when(userService.getUsersByIds(ids)).thenReturn(List.of());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(0)));
    }
}