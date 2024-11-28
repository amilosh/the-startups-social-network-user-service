package school.faang.user_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class RecommendationRequestFilterDto {
    private String messagePattern;
    private List<Long> requesterIds;
    private List<Long> receiverIds;
    private String status;
}
