package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Data
@Builder
public class MentorshipRequestDto {

    @NotNull(message = "Id is required")
    @Positive(message = "Id must be greater than 0.")
    private Long id;

    @NotBlank(message = "Description most not be empty or blank.")
    private String description;

    private Long requesterId;
    private Long receiverId;
    private RequestStatus status;
}
