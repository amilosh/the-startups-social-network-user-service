package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

@Data
@NoArgsConstructor
public class GoalInvitationDto {
    private Long id;

    @NotNull(message = "The inviter's ID should not be empty")
    private Long inviterId;

    @NotNull(message = "The invitee's ID should not be empty.")
    private Long invitedUserId;

    @NotNull(message = "The goal's ID should not be empty.")
    private Long goalId;

    @NotNull(message = "The request status should not be null.")
    private RequestStatus status;
}
