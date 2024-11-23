package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class RecommendationRequestDto {
    private Long id;
    @NotBlank(message = "message cannot be empty or consist of whitespace characters")
    private String message;
    private RequestStatus status;
    @NotNull(message = "skillIds cannot be null")
    private List<Long> skillIds;
    @NotNull(message = "requested cannot be null")
    private Long requesterId;
    @NotNull(message = "receiverId cannot be null")
    private Long receiverId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
