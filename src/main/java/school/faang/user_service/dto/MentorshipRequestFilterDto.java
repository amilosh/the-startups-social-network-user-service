package school.faang.user_service.dto;

import lombok.Data;

@Data
public class MentorshipRequestFilterDto {
    private Long requesterUserId;
    private Long receiverUserId;
    private String status;
}
