package school.faang.user_service.dto.mentorshipRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MentorshipRequestDto {
    private Long id;

    @NotNull(message = "Description must not be null")
    @NotBlank(message = "Description must not be blank")
    @Size(max = 4096, message = "Description must not exceed 4096 characters")
    private String description;

    @NotNull(message = "Request user id must not be null")
    private Long requesterUserId;

    @NotNull(message = "receiver user id must not be null")
    private Long receiverUserId;

    private RequestStatus status;

    @Size(max = 4096, message = "Rejection reason must not exceed 4096 characters")
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Long> taskIds;
}
