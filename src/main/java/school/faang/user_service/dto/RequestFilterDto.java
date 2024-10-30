package school.faang.user_service.dto;

import lombok.Data;

@Data
public class RequestFilterDto {
    private Long requesterUserId;
    private Long receiverUserId;
    private String status;
}
