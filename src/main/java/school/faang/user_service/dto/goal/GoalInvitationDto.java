package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import school.faang.user_service.entity.RequestStatus;

@Builder
public record GoalInvitationDto(
        Long id,
        @NotNull @Positive Long inviterId,
        @NotNull @Positive Long invitedUserId,
        @NotNull @Positive Long goalId,
        @NotNull RequestStatus status
) {
}
