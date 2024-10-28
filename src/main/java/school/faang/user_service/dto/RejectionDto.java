package school.faang.user_service.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;
import school.faang.user_service.entity.RequestStatus;

@Getter
@Setter
public class RejectionDto {
    private String reason;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;
}
