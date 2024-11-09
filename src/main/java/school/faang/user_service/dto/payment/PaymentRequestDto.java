package school.faang.user_service.dto.payment;

import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.payment.Currency;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentRequestDto {
    private long paymentNumber;
    private BigDecimal amount;
    private Currency currency;
}
