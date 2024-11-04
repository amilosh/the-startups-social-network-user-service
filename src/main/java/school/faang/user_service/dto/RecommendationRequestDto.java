package school.faang.user_service.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecommendationRequestDto {
    private Long id;

    @NotEmpty(message = "Сообщение не должно быть пустым")
    private String message;

    private RequestStatus status;
    private List<Long> skills;
    private Long requesterId;
    private Long receiverId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String rejectionReason;
}
