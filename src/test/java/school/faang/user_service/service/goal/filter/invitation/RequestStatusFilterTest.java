package school.faang.user_service.service.goal.filter.invitation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.enumeration.RequestStatus;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static school.faang.user_service.util.goal.invitation.InvitationFabric.getInvitation;
import static school.faang.user_service.util.goal.invitation.InvitationFabric.getInvitationFilterDto;

class RequestStatusFilterTest {
    private final InvitationFilterDto applicableFilterDto = getInvitationFilterDto(RequestStatus.ACCEPTED);
    private final InvitationFilterDto nonApplicableFilterDto = getInvitationFilterDto();
    private final RequestStatusFilter requestStatusFilter = new RequestStatusFilter();

    @Test
    @DisplayName("Given non applicable filter when check then return false")
    void testIsApplicableNonApplicable() {
        assertThat(requestStatusFilter.isApplicable(nonApplicableFilterDto)).isEqualTo(false);
    }

    @Test
    @DisplayName("Given applicable filter when check then return true")
    void testIsApplicable() {
        assertThat(requestStatusFilter.isApplicable(applicableFilterDto)).isEqualTo(true);
    }

    @Test
    @DisplayName("Given invitations when apply then return invitation stream")
    void testApplySuccessful() {
        GoalInvitation invitation1 = getInvitation(RequestStatus.PENDING);
        GoalInvitation invitation2 = getInvitation(RequestStatus.ACCEPTED);
        GoalInvitation invitation3 = getInvitation(RequestStatus.REJECTED);

        Stream<GoalInvitation> invitationStream = Stream.of(invitation1, invitation2, invitation3);
        Stream<GoalInvitation> expectedInvitationStream = Stream.of(invitation2);
        assertThat(requestStatusFilter.apply(invitationStream, applicableFilterDto).toList())
                .isEqualTo(expectedInvitationStream.toList());
    }
}