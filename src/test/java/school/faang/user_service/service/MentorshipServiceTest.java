package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
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

    private final long NON_EXIST_USER_ID = 123456L;
    private final long EXISTING_USER_ID = 2L;
    private User simpleUser;
    private User correctUser;
    private User nonExistUser;

    @BeforeEach
    void initData() {
        simpleUser = User.builder()
                .id(3L)
                .username("Roma")
                .email("roma@mail.ru")
                .city("London")
                .build();
        nonExistUser = User.builder()
                .id(NON_EXIST_USER_ID)
                .username("Max")
                .email("max@mail.ru")
                .city("Amsterdam")
                .mentees(new ArrayList<>())
                .mentors(new ArrayList<>())
                .build();
        correctUser = User.builder()
                .id(EXISTING_USER_ID)
                .username("Denis")
                .email("denis@mail.ru")
                .city("New York")
                .mentees(new ArrayList<>(Arrays.asList(simpleUser, nonExistUser)))
                .mentors(new ArrayList<>(Arrays.asList(simpleUser, nonExistUser)))
                .build();
    }

    @Test
    public void testUserNotFound() {
        when(repository.findById(NON_EXIST_USER_ID)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> service.getMentees(NON_EXIST_USER_ID));
    }

    @Test
    public void testGetMenteesWithNoMenteesForUser() {
        when(repository.findById(NON_EXIST_USER_ID)).thenReturn(Optional.ofNullable(nonExistUser));

        List<UserDto> realList = service.getMentees(NON_EXIST_USER_ID);
        List<UserDto> expectedList = new ArrayList<>();

        verify(repository).findById(NON_EXIST_USER_ID);
        assertEquals(expectedList, realList);
    }

    @Test
    public void testGetMenteesWithMenteesForUser() {
        when(repository.findById(EXISTING_USER_ID)).thenReturn(Optional.ofNullable(correctUser));

        List<UserDto> realList = service.getMentees(EXISTING_USER_ID);
        List<UserDto> expectedList = getUserDtoList(correctUser.getMentees());

        verify(repository).findById(EXISTING_USER_ID);
        assertEquals(expectedList, realList);
    }

    @Test
    public void testGetMentorWithNoMentorForUser() {
        when(repository.findById(NON_EXIST_USER_ID)).thenReturn(Optional.ofNullable(nonExistUser));

        List<UserDto> realList = service.getMentors(NON_EXIST_USER_ID);
        List<UserDto> expectedList = new ArrayList<>();

        verify(repository).findById(NON_EXIST_USER_ID);
        assertEquals(expectedList, realList);
    }

    @Test
    public void testGetMentorWithMentorForUser() {
        when(repository.findById(EXISTING_USER_ID)).thenReturn(Optional.ofNullable(correctUser));

        List<UserDto> realList = service.getMentors(EXISTING_USER_ID);
        List<UserDto> expectedList = getUserDtoList(correctUser.getMentors());

        verify(repository).findById(EXISTING_USER_ID);
        assertEquals(expectedList, realList);
    }

    @Test
    public void testDeleteMenteeWithExistingMenteeForMentor() {
        when(repository.findById(EXISTING_USER_ID)).thenReturn(Optional.ofNullable(correctUser));
        when(repository.findById(3L)).thenReturn(Optional.ofNullable(simpleUser));

        service.deleteMentee(3L, EXISTING_USER_ID);
        List<User> realList = correctUser.getMentees();
        List<User> expectedList = new ArrayList<>(Collections.singletonList(nonExistUser));

        verify(repository).findById(EXISTING_USER_ID);
        verify(repository).findById(3L);
        assertEquals(expectedList, realList);
    }

    @Test
    public void testDeleteMentorWithNonExistingMenteeForMentor() {
        when(repository.findById(EXISTING_USER_ID)).thenReturn(Optional.ofNullable(correctUser));

        List<User> realList = correctUser.getMentees();
        service.deleteMentee(EXISTING_USER_ID, EXISTING_USER_ID);
        List<User> expectedList = correctUser.getMentees();

        verify(repository, times(2)).findById(EXISTING_USER_ID);
        assertEquals(expectedList, realList);
    }

    @Test
    public void testDeleteMentorWithExistingMentorForMentee() {
        when(repository.findById(EXISTING_USER_ID)).thenReturn(Optional.ofNullable(correctUser));
        when(repository.findById(3L)).thenReturn(Optional.ofNullable(simpleUser));

        service.deleteMentor(EXISTING_USER_ID, 3L);
        List<User> realList = correctUser.getMentors();
        List<User> expectedList = new ArrayList<>(Collections.singletonList(nonExistUser));

        verify(repository).findById(EXISTING_USER_ID);
        verify(repository).findById(3L);
        assertEquals(expectedList, realList);
    }

    @Test
    public void testDeleteMentorWithNonExistingMentorForMentee() {
        when(repository.findById(EXISTING_USER_ID)).thenReturn(Optional.ofNullable(correctUser));

        List<User> realList = correctUser.getMentors();
        service.deleteMentee(EXISTING_USER_ID, EXISTING_USER_ID);
        List<User> expectedList = correctUser.getMentors();

        verify(repository, times(2)).findById(EXISTING_USER_ID);
        assertEquals(expectedList, realList);
    }

    public List<UserDto> getUserDtoList(List<User> users) {
        return users.stream().map(mentee -> mapper.toDto(mentee)).toList();
    }
}
