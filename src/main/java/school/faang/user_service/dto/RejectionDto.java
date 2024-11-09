package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RejectionDto {
    @NotBlank(message = "Rejection reason must be filled")
    private String reason;
}
