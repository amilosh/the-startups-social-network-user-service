package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

@Data
@NoArgsConstructor
public class RequestFilterDto {
    @Size(min = 1, max = 255, message = "The description pattern should be between 1 and 255 characters")
    private String descriptionPattern;

    private Long requesterId;

    private Long receiverId;

    private RequestStatus status;

}
