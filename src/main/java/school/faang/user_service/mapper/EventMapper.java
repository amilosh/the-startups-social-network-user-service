package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.EventDto;
import school.faang.user_service.entity.event.Event;

@Mapper(componentModel = "spring", uses = SkillMapper.class)
public interface EventMapper {
//    @Mapping(target = "relatedSkills", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "attendees", ignore = true)
    @Mapping(target = "ratings", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "status", ignore = true)
    Event toEntity(EventDto eventDto);

//    @Mapping(target = "relatedSkills", ignore = true)
    @Mapping(source = "owner.id", target = "ownerId")
    EventDto toDto(Event event);
}
