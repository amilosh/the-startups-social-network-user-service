package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.service.user.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {

    @Mock
    private UserService userService;
    @Spy
    private UserMapperImpl userMapper;
    @InjectMocks
    public MentorshipService mentorshipService;

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
        when(userService.findUser(userId)).thenReturn(user);

        List<UserDto> result = mentorshipService.getMentees(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(2L, result.get(0).getId());
        verify(userService, times(1)).findUser(userId);
    }

    @Test
    public void getMenteesUserWithNoMenteesReturnsEmptyList() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setMentees(Collections.emptyList());
        when(userService.findUser(userId)).thenReturn(user);

        List<UserDto> result = mentorshipService.getMentees(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userService, times(1)).findUser(userId);
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
        when(userService.findUser(userId)).thenReturn(user);

        List<UserDto> result = mentorshipService.getMentors(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(2L, result.get(0).getId());
        assertEquals(3L, result.get(1).getId());
        verify(userService, times(1)).findUser(userId);
    }

    @Test
    public void testGetMentorsUserWithNoMenteesReturnsEmptyList() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setMentors(Collections.emptyList());
        when(userService.findUser(userId)).thenReturn(user);

        List<UserDto> result = mentorshipService.getMentors(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userService, times(1)).findUser(userId);
    }

    @Test
    public void testDeleteMenteeWhenMenteeExists() {
        User mentor = new User();
        mentor.setId(1L);
        User mentee = new User();
        mentee.setId(2L);
        mentor.setMentees(new ArrayList<>());
        mentor.getMentees().add(mentee);
        when(userService.findUser(mentor.getId())).thenReturn(mentor);

        mentorshipService.deleteMentee(mentee.getId(), mentor.getId());

        verify(userService, times(1)).saveUser(mentor);
        assertTrue(mentor.getMentees().isEmpty());
    }

    @Test
    public void testDeleteMenteeWhenMenteeDoesNotExist() {
        User mentor = new User();
        mentor.setId(1L);
        mentor.setMentees(new ArrayList<>());
        when(userService.findUser(mentor.getId())).thenReturn(mentor);

        mentorshipService.deleteMentee(3L, mentor.getId());

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
        when(userService.findUser(mentee.getId())).thenReturn(mentee);

        mentorshipService.deleteMentor(mentee.getId(), mentor.getId());

        verify(userService, times(1)).saveUser(mentee);
        assertTrue(mentee.getMentors().isEmpty());
    }

    @Test
    public void testDeleteMentorWhenMenteeDoesNotExist() {
        User mentee = new User();
        mentee.setId(1L);
        mentee.setMentors(new ArrayList<>());
        when(userService.findUser(mentee.getId())).thenReturn(mentee);

        mentorshipService.deleteMentor(mentee.getId(), 3L);

        verify(userService, never()).saveUser(mentee);
    }
}

