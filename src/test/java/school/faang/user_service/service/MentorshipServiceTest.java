package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    private final long SIMPLE_ID = 15L;
    private final long NON_EXIST_USER_ID = 123456L;
    private User simpleUser;
    private User userWithEmptyListOfMenteesAndMentors;
    private User userWithMenteesAndMentors;

    @BeforeEach
    void initData() {
        User userForSimpleUser = User.builder()
                .id(CORRECT_ID_2)
                .build();
        simpleUser = User.builder()
                .id(SIMPLE_ID)
                .username("Roma")
                .email("roma@mail.ru")
                .city("London")
                .mentees(new ArrayList<>(Collections.singletonList(userForSimpleUser)))
                .mentors(new ArrayList<>(Collections.singletonList(userForSimpleUser)))
                .build();
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
                .mentees(new ArrayList<>(Collections.singletonList(simpleUser)))
                .mentors(new ArrayList<>(Arrays.asList(simpleUser, userWithEmptyListOfMenteesAndMentors)))
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
        when(repository.findById(SIMPLE_ID)).thenReturn(Optional.ofNullable(simpleUser));

        service.deleteMentee(SIMPLE_ID, CORRECT_ID_2);
        List<User> realList = userWithMenteesAndMentors.getMentees();
        List<User> expectedList = new ArrayList<>();

        verify(repository).findById(CORRECT_ID_2);
        verify(repository).findById(SIMPLE_ID);
        assertEquals(expectedList, realList);
    }

    @Test
    public void testDeleteMenteeWithNonExistingMenteeForMentor() {
        when(repository.findById(CORRECT_ID_2)).thenReturn(Optional.ofNullable(userWithMenteesAndMentors));
        when(repository.findById(CORRECT_ID_1)).thenReturn(Optional.ofNullable(userWithEmptyListOfMenteesAndMentors));

        List<User> realList = userWithMenteesAndMentors.getMentees();
        service.deleteMentee(CORRECT_ID_1, CORRECT_ID_2);
        List<User> expectedList = userWithMenteesAndMentors.getMentees();

        verify(repository).findById(CORRECT_ID_2);
        verify(repository).findById(CORRECT_ID_1);
        assertEquals(expectedList, realList);
    }

    @Test
    public void testDeleteMentorWithExistingMentorForMentee() {
        when(repository.findById(CORRECT_ID_2)).thenReturn(Optional.ofNullable(userWithMenteesAndMentors));
        when(repository.findById(SIMPLE_ID)).thenReturn(Optional.ofNullable(simpleUser));

        service.deleteMentor(SIMPLE_ID, CORRECT_ID_2);
        List<User> realList = simpleUser.getMentors();
        List<User> expectedList = new ArrayList<>();

        verify(repository).findById(CORRECT_ID_2);
        verify(repository).findById(SIMPLE_ID);

        assertEquals(expectedList, realList);
    }

    @Test
    public void testDeleteMentorWithNonExistingMentorForMentee() {
        when(repository.findById(CORRECT_ID_2)).thenReturn(Optional.ofNullable(userWithMenteesAndMentors));
        when(repository.findById(CORRECT_ID_1)).thenReturn(Optional.ofNullable(userWithEmptyListOfMenteesAndMentors));

        List<User> realList = userWithMenteesAndMentors.getMentors();
        service.deleteMentor(CORRECT_ID_1, CORRECT_ID_2);
        List<User> expectedList = userWithMenteesAndMentors.getMentors();

        verify(repository).findById(CORRECT_ID_2);
        verify(repository).findById(CORRECT_ID_1);
        assertEquals(expectedList, realList);
    }

    @Test
    public void testDeleteMentorWithNotExistingByMentorMentee() {
        when(repository.findById(CORRECT_ID_2)).thenReturn(Optional.ofNullable(userWithMenteesAndMentors));
        when(repository.findById(CORRECT_ID_1)).thenReturn(Optional.ofNullable(userWithEmptyListOfMenteesAndMentors));

        assertThrows(EntityNotFoundException.class, () -> service.deleteMentor(CORRECT_ID_2, CORRECT_ID_1));
    }
}
