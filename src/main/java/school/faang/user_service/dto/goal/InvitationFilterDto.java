package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

@Data
@NoArgsConstructor
public class InvitationFilterDto {
    @Size(min = 1, max = 255, message = "The inviter's name pattern should be between 1 and 255 characters long")
    private String inviterNamePattern;

    @Size(min = 1, max = 255, message = "The invitee's name pattern should be between 1 and 255 characters long")
    private String invitedNamePattern;


    private Long inviterId;

    private Long invitedId;

    private RequestStatus status;
}
