package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.EventDto;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Mapper(componentModel = "spring", uses = SkillMapper.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {
    @Mapping(source = "relatedSkills", target = "relatedSkills")
    @Mapping(target = "owner", ignore = true)
    Event toEntity(EventDto eventDto);

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "relatedSkills", target = "relatedSkills")
    EventDto toDto(Event event);

    List<Event> toEntityList(List<EventDto> eventDtos);

    List<EventDto> toDtoList(List<Event> events);
}
