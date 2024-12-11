package school.faang.user_service.service.goal.filter.invitation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.enumeration.RequestStatus;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

@Component
public class RequestStatusFilter implements InvitationFilter {
    @Override
    public boolean isApplicable(InvitationFilterDto filters) {
        return filters.status() != null;
    }

    @Override
    public Stream<GoalInvitation> apply(Stream<GoalInvitation> invitations, InvitationFilterDto filters) {
        return invitations.filter(invitation -> {
            RequestStatus invitationStatus = invitation.getStatus();
            RequestStatus filterStatus = filters.status();
            return invitationStatus.equals(filterStatus);
        });
    }
}
