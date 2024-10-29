package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class MentorshipServiceTest {

    @InjectMocks
    private MentorshipService service;

    @Mock
    private UserRepository userRepository;

    private final long NON_EXIST_USER_ID = 0L;
    private final long EXISTING_USER_ID = 2L;
    private User correctUser;
    private User incorrectUser;
    private User nonExistUser;

    @BeforeEach
    void initData() {
        correctUser = User.builder()
                .id(EXISTING_USER_ID)
                .username("Roma")
                .email("roma@mail.ru")
                .city("Екатеринбург")
                .mentees(new ArrayList<>())
                .build();
        nonExistUser = User.builder()
                .id(NON_EXIST_USER_ID)
                .username("Roma")
                .email("roma@mail.ru")
                .city("Екатеринбург")
                .build();




    }




    @Test
    public void testGetMenteesWithNoMenteesForMentor() {
//        when(repository.findById(NON_EXIST_USER_ID)).thenReturn(Optional.ofNullable());
        assertThrows(NoSuchMethodException.class, () -> service.getMentees(NON_EXIST_USER_ID));
    }


    @Test
    public void testGetMenteesWithMenteesForMentor() {

    }
}
