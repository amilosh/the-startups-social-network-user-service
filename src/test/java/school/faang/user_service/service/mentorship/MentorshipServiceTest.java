package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {

    @Mock
    private UserRepository repository;

    @Spy
    private UserMapper mapper;

    @InjectMocks
    private MentorshipService service;

    private final long CORRECT_ID_1 = 1L;
    private final long CORRECT_ID_2 = 2L;
    private final long NON_EXIST_USER_ID = 123456L;
    private User userWithEmptyListOfMenteesAndMentors;
    private User userWithMenteesAndMentors;

    @BeforeEach
    void initData() {
        userWithEmptyListOfMenteesAndMentors = User.builder()
                .id(CORRECT_ID_1)
                .username("Max")
                .email("max@mail.ru")
                .city("Amsterdam")
                .mentees(new ArrayList<>())
                .mentors(new ArrayList<>())
                .build();
        userWithMenteesAndMentors = User.builder()
                .id(CORRECT_ID_2)
                .username("Denis")
                .email("denis@mail.ru")
                .city("New York")
                .mentees(new ArrayList<>(Collections.singletonList(userWithEmptyListOfMenteesAndMentors)))
                .mentors(new ArrayList<>(Collections.singletonList(userWithEmptyListOfMenteesAndMentors)))
                .build();
    }

    @Test
    public void testUserNotFound() {
        when(repository.findById(NON_EXIST_USER_ID)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> service.getMentees(NON_EXIST_USER_ID));
    }

    @Test
    public void testGetMenteesWithNoMenteesForUser() {
        when(repository.findById(CORRECT_ID_1)).thenReturn(Optional.ofNullable(userWithEmptyListOfMenteesAndMentors));

        List<UserDto> realList = service.getMentees(CORRECT_ID_1);
        List<UserDto> expectedList = new ArrayList<>();

        verify(repository).findById(CORRECT_ID_1);
        assertEquals(expectedList, realList);
    }

    @Test
    public void testGetMenteesWithMenteesForUser() {
        when(repository.findById(CORRECT_ID_2)).thenReturn(Optional.ofNullable(userWithMenteesAndMentors));

        List<UserDto> realList = service.getMentees(CORRECT_ID_2);
        List<UserDto> expectedList = mapper.toListDto(userWithMenteesAndMentors.getMentees());

        verify(repository).findById(CORRECT_ID_2);
        assertEquals(expectedList, realList);
    }

    @Test
    public void testGetMentorWithNoMentorForUser() {
        when(repository.findById(CORRECT_ID_1)).thenReturn(Optional.ofNullable(userWithEmptyListOfMenteesAndMentors));

        List<UserDto> realList = service.getMentors(CORRECT_ID_1);
        List<UserDto> expectedList = new ArrayList<>();

        verify(repository).findById(CORRECT_ID_1);
        assertEquals(expectedList, realList);
    }

    @Test
    public void testGetMentorWithMentorForUser() {
        when(repository.findById(CORRECT_ID_2)).thenReturn(Optional.ofNullable(userWithMenteesAndMentors));

        List<UserDto> realList = service.getMentors(CORRECT_ID_2);
        List<UserDto> expectedList = mapper.toListDto(userWithMenteesAndMentors.getMentors());

        verify(repository).findById(CORRECT_ID_2);
        assertEquals(expectedList, realList);
    }

    @Test
    public void testDeleteMenteeWithExistingMenteeForMentor() {
        when(repository.findById(CORRECT_ID_2)).thenReturn(Optional.ofNullable(userWithMenteesAndMentors));
        when(repository.findById(CORRECT_ID_1)).thenReturn(Optional.ofNullable(userWithEmptyListOfMenteesAndMentors));

        service.deleteMentee(CORRECT_ID_1, CORRECT_ID_2);
        List<User> realList = userWithMenteesAndMentors.getMentees();
        List<User> expectedList = new ArrayList<>();

        verify(repository).findById(CORRECT_ID_2);
        verify(repository).findById(CORRECT_ID_1);
        verify(repository).save(userWithMenteesAndMentors);
        assertEquals(expectedList, realList);
    }

    @Test
    public void testDeleteMenteeWithNonExistingMenteeForMentor() {
        when(repository.findById(CORRECT_ID_2)).thenReturn(Optional.ofNullable(userWithMenteesAndMentors));
        when(repository.findById(CORRECT_ID_1)).thenReturn(Optional.ofNullable(userWithEmptyListOfMenteesAndMentors));

        List<User> realList = userWithEmptyListOfMenteesAndMentors.getMentees();
        service.deleteMentee(CORRECT_ID_2, CORRECT_ID_1);
        List<User> expectedList = userWithEmptyListOfMenteesAndMentors.getMentees();

        verify(repository).findById(CORRECT_ID_2);
        verify(repository).findById(CORRECT_ID_1);
        verify(repository, never()).save(any());
        assertEquals(expectedList, realList);
    }

    @Test
    public void testDeleteMentorWithExistingMentorForMentee() {
        when(repository.findById(CORRECT_ID_2)).thenReturn(Optional.ofNullable(userWithMenteesAndMentors));
        when(repository.findById(CORRECT_ID_1)).thenReturn(Optional.ofNullable(userWithEmptyListOfMenteesAndMentors));

        service.deleteMentor(CORRECT_ID_2, CORRECT_ID_1);
        List<User> realList = userWithMenteesAndMentors.getMentors();
        List<User> expectedList = new ArrayList<>();

        verify(repository).findById(CORRECT_ID_2);
        verify(repository).findById(CORRECT_ID_1);
        verify(repository).save(userWithMenteesAndMentors);
        assertEquals(expectedList, realList);
    }

    @Test
    public void testDeleteMentorWithNonExistingMentorForMentee() {
        when(repository.findById(CORRECT_ID_2)).thenReturn(Optional.ofNullable(userWithMenteesAndMentors));
        when(repository.findById(CORRECT_ID_1)).thenReturn(Optional.ofNullable(userWithEmptyListOfMenteesAndMentors));

        List<User> realList = userWithEmptyListOfMenteesAndMentors.getMentors();
        service.deleteMentor(CORRECT_ID_1, CORRECT_ID_2);
        List<User> expectedList = userWithEmptyListOfMenteesAndMentors.getMentors();

        verify(repository).findById(CORRECT_ID_2);
        verify(repository).findById(CORRECT_ID_1);
        verify(repository, never()).save(any());
        assertEquals(expectedList, realList);
    }

    @Test
    void testThrowExceptionWhenMentorIsNull() {
        assertThrows(IllegalArgumentException.class, () -> service.stopMentorship(null));
    }

    @Test
    void testStopMentorshipSuccess() {
        User mentee = User.builder()
                .id(CORRECT_ID_1)
                .username("MenteeUser")
                .email("mentee@mail.com")
                .city("CityMentee")
                .mentees(new ArrayList<>())
                .mentors(new ArrayList<>())
                .build();

        User mentor = User.builder()
                .id(CORRECT_ID_2)
                .username("MentorUser")
                .email("mentor@mail.com")
                .city("CityMentor")
                .mentees(new ArrayList<>(Collections.singletonList(mentee)))
                .mentors(new ArrayList<>())
                .build();

        when(repository.save(mentee)).thenReturn(mentee);

        service.stopMentorship(mentor);

        assertTrue(mentee.getMentors().isEmpty(),
                "Mentee's mentors list should be empty after stopping mentorship");

        verify(repository).save(mentee);
    }
}