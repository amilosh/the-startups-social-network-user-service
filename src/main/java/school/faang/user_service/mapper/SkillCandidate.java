package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.entity.Skill;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillCandidate {
    Skill toEntity(SkillCandidateDto skillCandidateDto);
    SkillCandidateDto toDTO(Skill skill);
    @Mapping(source = "skill", target = "skill")
    @Mapping(source = "count", target = "offersAmount")
    SkillCandidateDto mapToDTO(Skill skill, Long count);

}
