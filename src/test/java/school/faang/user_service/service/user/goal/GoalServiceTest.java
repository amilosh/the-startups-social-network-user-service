package school.faang.user_service.service.user.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.goal.GoalRepository;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {

    @InjectMocks
    private GoalService goalService;

    @Mock
    private GoalRepository goalRepository;

    @Test
    public void testGetGoalsByMentorId() {
        long mentorId = 1L;

        goalService.getGoalsByMentorId(mentorId);
        verify(goalRepository).findGoalsByMentorId(mentorId);
    }
}
