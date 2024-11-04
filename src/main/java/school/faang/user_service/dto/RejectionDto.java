package school.faang.user_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Data
public class RejectionDto {
    @NotNull
    private Long id;

    @NotNull
    private RequestStatus status;

    @NotNull
    private String reason;
}
