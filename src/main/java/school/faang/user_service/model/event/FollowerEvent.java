package school.faang.user_service.model.event;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class FollowerEvent {
    private Long followerId;
    private Long followedUserId;
    private Long followedProjectId;
    private LocalDateTime followedAt;
}