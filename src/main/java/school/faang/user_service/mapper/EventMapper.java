package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.EventDto;
import school.faang.user_service.entity.event.Event;

@Mapper(componentModel = "Spring")
public interface EventMapper {

    EventDto toEventDto(Event event);

    Event toEvent(EventDto eventDto);
}
