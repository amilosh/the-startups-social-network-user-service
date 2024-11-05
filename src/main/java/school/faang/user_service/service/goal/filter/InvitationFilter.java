package school.faang.user_service.service.goal.filter;

import school.faang.user_service.dto.goal.InvitationFilterIDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

public interface InvitationFilter {
    boolean isApplicable(InvitationFilterIDto filters);

    void apply(Stream<GoalInvitation> invitations, InvitationFilterIDto filters);
}
