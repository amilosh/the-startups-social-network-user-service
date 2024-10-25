package school.faang.user_service.model.event;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FollowerEvent {
    private Long followerId;
    private Long followedUserId;
    private Long followedProjectId;
    private LocalDateTime followedAt;
}