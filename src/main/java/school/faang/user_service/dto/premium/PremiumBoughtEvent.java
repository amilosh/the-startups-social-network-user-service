package school.faang.user_service.dto.premium;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PremiumBoughtEvent {
    private long id;
    private long userId;
    private String premiumType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
