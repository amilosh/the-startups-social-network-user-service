package school.faang.user_service.filter.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class GoalInvitationStatusFilterTest {

    private GoalInvitationStatusFilter goalInvitationStatusFilter;

    private GoalInvitation firstGoalInvitation;
    private GoalInvitation secondGoalInvitation;

    @BeforeEach
    public void setUp() {
        goalInvitationStatusFilter = new GoalInvitationStatusFilter();
        firstGoalInvitation = new GoalInvitation();
        secondGoalInvitation = new GoalInvitation();
    }

    @Test
    @DisplayName("Test isApplicable True")
    void testIsApplicableTrue() {
        GoalInvitationFilterDto goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                .status(RequestStatus.ACCEPTED)
                .build();

        boolean result = goalInvitationStatusFilter.isApplicable(goalInvitationFilterDto);

        assertTrue(result);
    }

    @Test
    @DisplayName("Test isApplicable False")
    void testIsApplicableFalse() {
        GoalInvitationFilterDto goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                .build();

        boolean result = goalInvitationStatusFilter.isApplicable(goalInvitationFilterDto);

        assertFalse(result);
    }

    @Test
    @DisplayName("Test applyFilter All Invitations Matching Pattern")
    void testApplyFilterAllInvitationsMatchingPattern() {
        RequestStatus requestStatus = RequestStatus.ACCEPTED;
        GoalInvitationFilterDto goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                .status(requestStatus)
                .build();
        firstGoalInvitation = mock(GoalInvitation.class);
        secondGoalInvitation = mock(GoalInvitation.class);
        Stream<GoalInvitation> goalInvitations = Stream.of(firstGoalInvitation, secondGoalInvitation);
        Mockito.when(firstGoalInvitation.getStatus()).thenReturn(RequestStatus.ACCEPTED);
        Mockito.when(secondGoalInvitation.getStatus()).thenReturn(RequestStatus.ACCEPTED);

        List<GoalInvitation> result = goalInvitationStatusFilter.apply(goalInvitations, goalInvitationFilterDto).toList();

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Test applyFilter No Matching Pattern")
    void testApplyFilterShouldReturnEmptyStreamWhenNoInvitationsMatchPattern() {
        RequestStatus requestStatus = RequestStatus.REJECTED;
        GoalInvitationFilterDto goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                .status(requestStatus)
                .build();
        firstGoalInvitation = mock(GoalInvitation.class);
        secondGoalInvitation = mock(GoalInvitation.class);
        Stream<GoalInvitation> goalInvitations = Stream.of(firstGoalInvitation, secondGoalInvitation);
        Mockito.when(firstGoalInvitation.getStatus()).thenReturn(RequestStatus.ACCEPTED);
        Mockito.when(secondGoalInvitation.getStatus()).thenReturn(RequestStatus.ACCEPTED);

        List<GoalInvitation> result = goalInvitationStatusFilter.apply(goalInvitations, goalInvitationFilterDto).toList();

        assertEquals(0, result.size());
    }
}
