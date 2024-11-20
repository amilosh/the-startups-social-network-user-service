package school.faang.user_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class RecommendationRequestDto {
    private Long id;
    private String message;
    private String status;
    private List<Long> skills;
    private Long requesterId;
    private Long receiverId;
    private String createdAt;
    private String updatedAt;
}
