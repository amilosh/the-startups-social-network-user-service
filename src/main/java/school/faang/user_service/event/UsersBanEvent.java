package school.faang.user_service.event;

import java.util.List;

public record UsersBanEvent(
        List<Long> userIdsToBan
) {
}
