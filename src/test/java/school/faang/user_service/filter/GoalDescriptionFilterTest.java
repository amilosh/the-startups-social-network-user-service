package school.faang.user_service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.service.goal.GoalDescriptionFilter;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GoalDescriptionFilterTest {
    private final GoalFilterDto filter = new GoalFilterDto();

    @InjectMocks
    private GoalDescriptionFilter goalDescriptionFilter;

    @BeforeEach
    void setUp() {
        filter.setDescription("Test Goal 2");
    }

    @Test
    public void testIsApplicableTrue() {
        assertTrue(goalDescriptionFilter.isApplicable(filter));
    }

    @Test
    public void testIsApplicableFalse() {
        filter.setDescription(null);
        assertFalse(goalDescriptionFilter.isApplicable(filter));
    }

    @Test
    public void apply() {
        Goal firstGoal = new Goal();
        firstGoal.setDescription("Test Goal");
        Goal secondGoal = new Goal();
        secondGoal.setDescription("Test Goal 2");

        Stream<Goal> goals = Stream.of(firstGoal, secondGoal);
        Stream<Goal> filteredGoals = goalDescriptionFilter.apply(goals, filter);

        assertEquals(1, filteredGoals.count());
    }
}