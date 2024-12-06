package school.faang.user_service.redis.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserFollowerEvent {
    private long followerId;
    private long followeeId;
}
