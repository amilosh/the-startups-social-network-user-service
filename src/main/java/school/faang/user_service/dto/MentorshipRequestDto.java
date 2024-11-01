package school.faang.user_service.dto;

import lombok.*;
import school.faang.user_service.entity.RequestStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MentorshipRequestDto {
    private Long id;
    @NonNull
    private Long requesterId;
    @NonNull
    private Long receiverId;
    @NonNull
    private String description;
    private RequestStatus status;
    private String rejectionReason;
}
