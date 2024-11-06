package school.faang.user_service.dto;

import jakarta.validation.constraints.Min;
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

    @Min(value = 1, message = "Ошибка в ID. Номер должен быть больше 0")
    private Long requesterId;

    @Min(value = 1, message = "Ошибка в ID. Номер должен быть больше 0")
    private Long receiverId;

    private List<String> skillTitles;
}
