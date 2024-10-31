package school.faang.user_service.mapper;

import org.mapstruct.Mapper;

import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface EventMapper {
    EventDto eventToDto(Event event);
    Event dtoToEvent(EventDto eventDto);
}
