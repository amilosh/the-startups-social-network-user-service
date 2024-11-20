package school.faang.user_service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.service.goal.GoalStatusFilter;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GoalGoalInvitationStatusFilterTest {
    private final GoalFilterDto filter = new GoalFilterDto();

    @InjectMocks
    private GoalStatusFilter goalStatusFilter;

    @BeforeEach
    void setUp() {
        filter.setStatus(GoalStatus.ACTIVE);
    }

    @Test
    public void testIsApplicableTrue() {
        assertTrue(goalStatusFilter.isApplicable(filter));
    }

    @Test
    public void testIsApplicableFalse() {
        filter.setStatus(null);
        assertFalse(goalStatusFilter.isApplicable(filter));
    }

    @Test
    public void apply() {
        Goal firstGoal = new Goal();
        firstGoal.setStatus(GoalStatus.ACTIVE);
        Goal secondGoal = new Goal();
        secondGoal.setStatus(GoalStatus.COMPLETED);

        Stream<Goal> goals = Stream.of(firstGoal, secondGoal);
        Stream<Goal> filteredGoals = goalStatusFilter.apply(goals, filter);

        assertEquals(1, filteredGoals.count());
    }
}