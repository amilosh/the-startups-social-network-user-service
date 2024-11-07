package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestFilterDto {
    private String messagePattern;
    private Long requesterId;
    private Long receiverId;
    private LocalDateTime createdAt;
}
