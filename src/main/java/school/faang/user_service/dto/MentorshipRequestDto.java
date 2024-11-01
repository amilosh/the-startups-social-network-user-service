package school.faang.user_service.dto;

import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

public record MentorshipRequestDto(long id,
                                   String description,
                                   long requesterId,
                                   long receiverId,
                                   RequestStatus status,
                                   String rejectionReason,
                                   LocalDateTime createdAt,
                                   LocalDateTime updatedAt) {
}