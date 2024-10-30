package school.faang.user_service.mapper.skill;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillCandidateMapper {

    default List<SkillCandidateDto> toSkillCandidateDtoList(List<SkillDto> skills) {
        Map<SkillDto, SkillCandidateDto> skillsMap = new HashMap<>();
        for (SkillDto skillDto : skills) {
            skillsMap.computeIfAbsent(
                    skillDto,
                    k -> new SkillCandidateDto(skillDto, 0)
            ).setOffersAmount(
                    skillsMap.get(skillDto).getOffersAmount() + 1
            );
        }
        return skillsMap.values().stream()
                .toList();
    }
}