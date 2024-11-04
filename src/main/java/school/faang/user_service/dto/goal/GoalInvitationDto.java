package school.faang.user_service.dto.goal;

import lombok.Getter;
import lombok.Setter;
import school.faang.user_service.entity.RequestStatus;

@Getter
@Setter
public class GoalInvitationDto {
    private long id;
    private long inviterId;
    private long invitedUserId;
    private long goalId;
    private RequestStatus status;
}
