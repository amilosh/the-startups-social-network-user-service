package school.faang.user_service.mapper;

import org.mapstruct.Mapper;

import org.mapstruct.Mapping;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface EventMapper {
    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "relatedSkills", target = "relatedSkills")
    EventDto eventToDto(Event event);

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "relatedSkills", ignore = true)
    Event dtoToEvent(EventDto eventDto);

    List<EventDto> toDtoList(List<Event> events);

    List<SkillDto> skillListToSkillDtoList(List<Skill> skills);

    List<Skill> skillDtoListToSkillList(List<SkillDto> skillDtosS);
}
