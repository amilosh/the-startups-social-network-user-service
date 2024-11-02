package school.faang.user_service.dto;

import lombok.Data;
import school.faang.user_service.entity.Skill;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecommendationRequestDto {
    private Long id;
    private String message;
    private String status;
    private List<Skill> skills;
    private Long requesterId;
    private Long receiverId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
