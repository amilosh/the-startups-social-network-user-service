package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.MenteesDto;
import school.faang.user_service.dto.MentorsDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MenteesMapperImpl;
import school.faang.user_service.mapper.MentorsMapperImpl;
import school.faang.user_service.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {
    @Spy
    private MenteesMapperImpl menteesMapper;
    @Spy
    private MentorsMapperImpl mentorsMapper;
    @Mock
    private UserRepository userRepository;

    @Captor
    private ArgumentCaptor<User> captor;

    @InjectMocks
    private MentorshipService mentorshipService;

    long id = 1L;
    User user;

    @BeforeEach
    public void init() {
        user = new User();
        user.setId(id);

        User mentee1 = new User();
        mentee1.setId(3L);
        mentee1.setUsername("Karl");

        User mentee2 = new User();
        mentee2.setId(5L);
        mentee2.setUsername("Kolya");

        List<User> mentees = new ArrayList<>();
        mentees.add(mentee1);
        mentees.add(mentee2);

        user.setMentees(mentees);

        User mentor1 = new User();
        mentor1.setId(6L);
        mentor1.setUsername("Zara");

        User mentor2 = new User();
        mentor2.setId(7L);
        mentor2.setUsername("Tommy");

        List<User> mentors = new ArrayList<>();
        mentors.add(mentor1);
        mentors.add(mentor2);

        user.setMentors(mentors);
    }

    private void prepareData(boolean existById) {
        if (existById) {
            when(userRepository.findById(id)).thenReturn(Optional.of(user));
        } else {
            when(userRepository.findById(id)).thenReturn(Optional.empty());
        }
    }

    @Test
    public void getMenteesUserEmptyNegativeTest() {
        prepareData(false);
        assertThrows(RuntimeException.class, () -> mentorshipService.getMentees(id));
    }

    @Test
    public void getMenteesListEmptyNegativeTest() {
        user.getMentees().clear();
        prepareData(true);
        assertEquals(new ArrayList<>(), mentorshipService.getMentees(id));
    }

    @Test
    public void getMenteesPositiveTest() {
        prepareData(true);
        List<MenteesDto> mentees = menteesMapper.toDto(user.getMentees());
        assertEquals(mentees, mentorshipService.getMentees(id));
    }

    @Test
    public void getMentorsUserEmptyNegativeTest() {
        prepareData(false);
        assertThrows(RuntimeException.class, () -> mentorshipService.getMentors(id));
    }

    @Test
    public void getMentorsListEmptyNegativeTest() {
        user.getMentors().clear();
        prepareData(true);
        assertEquals(new ArrayList<>(), mentorshipService.getMentors(id));
    }

    @Test
    public void getMentorsPositiveTest() {
        prepareData(true);
        List<MentorsDto> mentors = mentorsMapper.toDto(user.getMentors());
        assertEquals(mentors, mentorshipService.getMentors(id));
    }

    @Test
    public void deleteMenteeUserEmptyNegativeTest() {
        prepareData(false);
        assertThrows(RuntimeException.class, () -> mentorshipService.deleteMentee(3L, id));
    }

    @Test
    public void deleteMenteesListEmptyNegativeTest() {
        user.getMentees().clear();
        prepareData(true);
        mentorshipService.deleteMentee(3L, id);
    }

    @Test
    public void deleteSaveMenteePositiveTest() {
        prepareData(true);
        mentorshipService.deleteMentee(3L, id);
        user.getMentees().removeIf(user -> user.getId().equals(3L));
        verify(userRepository, times(1)).save(captor.capture());
        User userCaptor = captor.getValue();
        assertEquals(user.getId(), userCaptor.getId());
        assertEquals(user.getMentees(), userCaptor.getMentees());
    }

    @Test
    public void deleteMentorUserEmptyNegativeTest() {
        prepareData(false);
        assertThrows(RuntimeException.class, () -> mentorshipService.deleteMentor(id, 6L));
    }

    @Test
    public void deleteMentorsListEmptyNegativeTest() {
        user.getMentors().clear();
        prepareData(true);
        mentorshipService.deleteMentor(id, 6L);
    }

    @Test
    public void deleteSaveMentorPositiveTest() {
        prepareData(true);
        mentorshipService.deleteMentor(id, 6L);
        user.getMentors().removeIf(user -> user.getId().equals(6L));
        verify(userRepository, times(1)).save(captor.capture());
        User userCaptor = captor.getValue();
        assertEquals(user.getId(), userCaptor.getId());
        assertEquals(user.getMentors(), userCaptor.getMentors());
    }
}
