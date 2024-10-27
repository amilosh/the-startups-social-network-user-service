package school.faang.user_service.mapper.event;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {
    EventDto toDto(Event event);
    Event toEvent(EventDto eventDto);
    List<EventDto> toDtoList(List<Event> events);
}
