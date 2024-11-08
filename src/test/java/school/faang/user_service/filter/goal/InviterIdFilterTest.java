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

class InviterIdFilterTest {

    private InviterIdFilter inviterIdFilter;

    private GoalInvitation firstGoalInvitation;
    private GoalInvitation secondGoalInvitation;
    private User firstUser;
    private User secondUser;


    @BeforeEach
    public void setUp() {
        inviterIdFilter = new InviterIdFilter();
        firstUser = new User();
        secondUser = new User();
        firstGoalInvitation = new GoalInvitation();
        secondGoalInvitation = new GoalInvitation();
    }

    @Test
    @DisplayName("Test isApplicable True")
    void testIsApplicableTrue() {
        GoalInvitationFilterDto goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                .inviterId(1L)
                .build();

        boolean result = inviterIdFilter.isApplicable(goalInvitationFilterDto);

        assertTrue(result);
    }

    @Test
    @DisplayName("Test isApplicable False")
    void testIsApplicableFalse() {
        GoalInvitationFilterDto goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                .build();

        boolean result = inviterIdFilter.isApplicable(goalInvitationFilterDto);

        assertFalse(result);
    }

    @Test
    @DisplayName("Test applyFilter Matching Pattern")
    void testApplyFilterSameInvitationsMatchingPattern() {
        Long inviterIdPattern = 1L;
        GoalInvitationFilterDto goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                .inviterId(inviterIdPattern)
                .build();
        firstUser = mock(User.class);
        secondUser = mock(User.class);
        firstGoalInvitation = mock(GoalInvitation.class);
        secondGoalInvitation = mock(GoalInvitation.class);
        Stream<GoalInvitation> goalInvitations = Stream.of(firstGoalInvitation, secondGoalInvitation);
        Mockito.when(firstUser.getId()).thenReturn(1L);
        Mockito.when(secondUser.getId()).thenReturn(2L);
        Mockito.when(firstGoalInvitation.getInviter()).thenReturn(firstUser);
        Mockito.when(secondGoalInvitation.getInviter()).thenReturn(secondUser);

        List<GoalInvitation> result = inviterIdFilter.apply(goalInvitations, goalInvitationFilterDto).toList();

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Test applyFilter No Matching Pattern")
    void testApplyFilterShouldReturnEmptyStreamWhenNoInvitationsMatchPattern() {
        Long inviterIdPattern = 3L;
        GoalInvitationFilterDto goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                .inviterId(inviterIdPattern)
                .build();
        firstUser = mock(User.class);
        secondUser = mock(User.class);
        firstGoalInvitation = mock(GoalInvitation.class);
        secondGoalInvitation = mock(GoalInvitation.class);
        Stream<GoalInvitation> goalInvitations = Stream.of(firstGoalInvitation, secondGoalInvitation);
        Mockito.when(firstUser.getId()).thenReturn(1L);
        Mockito.when(secondUser.getId()).thenReturn(2L);
        Mockito.when(firstGoalInvitation.getInviter()).thenReturn(firstUser);
        Mockito.when(secondGoalInvitation.getInviter()).thenReturn(secondUser);

        List<GoalInvitation> result = inviterIdFilter.apply(goalInvitations, goalInvitationFilterDto).toList();

        assertEquals(0, result.size());
    }
}
