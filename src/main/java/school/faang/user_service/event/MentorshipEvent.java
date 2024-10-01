package school.faang.user_service.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MentorshipEvent extends Event {
    private long requesterId;
    private long userId;
}