package school.faang.user_service.dto;

import lombok.Data;

@Data
public class MentorshipRequestDto {
    private Long id;
    private String description;
    private Long requesterUserId;
    private Long receiverUserId;
    private String status;
    private String rejectionReason;
    private String createdAt;
    private String updatedAt;
}
