package school.faang.user_service.service.goal;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class GoalTitleFilterTest {
    private final GoalTitleFilter goalTitleFilter = new GoalTitleFilter();

    private Goal goal1;
    private Goal goal2;
    private Goal goal3;

    @BeforeEach
    void setUp() {
        goal1 = new Goal();
        goal1.setTitle("Learn Java");

        goal2 = new Goal();
        goal2.setTitle("Learn Python");

        goal3 = new Goal();
        goal3.setTitle("Learn JavaScript");
    }

    @Test
    void isApplicable_WhenTitleIsNotNull_ReturnsTrue() {
        GoalFilterDto filter = new GoalFilterDto();
        filter.setTitle("Java");

        boolean result = goalTitleFilter.isApplicable(filter);
        assertTrue(result);
    }

    @Test
    void isApplicable_WhenTitleIsNull_ReturnsFalse() {
        GoalFilterDto filter = new GoalFilterDto();
        filter.setTitle(null);

        boolean result = goalTitleFilter.isApplicable(filter);

        assertFalse(result);
    }

    @Test
    void apply_WithMatchingGoals_ReturnsFilteredStream() {
        List<Goal> goals = List.of(goal1, goal2, goal3);
        GoalFilterDto filter = new GoalFilterDto();
        filter.setTitle("Java");

        Stream<Goal> filteredGoals = goalTitleFilter.apply(goals.stream(), filter);

        List<Goal> result = filteredGoals.toList();

        assertEquals(2, result.size());
        assertTrue(result.contains(goal1));
        assertTrue(result.contains(goal3));
        assertFalse(result.contains(goal2));
    }

    @Test
    void apply_WithNoMatchingGoals_ReturnsEmptyStream() {
        List<Goal> goals = List.of(goal1, goal2, goal3);
        GoalFilterDto filter = new GoalFilterDto();
        filter.setTitle("C++");

        Stream<Goal> filteredGoals = goalTitleFilter.apply(goals.stream(), filter);

        List<Goal> result = filteredGoals.toList();

        assertTrue(result.isEmpty());
    }
}