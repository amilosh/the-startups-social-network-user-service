package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MentorshipRequestEvent {
    private long mentorId;
    private long menteeId;
    private LocalDateTime data;
}
