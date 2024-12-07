package school.faang.user_service.service.user.google;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.UserGoogleInfoDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserGoogleInfo;
import school.faang.user_service.mapper.user.google.UserGoogleInfoMapper;
import school.faang.user_service.repository.UserGoogleInfoRepository;
import school.faang.user_service.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserGoogleInfoServiceTest {

    @Mock
    private UserGoogleInfoRepository userGoogleInfoRepository;

    @Mock
    private UserService userService;

    @Mock
    private UserGoogleInfoMapper userGoogleInfoMapper;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private UserGoogleInfoService userGoogleInfoService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private OAuth2AuthenticationToken authentication;

    @Mock
    private OAuth2User oAuth2User;

    User user;
    UserGoogleInfo userGoogleInfo;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);

        userGoogleInfo = new UserGoogleInfo();
    }

    @Test
    public void testBindUserWithGoogleInfo() {
        when(userContext.getUserId()).thenReturn(user.getId());
        when(userService.getUserById(1L)).thenReturn(user);

        Map<String, Object> attributes = new HashMap<>();

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttributes()).thenReturn(attributes);
        when(userGoogleInfoMapper.userAttributesToUserGoogleInfo(attributes)).thenReturn(userGoogleInfo);

        when(userGoogleInfoRepository.findBySub(any())).thenReturn(Optional.of(userGoogleInfo));
        when(userGoogleInfoRepository.save(any())).thenReturn(userGoogleInfo);
        when(userGoogleInfoMapper.toDto(userGoogleInfo)).thenReturn(new UserGoogleInfoDto());

        userGoogleInfoService.bindUserWithGoogleInfo();
    }

    @Test
    public void testGetGoogleEmailOrDefaultByUserId() {
        userGoogleInfo.setEmail("email");
        when(userService.getUserById(1L)).thenReturn(user);
        when(userGoogleInfoRepository.findByUser(user)).thenReturn(Optional.of(userGoogleInfo));

        String result = userGoogleInfoService.getGoogleEmailOrDefaultByUserId(user.getId());
        assertEquals("email", result);
    }

    @Test
    public void testGetAllGoogleEmailsByUserIds() {
        UserGoogleInfo firstUserGoogleInfo = new UserGoogleInfo();
        firstUserGoogleInfo.setId(1L);
        firstUserGoogleInfo.setEmail("email1");

        UserGoogleInfo secondUserGoogleInfo = new UserGoogleInfo();
        secondUserGoogleInfo.setId(2L);
        secondUserGoogleInfo.setEmail("email2");

        List<Long> userIds = List.of(1L, 2L);

        List<UserGoogleInfo> users = List.of(firstUserGoogleInfo, secondUserGoogleInfo);

        when(userGoogleInfoRepository.findAllByUserIds(any())).thenReturn(users);

        List<String> emails = userGoogleInfoService.getAllGoogleEmailsByUserIds(userIds);
        assertEquals(2, emails.size());
        assertEquals("email1", emails.get(0));
        assertEquals("email2", emails.get(1));
    }
}
