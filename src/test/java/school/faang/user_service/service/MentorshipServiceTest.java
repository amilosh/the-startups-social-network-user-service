package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class MentorshipServiceTest {

    private final long NON_EXIST_USER_ID = 0L;

    @InjectMocks
    private MentorshipService service;

    @Mock
    private UserRepository repository;

    @Test
    public void testGetMenteesWithNoMenteesForMentor() {
//        when(repository.findById(NON_EXIST_USER_ID)).thenReturn(Optional.ofNullable());
        assertThrows(NoSuchMethodException.class, () -> service.getMentees(NON_EXIST_USER_ID));
    }


    @Test
    public void testGetMenteesWithMenteesForMentor() {

    }
}
