package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

@Mapper(componentModel = "spring")
public interface SkillMapper {
    Skill dtoToEntity(SkillDto skillDto);

    SkillDto entityToDto(Skill skill);
}
