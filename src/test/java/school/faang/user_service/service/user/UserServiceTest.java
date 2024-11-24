package school.faang.user_service.service.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.mentorship.MentorshipService;
import school.faang.user_service.service.s3.S3Service;
import school.faang.user_service.utils.AvatarLibrary;
import school.faang.user_service.validator.user.UserValidator;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private MentorshipService mentorshipService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserValidator userValidator;

    @Mock
    private S3Service s3Service;

    @Mock
    private AvatarLibrary avatarLibrary;

    private final long userId = 1L;
    private User user;

    @BeforeEach
    public void initUser() {
        user = new User();
        user.setId(userId);
    }

    @Test
    void testDeactivateUser_Success() {
        user.setActive(true);

        when(userValidator.validateUser(userId)).thenReturn(user);

        userService.deactivateUser(userId);

        verify(goalRepository, times(1)).findGoalsByUserId(userId);
        verify(eventRepository, times(1)).findAllByUserId(userId);
        verify(mentorshipService, times(1)).stopMentorship(user);
        verify(userRepository, times(1)).save(user);

        assertFalse(user.isActive(), "User should be deactivated");
    }

    @Test
    public void testGetUserSuccess() {
        UserDto userDto = new UserDto();
        userDto.setId(userId);

        when(userValidator.validateUser(userId)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.getUser(userId);

        assertEquals(userDto, result);
        verify(userValidator, times(1)).validateUser(userId);
        verify(userMapper, times(1)).toDto(user);
    }

    @Test
    public void testGetUsersByIdsSuccess() {
        List<Long> ids = List.of(1L, 2L);
        List<User> users = List.of(new User(), new User());
        List<UserDto> usersDtos = List.of(new UserDto(), new UserDto());

        when(userRepository.findAllById(ids)).thenReturn(users);
        when(userMapper.toListDto(users)).thenReturn(usersDtos);

        List<UserDto> result = userService.getUsersByIds(ids);

        assertEquals(usersDtos, result);
        verify(userRepository, times(1)).findAllById(ids);
        verify(userMapper, times(1)).toListDto(users);
    }

    @Test
    public void testAddAvatar() {
        byte[] content = new byte[1024];
        MockMultipartFile file = new MockMultipartFile("file",content);
        user.setUserProfilePic(new UserProfilePic());
        user.getUserProfilePic().setFileId("123");

        when(userValidator.validateUser(userId)).thenReturn(user);

        String fileId = userService.addAvatar(userId, file);

        assertEquals("123", fileId);
        verify(s3Service, times(1)).uploadFile(file, user);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testGetAvatarWhenUserPicProfileIsNotNull() {
        UserProfilePic profilePic = new UserProfilePic();
        user.setUserProfilePic(profilePic);
        profilePic.setFileId("123");

        user.setUserProfilePic(profilePic);

        when(userValidator.validateUser(userId))
                .thenReturn(user);
        when(s3Service.getFile(user.getUserProfilePic().getFileId()))
                .thenReturn(new ByteArrayInputStream("imageData".getBytes()));

        when(avatarLibrary.getPictureFromResponse(any(byte[].class)))
                .thenReturn(new ResponseEntity<>("imageData".getBytes(), new HttpHeaders(), HttpStatus.OK));

        ResponseEntity<byte[]> response = userService.getAvatar(userId);

        Assertions.assertNotNull(response);
        Assertions.assertArrayEquals("imageData".getBytes(), response.getBody());
        verify(avatarLibrary, times(1)).getPictureFromResponse(any(byte[].class));
    }
}