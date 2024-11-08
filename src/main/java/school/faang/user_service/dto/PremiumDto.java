package school.faang.user_service.dto;

import lombok.Data;

@Data
public class PremiumDto {
    private Long id;
    private Long userId;
    private String startDate;
    private String endDate;
}
