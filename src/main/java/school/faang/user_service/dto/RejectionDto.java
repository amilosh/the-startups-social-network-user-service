package school.faang.user_service.dto;

import lombok.Data;

@Data
public class RejectionDto {
    private int mentorshipRequestId;
    private String reason;
}
