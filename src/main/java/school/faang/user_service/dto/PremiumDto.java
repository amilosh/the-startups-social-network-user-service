package school.faang.user_service.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PremiumDto {
    private Long id;
    private Long userId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
