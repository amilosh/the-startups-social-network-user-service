package school.faang.user_service.dto.recommendation;

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
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationRequestDto {
    private Long id;

    @NotEmpty(message = "The recommendation message should not be empty")
    @NotBlank(message = "The recommendation message should not be blank")
    @Size(min = 1, max = 4096, message = "The recommendation message should be between 1 and 4096 characters long")
    private String message;

    private RequestStatus status;

    @NotNull(message = "The list of skill requests should not be null")
    @Size(min = 1, message = "The list of skill requests should contain at least one skill")
    private List<SkillRequestDto> skillRequests;

    @NotNull(message = "The requester's ID should not be null")
    private Long requesterId;

    @NotNull(message = "The receiver's ID should not be null")
    private Long receiverId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
