package school.faang.user_service.filter.goal;

import org.junit.jupiter.api.BeforeEach;
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

public class InvitedNameFilterTest {

    private InvitedNameFilter invitedNameFilter;

    private GoalInvitation firstGoalInvitation;
    private GoalInvitation secondGoalInvitation;
    private User firstUser;
    private User secondUser;

    @BeforeEach
    public void setUp() {
        invitedNameFilter = new InvitedNameFilter();
        firstUser = new User();
        secondUser = new User();
        firstGoalInvitation = new GoalInvitation();
        secondGoalInvitation = new GoalInvitation();
    }

    @Test
    public void testIsApplicableTrue() {
        String invitedNamePattern = "notNull";
        GoalInvitationFilterDto goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                .invitedNamePattern(invitedNamePattern)
                .build();

        boolean result = invitedNameFilter.isApplicable(goalInvitationFilterDto);

        assertTrue(result);
    }

    @Test
    public void testIsApplicableFalse() {
        GoalInvitationFilterDto goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                .build();

        boolean result = invitedNameFilter.isApplicable(goalInvitationFilterDto);

        assertFalse(result);
    }

    @Test
    public void testApplyFilterPositiveShouldReturnAllInvitationsMatchingPattern() {
        String invitedNamePattern = "john";
        GoalInvitationFilterDto goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                .invitedNamePattern(invitedNamePattern)
                .build();
        firstUser = mock(User.class);
        secondUser = mock(User.class);
        firstGoalInvitation = mock(GoalInvitation.class);
        secondGoalInvitation = mock(GoalInvitation.class);
        Mockito.when(firstUser.getUsername()).thenReturn("JohnDoe");
        Mockito.when(secondUser.getUsername()).thenReturn("JohnDoe");
        Mockito.when(firstGoalInvitation.getInvited()).thenReturn(firstUser);
        Mockito.when(secondGoalInvitation.getInvited()).thenReturn(secondUser);

        Stream<GoalInvitation> goalInvitations = Stream.of(firstGoalInvitation, secondGoalInvitation);
        List<GoalInvitation> result = invitedNameFilter.apply(goalInvitations, goalInvitationFilterDto).toList();
        assertEquals(2, result.size());
    }

    @Test
    public void testApplyFilterPositiveShouldReturnEmptyStreamWhenNoInvitationsMatchPattern() {
        String invitedNamePattern = "nonePattern";
        GoalInvitationFilterDto goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                .invitedNamePattern(invitedNamePattern)
                .build();
        firstUser = mock(User.class);
        secondUser = mock(User.class);
        firstGoalInvitation = mock(GoalInvitation.class);
        secondGoalInvitation = mock(GoalInvitation.class);
        Mockito.when(firstUser.getUsername()).thenReturn("JohnDoe");
        Mockito.when(secondUser.getUsername()).thenReturn("JohnDoe");
        Mockito.when(firstGoalInvitation.getInvited()).thenReturn(firstUser);
        Mockito.when(secondGoalInvitation.getInvited()).thenReturn(secondUser);

        Stream<GoalInvitation> goalInvitations = Stream.of(firstGoalInvitation, secondGoalInvitation);
        List<GoalInvitation> result = invitedNameFilter.apply(goalInvitations, goalInvitationFilterDto).toList();
        assertEquals(0, result.size());
    }
}
