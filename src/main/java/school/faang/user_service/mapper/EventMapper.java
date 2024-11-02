package school.faang.user_service.mapper;

import org.mapstruct.Mapper;

import org.mapstruct.Mapping;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface EventMapper {
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "relatedSkills", ignore = true)
    EventDto eventToDto(Event event);


    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "relatedSkills", ignore = true)
    Event dtoToEvent(EventDto eventDto);
}
