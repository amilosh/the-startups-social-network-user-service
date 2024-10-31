package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.event.SkillDto;
import school.faang.user_service.entity.Skill;

@Mapper(componentModel = "spring")
public interface SkillMapper {
   Skill toEntity(SkillDto skillDto);

   SkillDto toDto(Skill skillEntity);
}
