package school.faang.user_service.messaging.events;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ProfileViewEvent {
    private Long idVisitor;
    private Long idVisited;
    private LocalDateTime timestamp;

    @Override
    public String toString() {
        return idVisitor + "\n" + idVisited;
    }
}
