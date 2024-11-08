package school.faang.user_service.service.goal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.goal.EntityNotFound;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private GoalService goalService;

    @Test
    @DisplayName("Test FindById Positive")
    void testFindGoalByIdPositive() {
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
    @DisplayName("Test FindById Negative")
    void testFindByIdNegative() {
        long goalId = 1L;
        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFound.class, () -> goalService.findGoalById(goalId));
        assertEquals(String.format("Goal not found by id: %s", goalId), exception.getMessage());
        verify(goalRepository, times(1)).findById(goalId);
    }
}
