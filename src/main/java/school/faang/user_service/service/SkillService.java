package school.faang.user_service.service;

import school.faang.user_service.web.dto.skill.SkillCandidateDto;
import school.faang.user_service.web.dto.skill.SkillDto;

import java.util.List;
import java.util.Optional;

public interface SkillService {

    SkillDto create(SkillDto skillDto);

    Optional<List<SkillDto>> getUserSkills(Long userId);

    Optional<List<SkillCandidateDto>> getOfferedSkills(Long userId);

    SkillDto acquireSkillFromOffers(Long skillId, Long userId);
}
