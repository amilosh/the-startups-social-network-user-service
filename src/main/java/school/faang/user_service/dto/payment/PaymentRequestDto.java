package school.faang.user_service.dto.payment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.payment.Currency;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDto {
    @NotNull(message = "Payment number is required and cannot be null")
    private Long paymentNumber;

    @NotNull(message = "Amount is required and cannot be null")
    @Min(value = 10, message = "Amount must be at least 10")
    private BigDecimal amount;

    @NotNull(message = "Currency is required and cannot be null")
    private Currency currency;
}
