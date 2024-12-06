package school.faang.user_service.message.event;

import java.util.List;

public record UsersBanEvent(
        List<Long> userIdsToBan
) {
}
