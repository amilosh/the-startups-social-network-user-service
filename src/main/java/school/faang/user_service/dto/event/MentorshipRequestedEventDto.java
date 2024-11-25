package school.faang.user_service.dto.event;


import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MentorshipRequestedEventDto {
    private Long requesterId;
    private Long receiverId;
    private LocalDateTime requestedAt;
}