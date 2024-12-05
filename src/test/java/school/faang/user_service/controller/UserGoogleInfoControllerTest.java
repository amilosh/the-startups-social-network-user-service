package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import school.faang.user_service.dto.UserGoogleInfoDto;
import school.faang.user_service.service.user.google.UserGoogleInfoService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserGoogleInfoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserGoogleInfoService userGoogleInfoService;

    @Mock
    private OAuth2AuthorizedClientService oAuth2AuthorizedClient;

    @Mock
    private OAuth2AuthorizedClient client;

    @Mock
    private OAuth2AccessToken accessToken;

    @InjectMocks
    private UserGoogleInfoController userGoogleInfoController;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userGoogleInfoController).build();
    }

    @Test
    public void testGetAccessToken() throws Exception {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("name");

        when(oAuth2AuthorizedClient.loadAuthorizedClient(any(), any())).thenReturn(client);
        when(client.getAccessToken()).thenReturn(accessToken);
        when(accessToken.getTokenValue()).thenReturn("accessToken");

        mockMvc.perform(get("/users/google"))
                .andExpect(status().isOk())
                .andExpect(content().string("accessToken"));
    }

    @Test
    public void testGetGoogleEmailsByUserIds() throws Exception {
        List<String> expectedResult = List.of("email1", "email2", "email3");
        List<Long> ids = List.of(1L, 2L, 3L);

        when(userGoogleInfoService.getAllGoogleEmailsByUserIds(ids)).thenReturn(expectedResult);

        mockMvc.perform(post("/users/google/emails")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(ids)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(3));
    }

    @Test
    public void testGetGoogleEmailOrDefaultByUserId() throws Exception {
        long userId = 1L;
        String email = "email";

        when(userGoogleInfoService.getGoogleEmailOrDefaultByUserId(userId)).thenReturn(email);

        mockMvc.perform(get("/users/google/emails/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("email"));
    }

    @Test
    public void testBindUserWithGoogleInfo() throws Exception {
        UserGoogleInfoDto userGoogleInfoDto = new UserGoogleInfoDto();
        userGoogleInfoDto.setId(1L);

        when(userGoogleInfoService.bindUserWithGoogleInfo()).thenReturn(userGoogleInfoDto);

        mockMvc.perform(post("/users/google"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }
}
