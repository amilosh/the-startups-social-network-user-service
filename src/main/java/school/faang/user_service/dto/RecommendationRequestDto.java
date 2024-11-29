package school.faang.user_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "Unique identifier of the recommendation request", example = "1")
    private Long id;

    @NotBlank(message = "Message must not be empty")
    @Size(min = 5, max = 500, message = "Message must contain from 5 to 500 symbols")
    @Schema(description = "Message provided by the requester", example = "Please consider me for this position.")
    private String message;

    @Schema(description = "Status of the recommendation request", example = "PENDING")
    private RequestStatus status;

    @NotEmpty(message = "Skill list must not be empty")
    @Schema(description = "List of skill identifiers associated with the request", example = "[101, 102, 103]")
    private List<Long> skillIdentifiers;

    @NotNull(message = "Requester ID field is mandatory")
    @Schema(description = "Unique identifier of the user making the request", example = "1")
    private Long requesterId;

    @NotNull(message = "Receiver ID field is Mandatory")
    @Schema(description = "Unique identifier of the user receiving the request", example = "2")
    private Long receiverId;

    @Schema(description = "Timestamp when the request was created", example = "2023-10-01T12:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the request was last updated", example = "2023-10-02T15:45:00")
    private LocalDateTime updatedAt;

    @Size(max = 300, message = "Reject reason must not exceed 300 symbols")
    @Schema(description = "Reason for rejecting the recommendation request", example = "Insufficient experience in the required skills.")
    private String rejectionReason;
}
