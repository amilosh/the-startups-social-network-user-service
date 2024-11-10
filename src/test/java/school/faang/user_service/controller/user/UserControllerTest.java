package school.faang.user_service.controller.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.user.UserService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void testGetNotPremiumUsers() throws Exception {
        UserDto userDto1 = UserDto.builder()
                .id(1L)
                .username("Charlie")
                .build();

        UserDto userDto2 = UserDto.builder()
                .id(2L)
                .username("Dana")
                .build();

        UserFilterDto filters = new UserFilterDto();
        List<UserDto> notPremiumUsers = Arrays.asList(userDto1, userDto2);
        String filterJson = objectMapper.writeValueAsString(filters);

        when(userService.getNotPremiumUsers(any(UserFilterDto.class))).thenReturn(notPremiumUsers);

        mockMvc.perform(post("/users/not-premium")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filterJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].username").value("Charlie"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].username").value("Dana"));
    }

    @Test
    void testGetPremiumUsers() throws Exception {
        UserDto userDto1 = UserDto.builder()
                .id(1L).username("Charlie")
                .build();

        UserDto userDto2 = UserDto.builder()
                .id(2L)
                .username("Dana")
                .build();

        List<UserDto> premiumUsers = Arrays.asList(userDto1, userDto2);
        UserFilterDto filters = new UserFilterDto();
        String filterJson = objectMapper.writeValueAsString(filters);

        when(userService.getPremiumUsers(any(UserFilterDto.class))).thenReturn(premiumUsers);

        mockMvc.perform(post("/users/premium")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filterJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].username").value("Charlie"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].username").value("Dana"));
    }
}