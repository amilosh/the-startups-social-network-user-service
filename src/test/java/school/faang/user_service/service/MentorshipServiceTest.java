package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.UserMapperImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {

    @InjectMocks
    public MentorshipService mentorshipService;
    @Mock
    private UserService userService;
    @Spy
    private UserMapperImpl userMapper;

    private User mentor;
    private User mentee;

    @BeforeEach
    public void setUp() {
        mentor = new User();
        mentor.setId(1L);
        mentee = new User();
        mentee.setId(2L);

        Goal goalWithMentee = new Goal();
        goalWithMentee.setUsers(List.of(mentee));
        Goal goalWithoutMentee = new Goal();
        goalWithoutMentee.setUsers(new ArrayList<>());

        mentor.setSetGoals(List.of(goalWithMentee, goalWithoutMentee));
    }

    @Test
    public void getMenteesWhenUserHasMentees() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        User mentee1 = new User();
        mentee1.setId(2L);
        User mentee2 = new User();
        mentee2.setId(3L);
        List<User> mentees = List.of(mentee1, mentee2);
        user.setMentees(mentees);
        when(userService.findUserById(userId)).thenReturn(user);

        List<UserDto> result = mentorshipService.getMentees(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(2L, result.get(0).getId());
        verify(userService, times(1)).findUserById(userId);
    }

    @Test
    public void getMenteesUserWithNoMenteesReturnsEmptyList() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setMentees(Collections.emptyList());
        when(userService.findUserById(userId)).thenReturn(user);

        List<UserDto> result = mentorshipService.getMentees(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userService, times(1)).findUserById(userId);
    }

    @Test
    public void testGetMentorsWhenUserHasMentees() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        User mentor1 = new User();
        mentor1.setId(2L);
        User mentor2 = new User();
        mentor2.setId(3L);
        List<User> mentors = List.of(mentor1, mentor2);
        user.setMentors(mentors);
        when(userService.findUserById(userId)).thenReturn(user);

        List<UserDto> result = mentorshipService.getMentors(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(2L, result.get(0).getId());
        assertEquals(3L, result.get(1).getId());
        verify(userService, times(1)).findUserById(userId);
    }

    @Test
    public void testGetMentorsUserWithNoMenteesReturnsEmptyList() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setMentors(Collections.emptyList());
        when(userService.findUserById(userId)).thenReturn(user);

        List<UserDto> result = mentorshipService.getMentors(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userService, times(1)).findUserById(userId);
    }

    @Test
    public void testDeleteMenteeWhenMenteeExists() {
        User mentor = new User();
        mentor.setId(1L);
        User mentee = new User();
        mentee.setId(2L);
        mentor.setMentees(new ArrayList<>());
        mentor.getMentees().add(mentee);
        when(userService.findUserById(mentor.getId())).thenReturn(mentor);

        mentorshipService.deleteMentee( mentor.getId(),mentee.getId());

        verify(userService, times(1)).saveUser(mentor);
        assertTrue(mentor.getMentees().isEmpty());
    }

    @Test
    public void testDeleteMenteeWhenMenteeDoesNotExist() {
        User mentor = new User();
        mentor.setId(1L);
        mentor.setMentees(new ArrayList<>());
        when(userService.findUserById(mentor.getId())).thenReturn(mentor);

        mentorshipService.deleteMentee( mentor.getId(), 3L);

        verify(userService, never()).saveUser(mentor);
    }

    @Test
    public void testDeleteMentorWhenMenteeExists() {
        User mentee = new User();
        mentee.setId(1L);
        User mentor = new User();
        mentor.setId(2L);
        mentee.setMentors(new ArrayList<>());
        mentee.getMentors().add(mentor);
        when(userService.findUserById(mentee.getId())).thenReturn(mentee);

        mentorshipService.deleteMentor(mentee.getId(), mentor.getId());

        verify(userService, times(1)).saveUser(mentee);
        assertTrue(mentee.getMentors().isEmpty());
    }

    @Test
    public void testDeleteMentorWhenMenteeDoesNotExist() {
        User mentee = new User();
        mentee.setId(1L);
        mentee.setMentors(new ArrayList<>());
        when(userService.findUserById(mentee.getId())).thenReturn(mentee);

        mentorshipService.deleteMentor(mentee.getId(), 3L);

        verify(userService, never()).saveUser(mentee);
    }

    @Test
    public void testMoveGoalsToMentee_Successful() {
        when(userService.findUserById(1L)).thenReturn(mentor);
        when(userService.findUserById(2L)).thenReturn(mentee);

        mentorshipService.moveGoalsToMentee(2, 1);

        assertEquals(mentee, mentor.getSetGoals().stream()
                .filter(goal -> goal.getUsers().contains(mentee))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Goal not found"))
                .getMentor());
    }
}


