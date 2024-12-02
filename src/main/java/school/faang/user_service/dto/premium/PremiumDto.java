package school.faang.user_service.dto.premium;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.dto.UserDto;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PremiumDto {

    @NotNull(message = "User can not be null")
    private UserDto userDto;

    @NotNull(message = "Start date can not be null")
    private LocalDate startDate;

    @NotNull(message = "End date can not be null")
    private LocalDate endDate;
}
