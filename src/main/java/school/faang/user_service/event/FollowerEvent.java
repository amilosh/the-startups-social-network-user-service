package school.faang.user_service.event;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FollowerEvent {

    @NotNull
    private Long followerId;

    @NotNull
    private Long followeeId;
    private LocalDateTime subscriptionTime;
}
