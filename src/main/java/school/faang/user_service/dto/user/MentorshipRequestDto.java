package school.faang.user_service.dto.mentorship;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MentorshipRequestDto {

    private Long id;
    private String description;
    private Long requesterId;
    private Long receiverId;
    private LocalDateTime createdAt;
}
