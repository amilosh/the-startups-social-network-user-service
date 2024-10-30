package school.faang.user_service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFilterDTO {

    private String namePattern;
    private String aboutPattern;
    private String emailPattern;

    @Min(value = 0, message = "Минимальный опыт не может быть отрицательным.")
    private Integer experienceMin;

    @Min(value = 0, message = "Максимальный опыт не может быть отрицательным.")
    @Max(value = 50, message = "Максимальный опыт не может превышать 50.")
    private Integer experienceMax;

    private Integer page;
    private Integer pageSize;
}
