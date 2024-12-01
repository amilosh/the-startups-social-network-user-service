package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import school.faang.user_service.entity.RequestStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestFilterDto {
    private String messagePattern;
    @NotNull
    private Long requesterId;
    private Long receiverId;
    private LocalDateTime createdAt;
    private RequestStatus status;
    private String description;
}
