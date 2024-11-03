package school.faang.user_service.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestFilterDto {
    private Long id;
    private String messagePattern;
    private Long requesterId;
    private Long receiverId;
    private LocalDateTime createdAt;
}
