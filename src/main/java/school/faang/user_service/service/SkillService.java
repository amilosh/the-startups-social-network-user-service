package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.SkillDuplicateException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.validator.SkillValidator;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final SkillValidator skillValidator;
    private final UserService userService;
    private final UserSkillGuaranteeService userSkillGuaranteeService;

    public SkillDto create(SkillDto skillDto) {
        Skill skill = skillMapper.toEntity(skillDto);
        try {
            skillValidator.validateDuplicate(skill);
        } catch (SkillDuplicateException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
        skill = skillRepository.save(skill);

        return skillMapper.toDto(skill);
    }

    public List<SkillDto> getUserSkills(Long userId) {
        List<Skill> skills = skillRepository.findAllByUserId(userId);

        return skillMapper.toDto(skills);
    }

    public List<SkillDto> getOfferedSkills(long userId) {
        List<Skill> offeredSkills = skillRepository.findSkillsOfferedToUser(userId);
        return skillMapper.toDto(offeredSkills);
    }

    public List<Long> getSkillGuaranteeIds(Skill skill) {
        skillValidator.validateSkillExists(skill.getId());
        return skill.getGuarantees().stream()
                .map(userSkillGuarantee -> userSkillGuarantee.getGuarantor().getId())
                .toList();
    }

    @Transactional
    public void addGuarantee(Recommendation recommendation) {
        List<Skill> userSkills = userService.findUser(recommendation.getReceiver().getId()).getSkills();
        List<Long> recommendedSkillsIds = recommendation.getSkillOffers().stream().map(SkillOffer::getId).toList();

        userSkills.stream()
                .filter(skill -> filterSkillsForGuarantee(skill, recommendedSkillsIds, recommendation))
                .forEach(skill -> {
                    userSkillGuaranteeService.addSkillGuarantee(skill, recommendation);
                    skillRepository.save(skill);
                });
    }
    public boolean checkIfSkillExistsById(Long skillId) {
        return skillRepository.existsById(skillId);
    }

    public Skill getSkillById(Long skillId) {
        return skillRepository.getReferenceById(skillId);
    }

    private boolean filterSkillsForGuarantee(Skill skill, List<Long> recommendedSkillsIds, Recommendation
            recommendation) {
        return recommendedSkillsIds.contains(skill.getId()) &&
                !getSkillGuaranteeIds(skill).contains(recommendation.getAuthor().getId());
    }
}
