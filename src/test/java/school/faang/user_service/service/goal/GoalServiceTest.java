package school.faang.user_service.service.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private GoalService goalService;

    @Test
    public void testFindGoalByIdPositive() {
        long goalId = 1L;
        Goal goal = Goal.builder()
                .id(1L)
                .build();
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));

        Goal result = goalService.findGoalById(goalId);

        verify(goalRepository, times(1)).findById(goalId);
        assertNotNull(result);
        assertEquals(goalId, result.getId());
    }

    @Test
    public void testFindByIdNegative() {
        long goalId = 1L;
        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> goalService.findGoalById(goalId));
        assertEquals("Goal not found", exception.getMessage());
        verify(goalRepository, times(1)).findById(goalId);
    }
}
