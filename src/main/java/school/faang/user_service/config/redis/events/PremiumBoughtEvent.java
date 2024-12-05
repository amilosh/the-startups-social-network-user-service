package school.faang.user_service.config.redis.events;



import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PremiumBoughtEvent {

    @NotNull
    private Long userId;
    private Double amount;
    private Integer duration;
    private LocalDateTime timestamp;
}
