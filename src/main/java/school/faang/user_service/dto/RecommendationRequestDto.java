package school.faang.user_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class RecommendationRequestDto {
    private Long id;
    @NotBlank(message = "Recommendation request must contain a non-blank message.")
    private String message;
    private String status;
    private String rejectionReason;
    @NotNull(message = "Skills must be specified.")
    private List<Long> skills;
    @Min(1) @NotNull(message = "Requester id must be specified.")
    private Long requesterId;
    @Min(1) @NotNull(message = "Receiver id must be specified.")
    private Long receiverId;
    private String createdAt;
    private String updatedAt;
}
