package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Data
@Builder
public class RequestFilterDto {

    @NotBlank(message = "Description must not be empty or blank.")
    private String descriptionPattern;

    private Long requesterId;
    private Long receiverId;
    private RequestStatus status;
}
