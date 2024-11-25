package school.faang.user_service.dto;

import lombok.Data;

@Data
public class RecommendationRequestFilterDto {
    private String messagePattern;
    private Long requesterId;
    private Long receiverId;
    private RequestStatusDto status;
}
