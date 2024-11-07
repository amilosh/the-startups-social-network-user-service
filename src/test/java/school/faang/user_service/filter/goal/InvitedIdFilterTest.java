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

public class InvitedIdFilterTest {

    private InvitedIdFilter invitedIdFilter;

    private GoalInvitation firstGoalInvitation;
    private GoalInvitation secondGoalInvitation;
    private User firstUser;
    private User secondUser;

    @BeforeEach
    public void setUp() {
        invitedIdFilter = new InvitedIdFilter();
        firstUser = new User();
        secondUser = new User();
        firstGoalInvitation = new GoalInvitation();
        secondGoalInvitation = new GoalInvitation();
    }

    @Test
    public void testIsApplicableTrue() {
        GoalInvitationFilterDto goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                .invitedId(1L)
                .build();

        boolean result = invitedIdFilter.isApplicable(goalInvitationFilterDto);

        assertTrue(result);
    }

    @Test
    public void testIsApplicableFalse() {
        GoalInvitationFilterDto goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                .build();

        boolean result = invitedIdFilter.isApplicable(goalInvitationFilterDto);

        assertFalse(result);
    }

    @Test
    public void testApplyFilterSameInvitationsMatchingPattern() {
        Long invitedIdPattern = 1L;
        GoalInvitationFilterDto goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                .invitedId(invitedIdPattern)
                .build();
        firstUser = mock(User.class);
        secondUser = mock(User.class);
        firstGoalInvitation = mock(GoalInvitation.class);
        secondGoalInvitation = mock(GoalInvitation.class);
        Stream<GoalInvitation> goalInvitations = Stream.of(firstGoalInvitation, secondGoalInvitation);
        Mockito.when(firstUser.getId()).thenReturn(1L);
        Mockito.when(secondUser.getId()).thenReturn(2L);
        Mockito.when(firstGoalInvitation.getInvited()).thenReturn(firstUser);
        Mockito.when(secondGoalInvitation.getInvited()).thenReturn(secondUser);

        List<GoalInvitation> result = invitedIdFilter.apply(goalInvitations, goalInvitationFilterDto).toList();

        assertEquals(1, result.size());
    }

    @Test
    public void testApplyFilterShouldReturnEmptyStreamWhenNoInvitationsMatchPattern() {
        Long invitedIdPattern = 3L;
        GoalInvitationFilterDto goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                .invitedId(invitedIdPattern)
                .build();
        firstUser = mock(User.class);
        secondUser = mock(User.class);
        firstGoalInvitation = mock(GoalInvitation.class);
        secondGoalInvitation = mock(GoalInvitation.class);
        Stream<GoalInvitation> goalInvitations = Stream.of(firstGoalInvitation, secondGoalInvitation);
        Mockito.when(firstUser.getId()).thenReturn(1L);
        Mockito.when(secondUser.getId()).thenReturn(2L);
        Mockito.when(firstGoalInvitation.getInvited()).thenReturn(firstUser);
        Mockito.when(secondGoalInvitation.getInvited()).thenReturn(secondUser);

        List<GoalInvitation> result = invitedIdFilter.apply(goalInvitations, goalInvitationFilterDto).toList();

        assertEquals(0, result.size());
    }
}
