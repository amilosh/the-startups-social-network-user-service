package school.faang.user_service.dto.premium;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponsePremiumDto {
    private long id;
    private long userId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
