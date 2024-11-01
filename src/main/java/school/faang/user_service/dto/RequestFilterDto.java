package school.faang.user_service.dto;


import jakarta.validation.constraints.NotNull;
import lombok.*;
import school.faang.user_service.entity.RequestStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestFilterDto {
    @NotNull
    private Long requesterId;
    private Long receiverId;
    private RequestStatus status;
    private String description;
}
