package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Data
public class RecommendationRequestDto {
    private Long id;
    @NotBlank(message = "message cannot be empty or consist of whitespace characters")
    private String message;
    private RequestStatus status;
    @NotNull
    private List<Long> skillIds;
    @NotNull(message = "requested cannot be null")
    private Long requesterId;
    @NotNull(message = "receiverId cannot be null")
    private Long receiverId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
