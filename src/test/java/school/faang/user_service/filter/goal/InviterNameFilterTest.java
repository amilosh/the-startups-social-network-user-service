package school.faang.user_service.filter.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class InviterNameFilterTest {

    private InviterNameFilter inviterNameFilter;

    private GoalInvitation firstGoalInvitation;
    private GoalInvitation secondGoalInvitation;
    private User firstUser;
    private User secondUser;

    @BeforeEach
    public void setUp() {
        inviterNameFilter = new InviterNameFilter();
        firstUser = new User();
        secondUser = new User();
        firstGoalInvitation = new GoalInvitation();
        secondGoalInvitation = new GoalInvitation();
    }

    @Test
    @DisplayName("Test isApplicable True")
    void testIsApplicableTrue() {
        String inviterNamePattern = "notNull";
        GoalInvitationFilterDto goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                .inviterNamePattern(inviterNamePattern)
                .build();

        boolean result = inviterNameFilter.isApplicable(goalInvitationFilterDto);

        assertTrue(result);
    }

    @Test
    @DisplayName("Test isApplicable False")
    void testIsApplicableFalse() {
        GoalInvitationFilterDto goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                .build();

        boolean result = inviterNameFilter.isApplicable(goalInvitationFilterDto);

        assertFalse(result);
    }

    @Test
    @DisplayName("Test applyFilter Positive Should Return All Invitations Matching Pattern")
    void testApplyFilterPositiveShouldReturnAllInvitationsMatchingPattern() {
        String inviterNamePattern = "john";
        GoalInvitationFilterDto goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                .inviterNamePattern(inviterNamePattern)
                .build();
        firstUser = mock(User.class);
        secondUser = mock(User.class);
        firstGoalInvitation = mock(GoalInvitation.class);
        secondGoalInvitation = mock(GoalInvitation.class);
        Mockito.when(firstUser.getUsername()).thenReturn("JohnDoe");
        Mockito.when(secondUser.getUsername()).thenReturn("JohnDoe");
        Mockito.when(firstGoalInvitation.getInviter()).thenReturn(firstUser);
        Mockito.when(secondGoalInvitation.getInviter()).thenReturn(secondUser);

        Stream<GoalInvitation> goalInvitations = Stream.of(firstGoalInvitation, secondGoalInvitation);
        List<GoalInvitation> result = inviterNameFilter.apply(goalInvitations, goalInvitationFilterDto).toList();
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Test applyFilter Positive Should Return Empty Stream When No Invitations Match Pattern")
    void testApplyFilterPositiveShouldReturnEmptyStreamWhenNoInvitationsMatchPattern() {
        String inviterNamePattern = "nonePattern";
        GoalInvitationFilterDto goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                .inviterNamePattern(inviterNamePattern)
                .build();
        firstUser = mock(User.class);
        secondUser = mock(User.class);
        firstGoalInvitation = mock(GoalInvitation.class);
        secondGoalInvitation = mock(GoalInvitation.class);
        Mockito.when(firstUser.getUsername()).thenReturn("JohnDoe");
        Mockito.when(secondUser.getUsername()).thenReturn("JohnDoe");
        Mockito.when(firstGoalInvitation.getInviter()).thenReturn(firstUser);
        Mockito.when(secondGoalInvitation.getInviter()).thenReturn(secondUser);

        Stream<GoalInvitation> goalInvitations = Stream.of(firstGoalInvitation, secondGoalInvitation);
        List<GoalInvitation> result = inviterNameFilter.apply(goalInvitations, goalInvitationFilterDto).toList();
        assertEquals(0, result.size());
    }
}
