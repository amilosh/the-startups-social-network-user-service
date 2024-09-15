package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.user.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapperImpl userMapper;

    @Mock
    private UserFilter userFilter;

    @Mock
    private GoalService goalService;

    @Mock
    private EventService eventService;

    @Mock
    private MentorshipService mentorshipService;

    @InjectMocks
    private UserServiceImpl userService;

    private long id;
    Stream<User> userStream;
    UserDto userDto;
    UserFilterDto userFilterDto;
    User user;
    List<UserFilter> filters;

    @BeforeEach
    void setUp() {
        id = 1;
        userFilterDto = new UserFilterDto();
        userDto = new UserDto(
                2L,
                "JaneSmith",
                "janesmith@example.com");

        user = User.builder()
                .id(2L)
                .goals(List.of())
                .ownedEvents(List.of())
                .username("JaneSmith")
                .email("janesmith@example.com")
                .phone("0987654321")
                .aboutMe("About Jane Smith")
                .experience(5)
                .build();

        userStream = Stream.of(user);
        filters = List.of(userFilter);
        userService = new UserServiceImpl(userRepository, filters, userMapper, goalService,
                eventService, mentorshipService);
    }

    @Test
    void shouldReturnPremiumUsersByFilters() {
        when(userRepository.findPremiumUsers()).thenReturn(Stream.of(user));
        when(filters.get(0).isApplicable(any())).thenReturn(true);
        when(filters.get(0).apply(any(), any())).thenReturn(Stream.of(user));

        var result = userService.getPremiumUsers(userFilterDto);

        verify(userRepository, times(1)).findPremiumUsers();
        verify(userMapper, times(1)).toDto(user);
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result).contains(userDto);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    void testDeactivateUserProfileWrongIdThrow() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> userService.deactivateUserProfile(id));
    }


    @Test
    void testDeactivateUserProfileOk() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        userService.deactivateUserProfile(id);

        verify(userRepository).findById(id);
        verify(goalService).removeGoals(List.of());
        verify(eventService).deleteEvents(List.of());
        verify(mentorshipService).deleteMentorFromMentees(anyLong(), any());
        verify(userRepository).save(any());
    }
}