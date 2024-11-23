package school.faang.user_service.dto.premium;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.dto.payment.Currency;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PremiumDto {
    @NotNull
    private Long userId;

    @NotNull
    private Currency currency;

    @NotNull
    private Long days;

    @NotNull
    private String premiumType;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String message;
}
