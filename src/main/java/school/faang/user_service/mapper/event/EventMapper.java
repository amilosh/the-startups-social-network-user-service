package school.faang.user_service.mapper.event;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.skill.SkillMapper;

import java.util.List;
@Mapper(componentModel = "spring", uses = SkillMapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface EventMapper {
    @Mapping(source = "ownerId", target = "owner.id")
    @Mapping(source = "relatedSkills", target = "relatedSkills")
    @Mapping(target = "attendees", ignore = true)
    @Mapping(target = "ratings", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "status", ignore = true)
    Event toEntity(EventDto eventDto);

    List<Event> toEntity(List<EventDto> eventsDto);

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "relatedSkills", target = "relatedSkills")
    EventDto toDto(Event event);

    List<EventDto> toDto(List<Event> events);
}