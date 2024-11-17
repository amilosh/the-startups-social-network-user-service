package school.faang.user_service.controller.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.user.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = {UserController.class, UserService.class})
public class UserControllerTest {
    private static final String GET_USER_URL = "/users/{userId}";
    private static final String GET_USERS_URL = "/users";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void testGetUserByIdSuccess() throws Exception {
        UserDto user = createUserDto();
        when(userService.getUser(user.getId())).thenReturn(user);

        mockMvc.perform(get(GET_USER_URL, user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(notNullValue())))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("johndoe")));
    }

    @Test
    public void testGetUsersFail() throws Exception {
        when(userService.getUsers(anyList())).thenReturn(new ArrayList<>());

        mockMvc.perform(get(GET_USERS_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetUsersSuccess() throws Exception {
        UserDto firstUser = createUserDto();
        UserDto secondUser = createUserDto();
        secondUser.setId(2L);
        secondUser.setUsername("tomcat");
        when(userService.getUsers(List.of(1L, 2L))).thenReturn(List.of(firstUser, secondUser));

        mockMvc.perform(get(GET_USERS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(OBJECT_MAPPER.writeValueAsString(List.of(firstUser.getId(), secondUser.getId()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].username", is("johndoe")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].username", is("tomcat")));
    }

    private UserDto createUserDto() {
        return UserDto.builder()
                .id(1L)
                .username("johndoe")
                .email("johndoe@example.com")
                .userProfilePicFileId("https://amazon.com/s3/profilepic.jpg")
                .premiumId(12L).build();
    }
}
