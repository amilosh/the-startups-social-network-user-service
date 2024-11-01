package school.faang.user_service.mapper;

import school.faang.user_service.dto.EventDto;
import school.faang.user_service.entity.event.Event;

public class EventMapper {

    public static Event toEntity(EventDto eventDto) {
        if (eventDto == null) {
            return null;
        }

        return Event.builder()
                .id(eventDto.id())
                .title(eventDto.title())
                .description(eventDto.description())
                .startDate(eventDto.startDate())
                .endDate(eventDto.endDate())
                .build();
    }

    public static EventDto toDto(Event event) {
        if (event == null) {
            return null;
        }

        return EventDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .build();
    }
}
