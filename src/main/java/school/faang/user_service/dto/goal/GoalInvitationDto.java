package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Builder
@Data
public class GoalInvitationDto {
    @Positive
    private Long id;

    @Positive(message = "Inviter ID must be positive number")
    @NotNull(message = "Inviter ID must be positive number")
    private Long inviterId;

    @Positive(message = "Invited ID must be positive number")
    @NotNull(message = "Invited ID must be positive number")
    private Long invitedUserId;

    @Positive(message = "Goal ID must be positive number")
    @NotNull(message = "Invited ID must be positive number")
    private Long goalId;

    private RequestStatus status;
}
