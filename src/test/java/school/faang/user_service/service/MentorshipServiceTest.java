package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MentorshipServiceTest {

    @InjectMocks
    private MentorshipService service;

    @Mock
    private UserRepository repository;

    @Mock
    private UserMapper mapper;

    private final long NON_EXIST_USER_ID = 123456L;
    private final long EXISTING_USER_ID = 2L;
    private User testUser;
    private User correctUser;
    private User incorrectUser;
    private User nonExistUser;
    private List<UserDto> userDtoList;

    @BeforeEach
    void initData() {
        correctUser = User.builder()
                .id(EXISTING_USER_ID)
                .username("Roma")
                .email("roma@mail.ru")
                .city("Екатеринбург")
                .mentors(new ArrayList<>(Arrays.asList(testUser)))
                .build();
        testUser = User.builder()
                .id(3L)
                .username("Roma")
                .email("fdldsfld")
                .city("Moskow")
                .mentees(new ArrayList<>(Arrays.asList(correctUser, nonExistUser))).build();
        nonExistUser = User.builder()
                .id(NON_EXIST_USER_ID)
                .username("Roma")
                .email("roma@mail.ru")
                .city("Екатеринбург")
                .mentees(new ArrayList<>())
                .build();
    }

    @Test
    public void testGetMenteesWithNoSuchElement() {
        when(repository.findById(NON_EXIST_USER_ID)).thenThrow(NoSuchElementException.class);
        assertThrows(NoSuchMethodException.class, () -> service.getMentees(NON_EXIST_USER_ID));
    }


    @Test
    public void testGetMenteesWithNoMenteesForUser() {
        when(repository.findById(NON_EXIST_USER_ID)).thenReturn(Optional.ofNullable(nonExistUser));

        List<UserDto> expectedList = service.getMentees(NON_EXIST_USER_ID);
        List<UserDto> realList = new ArrayList<>();

        verify(repository).findById(NON_EXIST_USER_ID);
        assertEquals(realList, expectedList);
    }


    @Test
    public void testGetMenteesWithMenteesForUser() {
        when(repository.findById(3L)).thenReturn(Optional.ofNullable(testUser));

        List<UserDto> expectedList = service.getMentees(3L);
        List<UserDto> realList = new ArrayList<>(Arrays.asList(mapper.toDto(correctUser)));

        verify(repository).findById(NON_EXIST_USER_ID);
        assertEquals(realList, expectedList);
    }

    @Test
    public void testGetMentorWithNoMentorForUser() {
        when(repository.findById(NON_EXIST_USER_ID)).thenReturn(Optional.ofNullable(nonExistUser));

        List<UserDto> expectedList = service.getMentors(NON_EXIST_USER_ID);
        List<UserDto> realList = new ArrayList<>();

        verify(repository).findById(NON_EXIST_USER_ID);
        assertEquals(realList, expectedList);
    }

    @Test
    public void testGetMentorWithMentorForUser() {
        when(repository.findById(EXISTING_USER_ID)).thenReturn(Optional.ofNullable(correctUser));

        List<UserDto> expectedList = service.getMentors(EXISTING_USER_ID);
        List<UserDto> realList = new ArrayList<>(Arrays.asList(mapper.toDto(testUser)));

        verify(repository).findById(NON_EXIST_USER_ID);
        assertEquals(realList, expectedList);
    }

    @Test
    public void testDeleteMenteeWithExistingMenteeForMentor() {
        when(repository.findById(EXISTING_USER_ID)).thenReturn(Optional.ofNullable(correctUser));
        when(repository.findById(3L)).thenReturn(Optional.ofNullable(testUser));

        service.deleteMentee(EXISTING_USER_ID, 3L);

        List<User> expectedList = testUser.getMentees();
        List<User> realList = new ArrayList<>(Arrays.asList(nonExistUser));

        verify(repository).findById(EXISTING_USER_ID);
        verify(repository).findById(3L);

        assertEquals(realList, expectedList);

    }

    @Test
    public void testDeleteMentorWithNonExistingMenteeForMentor() {
        when(repository.findById(EXISTING_USER_ID)).thenReturn(Optional.ofNullable(correctUser));
        when(repository.findById(NON_EXIST_USER_ID)).thenReturn(Optional.ofNullable(nonExistUser));

        List<User> expectedList = nonExistUser.getMentees();

        service.deleteMentee(EXISTING_USER_ID, NON_EXIST_USER_ID);

        List<User> realList = nonExistUser.getMentees();

        verify(repository).findById(EXISTING_USER_ID);
        verify(repository).findById(NON_EXIST_USER_ID);

        assertEquals(realList, expectedList);
    }

    @Test
    public void testDeleteMentorWithExistingMentorForMentee() {
        when(repository.findById(EXISTING_USER_ID)).thenReturn(Optional.ofNullable(correctUser));
        when(repository.findById(3L)).thenReturn(Optional.ofNullable(testUser));

        service.deleteMentor(EXISTING_USER_ID, 3L);

        List<User> expectedList = correctUser.getMentors();
        List<User> realList = new ArrayList<>();

        verify(repository).findById(EXISTING_USER_ID);
        verify(repository).findById(3L);

        assertEquals(realList, expectedList);

    }

    @Test
    public void testDeleteMentorWithNonExistingMentorForMentee() {
        when(repository.findById(EXISTING_USER_ID)).thenReturn(Optional.ofNullable(correctUser));
        when(repository.findById(NON_EXIST_USER_ID)).thenReturn(Optional.ofNullable(nonExistUser));

        List<User> expectedList = correctUser.getMentors();

        service.deleteMentee(EXISTING_USER_ID, NON_EXIST_USER_ID);

        List<User> realList = correctUser.getMentors();

        verify(repository).findById(EXISTING_USER_ID);
        verify(repository).findById(NON_EXIST_USER_ID);

        assertEquals(realList, expectedList);
    }

}
