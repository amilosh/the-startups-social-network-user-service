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

    @Positive(message = "Inviter ID must be a positive number")
    @NotNull(message = "Inviter ID cannot be not null")
    private Long inviterId;

    @Positive(message = "Invited ID must be a positive number")
    @NotNull(message = "Invited ID cannot be not null")
    private Long invitedUserId;

    @Positive(message = "Goal ID must be a positive number")
    @NotNull(message = "Goal ID cannot be not null")
    private Long goalId;

    private RequestStatus status;
}
