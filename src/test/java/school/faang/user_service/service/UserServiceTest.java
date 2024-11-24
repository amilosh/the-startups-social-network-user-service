package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.user.goal.GoalService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapper userMapper;

    @Mock
    private UserContext userContext;

    @Mock
    private GoalService goalService;

    @Mock
    private MentorshipService mentorshipService;

    private long userId;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        userId = 1L;
        user.setId(userId);
    }

    @Test
    public void testExistsUserById() {
        when(userRepository.existsById(userId)).thenReturn(true);
        assertTrue(userService.existsById(userId));
    }

    @Test
    public void testNotExistsUserById() {
        when(userRepository.existsById(userId)).thenReturn(false);
        assertFalse(userService.existsById(userId));
    }

    @Test
    public void testGetUserById() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        assertEquals(user, userService.getUserById(userId));
    }

    @Test
    public void testThrowExceptionGetUserById() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> userService.getUserById(userId));
    }

    @Test
    public void testGetUserByIdNotfound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> userService.getUserById(userId));
    }

    @Test
    public void testGetUsersByIds() {
        List<User> users = List.of(new User(), new User());
        List<Long> ids = List.of(1L, 2L);

        when(userRepository.findAllById(ids)).thenReturn(users);
        userService.getUsersByIds(ids);
    }

    @Test
    public void testDeactivateUser() {
        Goal firstGoal = new Goal();
        Goal secondGoal = new Goal();

        List<Goal> goals = List.of(firstGoal, secondGoal);

        User firstMentor = new User();
        User secondMentor = new User();

        firstMentor.setId(5L);
        secondMentor.setId(7L);

        User firstMentee = new User();
        User secondMentee = new User();

        firstMentee.setId(8L);
        secondMentee.setId(9L);

        firstGoal.setMentor(firstMentor);
        firstGoal.setMentor(secondMentor);

        user.setGoals(goals);
        user.setOwnedEvents(List.of(new Event(), new Event()));
        user.setActive(true);

        user.setMentees(List.of(firstMentee, secondMentee));

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        doNothing().when(mentorshipService).deleteMentor(firstMentee.getId(), userId);
        doNothing().when(mentorshipService).deleteMentor(secondMentee.getId(), userId);
        when(goalService.getGoalsByMentorId(userId)).thenReturn(goals.stream());

        userService.deactivateUser();

        verify(userRepository).save(user);
        assertNull(user.getGoals());
        assertNull(user.getOwnedEvents());
        assertFalse(user.isActive());
    }
}
