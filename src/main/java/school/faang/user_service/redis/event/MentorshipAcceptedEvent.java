package school.faang.user_service.redis.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class MentorshipAcceptedEvent {

    private long id;
    private long requesterId;
    private String receiverUsername;
}
