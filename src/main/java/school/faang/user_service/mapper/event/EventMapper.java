package school.faang.user_service.mapper.event;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {
    Event toEntity(EventDto eventDto);

    List<Event> toEntity(List<EventDto> eventsDto);

    EventDto toDto(Event event);

    List<EventDto> toDto(List<Event> events);
}