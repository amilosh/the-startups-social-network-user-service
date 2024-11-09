package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface EventMapper {
    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "relatedSkills", target = "relatedSkills")
    EventDto eventToDto(Event event);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "owner.id", source = "ownerId")
    @Mapping(target = "relatedSkills", source = "relatedSkills")
    Event dtoToEvent(EventDto eventDto);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "owner.id", source = "eventDto.ownerId")
    @Mapping(target = "relatedSkills", source = "eventDto.relatedSkills")
    Event dtoToEventWithId(EventDto eventDto, Long id);

    List<EventDto> toDtoList(List<Event> events);
}
