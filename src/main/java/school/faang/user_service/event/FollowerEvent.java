package school.faang.user_service.event;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FollowerEvent {

    @NotNull
    private Long actorId;

    @NotNull
    private Long receiverId;

    private LocalDateTime receivedAt;
}
