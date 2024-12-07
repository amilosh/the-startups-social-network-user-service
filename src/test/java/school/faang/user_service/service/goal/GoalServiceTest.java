package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.events.GoalCompletedEvent;
import school.faang.user_service.publisher.GoalCompletedEventPublisher;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validator.GoalValidator;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {

    @Mock
    private UserContext userContext;

    @Mock
    private GoalCompletedEventPublisher completedEventPublisher;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private GoalValidator goalValidator;

    @InjectMocks
    private GoalService goalService;

    private Goal goal;

    @BeforeEach
    void setUp() {

        goal = new Goal();
        goal.setId(1L);
        goal.setStatus(GoalStatus.COMPLETED);
    }

    @Test
    void testCompleteGoalShouldPublishEventWhenGoalIsCompleted() {
        long goalId = 1L;

        when(userContext.getUserId()).thenReturn(100L);
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));

        goalService.completeGoal(goalId);

        assertEquals(GoalStatus.COMPLETED, goal.getStatus());
        verify(goalRepository).save(goal);
        verify(completedEventPublisher).publish(any(GoalCompletedEvent.class));
    }

    @Test
    void testCompleteGoalShouldThrowExceptionWhenGoalIsNotFound() {
        long goalId = 2L;

        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            goalService.completeGoal(goalId);
        });
    }

    @Test
    void testGetGoalShouldReturnGoalWhenGoalExists() {
        long goalId = 1L;

        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));

        Goal result = goalService.getGoal(goalId);
        assertNotNull(result);
        assertEquals(goalId, result.getId());
    }

    @Test
    void testGetGoalShouldThrowExceptionWhenGoalDoesNotExist() {
        long goalId = 3L;

        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            goalService.getGoal(goalId);
        });
        assertEquals("Цель не найдена в базе данных", exception.getMessage());
    }

    @Test
    void testGoalRepositoryCallSaveWhenGoalServiceCallSaveGoal(){
        goalService.saveGoal(goal);
        verify(goalRepository).save(goal);
    }
}