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

                .build();
        nonExistUser = User.builder()
                .id(NON_EXIST_USER_ID)
                .username("Roma")
                .email("roma@mail.ru")
                .city("Екатеринбург")
//                .mentees(new ArrayList<>(correctUser))
                .build();




    }

    @Test
    public void testGetMenteesWithNoSuchElement() {
        when(repository.findById(NON_EXIST_USER_ID)).thenThrow(NoSuchElementException.class);
        assertThrows(NoSuchMethodException.class, () -> service.getMentees(NON_EXIST_USER_ID));
    }


    @Test
    public void testGetMenteesWithNoMenteesForMentor() {
        when(repository.findById(NON_EXIST_USER_ID)).thenReturn(Optional.ofNullable(nonExistUser));

        List<UserDto> expectedList = service.getMentees(NON_EXIST_USER_ID);
//        List<UserDto> realList = new ArrayList<>(mapper.toDto(nonExistUser));

        verify(repository).findById(NON_EXIST_USER_ID);
//        assertEquals(expectedList, realList);
    }


    @Test
    public void testGetMenteesWithMenteesForMentor() {
        when(repository.findById(NON_EXIST_USER_ID)).thenReturn(Optional.ofNullable(nonExistUser));

        List<UserDto> expectedList = service.getMentees(NON_EXIST_USER_ID);
        List<UserDto> realList = new ArrayList<>();

        verify(repository).findById(NON_EXIST_USER_ID);
        assertEquals(expectedList, realList);
    }
}
