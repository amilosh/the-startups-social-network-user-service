package school.faang.user_service.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class CountDto {
    @Min(value = 0, message = "Count should not be less than 0.")
    private int count;
}
