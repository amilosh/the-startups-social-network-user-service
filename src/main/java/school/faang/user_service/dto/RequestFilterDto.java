package school.faang.user_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class RequestFilterDto {
    private Long id;
    private Long requesterId;
    private Long receiverId;
    @NotNull
    private RequestStatus status;

    private LocalDateTime createdAfter;
    private LocalDateTime createdBefore;
    private LocalDateTime updatedAfter;
    private LocalDateTime updatedBefore;

    private List<Long> skillIds;

    private String messageContains;
    private String rejectionReasonContains;
}
