package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GoalInvitationRequestDto {
    @NotNull
    private Long goalId;

    @NotNull
    private Long userId;
    @NotNull
    private Long invitedUserId;

    @NotNull
    private Long inviterId;
    @NotNull
    private String message;
}
