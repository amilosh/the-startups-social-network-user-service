package school.faang.user_service.client.payment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {

    @NotNull
    private long paymentNumber;

    @Min(1)
    @NotNull
    private BigDecimal amount;

    @NotNull
    private Currency currency;

}
