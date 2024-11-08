package school.faang.user_service.service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.service.filter.InvitationFilter;

import java.util.stream.Stream;

@Component
public class InvitationStatusFilter implements InvitationFilter {
    @Override
    public boolean isApplicable(InvitationFilterDto filterDto) {
        return filterDto.getStatus() != null;
    }

    @Override
    public void apply(Stream<GoalInvitation> invitations, InvitationFilterDto filterDto) {
        invitations.filter(invitation -> invitation.getStatus().equals(filterDto.getStatus()));
    }
}
