package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationRequestDto {
    private Long id;

    @NotBlank(message = "Message must not be empty")
    @Size(min = 5, max = 500, message = "Message must contain from 5 to 500 symbols")
    private String message;

    private RequestStatus status;

    @NotEmpty(message = "Skill list must not be empty")
    private List<Long> skills;

    @NotNull(message = "Requester ID field is mandatory")
    private Long requesterId;

    @NotNull(message = "Receiver ID field is Mandatory")
    private Long receiverId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Size(max = 300, message = "Reject reason must not exceed 300 symbols")
    private String rejectionReason;
}
