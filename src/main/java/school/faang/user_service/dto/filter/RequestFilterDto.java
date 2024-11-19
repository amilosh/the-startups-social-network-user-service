package school.faang.user_service.dto.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class RequestFilterDto {
    private Long id;
    private Long requesterId;
    private Long receiverId;
    private RequestStatus status;

    private LocalDateTime createdAfter;
    private LocalDateTime createdBefore;
    private LocalDateTime updatedAfter;
    private LocalDateTime updatedBefore;

    private List<Long> skillIds;

    private String messageContains;
    private String rejectionReasonContains;
}
