package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.mentorship.MentorshipService;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class MentorshipServiceTest {

    @InjectMocks
    private MentorshipService service;

    @Mock
    private UserRepository repository;

    @Test
    public void testGetMenteesWithNoSuchElement() {
//        assertEquals()

    }

    @Test
    public void testGetMenteesWithNoMenteesForMentor() {

    }

    @Test
    public void testGetMenteesWithMenteesForMentor() {

    }
}
