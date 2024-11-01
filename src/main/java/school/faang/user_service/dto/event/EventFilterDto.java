package school.faang.user_service.dto.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventFilterDto {
    private String titlePattern;
    private Long userId;
}
