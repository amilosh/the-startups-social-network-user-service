package school.faang.user_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import school.faang.user_service.entity.RequestStatus;

public record RequestFilterDto(@NotNull(message = "Status cannot be empty") RequestStatus status,
                               @Size(max = 255, message = "Message can contain no more than 255 characters.")
                               String messagePattern) {
}
