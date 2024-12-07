package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class SubscribeEventDTO {
    private long followerId;
    private long followeeId;
    private LocalDateTime eventTime;
}
