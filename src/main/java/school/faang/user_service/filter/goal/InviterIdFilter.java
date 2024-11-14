package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.filter.Filter;

import java.util.stream.Stream;

@Component
public class InviterIdFilter implements Filter<GoalInvitation, GoalInvitationFilterDto> {

    @Override
    public boolean isApplicable(GoalInvitationFilterDto filter) {
        return filter.getInviterId() != null;
    }

    @Override
    public Stream<GoalInvitation> apply(Stream<GoalInvitation> goalInvitations, GoalInvitationFilterDto filter) {
        return goalInvitations.filter(invitation -> invitation
                .getInviter()
                .getId()
                .equals(filter.getInviterId()));
    }
}
