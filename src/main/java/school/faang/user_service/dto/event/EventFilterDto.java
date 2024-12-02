package school.faang.user_service.dto.event;

import lombok.Data;

@Data
public class EventFilterDto {
    final String titlePattern;
    final String descriptionPattern;
    final Long ownerIdPattern;
}
