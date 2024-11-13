package school.faang.user_service.client.payment;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentRequest {
    private Long userId;
    private int days;
}
