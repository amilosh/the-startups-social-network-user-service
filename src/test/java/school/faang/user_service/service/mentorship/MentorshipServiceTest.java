package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.validation.Validation;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {

    @InjectMocks
    private MentorshipService mentorshipService;

    @Mock
    private MentorshipRepository mentorshipRepository;

    @Mock
    private Validation validation;

    @Spy
    private UserMapperImpl userMapper;

    @Captor
    private ArgumentCaptor<User> captorUser;

    User user;
    List<User> users = new ArrayList<>();

    @BeforeEach
    public void beforeEach() {
        user = new User();
        user.setId(1L);

        when(validation.validateUserData(user.getId())).thenReturn(user);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3})
    public void testGetMenteesNotEmptyList(int size) {
        long tempId = 10L;
        for (int i = 0; i < size; i++) {
            User mentee = new User();
            mentee.setId(tempId);
            users.add(mentee);
            tempId--;
        }
        user.setMentees(users);
        List<UserDto> menteesDto = user.getMentees().stream()
                .map(userMapper::toDto)
                .toList();

        List<UserDto> usersDto = mentorshipService.getMentees(user.getId());

        assertEquals(usersDto, menteesDto);
    }

    @Test
    public void testGetMenteesEmptyList() {
        user.setMentees(users);
        List<UserDto> menteesDto = user.getMentees().stream()
                .map(userMapper::toDto)
                .toList();

        List<UserDto> resultListUserDto = mentorshipService.getMentees(user.getId());

        assertEquals(resultListUserDto, menteesDto);
    }

    @Test
    public void testGetMentorsEmptyList() {
        user.setMentors(users);
        List<UserDto> mentorsDto = user.getMentors().stream()
                .map(userMapper::toDto)
                .toList();

        List<UserDto> resultListUserDto = mentorshipService.getMentors(user.getId());

        assertEquals(resultListUserDto, mentorsDto);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3})
    public void testGetMentorsNotEmptyList(int size) {
        long tempId = 10L;
        for (int i = 0; i < size; i++) {
            User mentor = new User();
            mentor.setId(tempId);
            users.add(mentor);
            tempId--;
        }
        user.setMentors(users);
        user.setId(1L);
        List<UserDto> mentorsDto = user.getMentors().stream()
                .map(userMapper::toDto)
                .toList();

        List<UserDto> resultListUserDto = mentorshipService.getMentors(user.getId());

        assertEquals(resultListUserDto, mentorsDto);
    }

    @Test
    public void testDeleteMentee() {
        User firstUser = new User();
        firstUser.setId(20L);
        users.add(firstUser);
        User secondUser = new User();
        secondUser.setId(30L);
        users.add(secondUser);
        user.setMentees(users);

        mentorshipService.deleteMentee(firstUser.getId(), user.getId());
        verify(mentorshipRepository, times(1)).save(captorUser.capture());
        User resultUser = captorUser.getValue();

        assertEquals(user, resultUser);
        assertFalse(user.getMentees().contains(firstUser));
    }

    @Test
    public void testDeleteMentor() {
        User firstUser = new User();
        firstUser.setId(20L);
        users.add(firstUser);
        User secondUser = new User();
        secondUser.setId(30L);
        users.add(secondUser);
        user.setMentors(users);

        mentorshipService.deleteMentor(firstUser.getId(), user.getId());
        verify(mentorshipRepository, times(1)).save(captorUser.capture());
        User resultUser = captorUser.getValue();

        assertEquals(user, resultUser);
        assertFalse(user.getMentors().contains(firstUser));
    }
}
