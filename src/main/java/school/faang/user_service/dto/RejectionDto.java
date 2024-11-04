package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NonNull;

@Data
public class RejectionDto {
    @NotBlank(message = "Нужно указать причину отклонения запроса на рекомендацию")
    private String reason;
}
