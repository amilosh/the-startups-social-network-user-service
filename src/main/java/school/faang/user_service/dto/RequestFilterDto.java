package school.faang.user_service.dto;

import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class RequestFilterDto {
    private RequestStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Positive(message = "Requester ID must be greater than 0")
    private Long requesterId;

    @Positive(message = "Receiver ID must be greater than 0")
    private Long receiverId;

    private List<String> skillTitles;
}
