package school.faang.user_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import school.faang.user_service.entity.RequestStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MentorshipRequestDto {
    private Long id;
    @NotNull
    private Long requesterId;
    @NotNull
    private Long receiverId;
    @NotNull
    private String description;
    private RequestStatus status;
    private String rejectionReason;
}
