package school.faang.user_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecommendationRequestDto {
    @NotNull(groups = {After.class})
    private Long id;

    @NotNull(groups = {After.class})
    private String message;

    @NotNull(groups = {After.class})
    private RequestStatus status;

    @NotNull(groups = {Before.class})
    private List<Long> skillIds;

    @NotNull(groups = {Before.class})
    private Long requesterId;

    @NotNull(groups = {Before.class})
    private Long receiverId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public interface After {}

    public interface Before {}
}
