package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Builder
@Data
public class GoalInvitationFilterDto {
    private String inviterNamePattern;

    private String invitedNamePattern;

    @Positive(message = "Inviter ID must be positive number")
    private Long inviterId;

    @Positive(message = "Inviter ID must be positive number")
    private Long invitedId;

    private RequestStatus status;
}
