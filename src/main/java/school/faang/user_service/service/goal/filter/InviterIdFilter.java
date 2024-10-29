package school.faang.user_service.service.goal.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

@Component
public class InviterIdFilter implements GoalInvitationFilter {
    @Override
    public boolean isApplicable(InvitationFilterDto filters) {
        return filters.getInviterId() != null;
    }

    @Override
    public void apply(Stream<GoalInvitation> goalInvitations, InvitationFilterDto filters) {
        goalInvitations.filter(goalInvitation -> goalInvitation.getInviter().getId().equals(filters.getInviterId()));
    }
}
