package school.faang.user_service.dto.goal;

import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;
@Data
public class GoalInvitationResponseDto {
    private Long id;
    private Long goalId;
    private Long userId;
    private String message;
    private RequestStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
