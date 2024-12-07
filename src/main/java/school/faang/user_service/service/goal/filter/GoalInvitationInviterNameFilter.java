package school.faang.user_service.service.goal.filter;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import java.util.stream.Stream;
@Component
public class GoalInvitationInviterNameFilter implements GoalInvitationFilter {
    @Override
    public boolean isApplicable(InvitationFilterDto filters) {
        return filters.getInviterNamePattern() != null;
    }

    @Override
    public Stream<GoalInvitation> apply(Stream<GoalInvitation> goalInvitations, InvitationFilterDto filters) {
        return goalInvitations.filter(goalInvitation -> goalInvitation.getInviter().getUsername().equals(filters.getInviterNamePattern()));
    }
}