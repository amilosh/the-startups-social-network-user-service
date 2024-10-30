package school.faang.user_service.web.dto.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.web.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

@Mapper(componentModel = "spring")
public interface SkillMapper extends Mappable<Skill, SkillDto> {
}
