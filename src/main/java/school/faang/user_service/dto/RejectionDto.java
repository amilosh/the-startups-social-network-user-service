package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RejectionDto {
    @NotBlank(message = "Нужно указать причину отклонения запроса на рекомендацию")
    private String reason;
}
