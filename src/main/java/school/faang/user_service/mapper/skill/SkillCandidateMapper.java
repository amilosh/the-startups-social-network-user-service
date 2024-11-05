package school.faang.user_service.mapper.skill;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.entity.Skill;

@Mapper(componentModel = "Spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface SkillCandidateMapper {

    SkillCandidateDto toDto(Skill skillCandidate);
}
