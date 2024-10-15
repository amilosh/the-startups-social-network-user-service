package school.faang.user_service.model.event;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record EventFilterDto(
        String titleFilter,
        Long ownerIdFilter,
        LocalDateTime startDateFilter
) {
}