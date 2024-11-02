package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.validation.recommendation.RecommendationServiceValidator;
import school.faang.user_service.validation.skill.SkillValidation;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final SkillValidation skillValidation;
    private final RecommendationServiceValidator recommendationServiceValidator;
    private final UserService userService;
    private final RecommendationService recommendationService;
    private final UserSkillGuaranteeService userSkillGuaranteeService;

    public SkillDto create(SkillDto skillDto) {
        Skill skill = skillMapper.toEntity(skillDto);

        skillValidation.validateDuplicate(skill);
        skill = skillRepository.save(skill);

        return skillMapper.toDto(skill);
    }

    public List<SkillDto> getUserSkills(Long userId) {
        List<Skill> skills = skillRepository.findAllByUserId(userId);

        return skillMapper.toDto(skills);
    }

    public SkillCandidateDto getOfferedSkills(SkillCandidateDto skillCandidateDto) {
        Skill skill = skillMapper.toEntity(skillCandidateDto.getSkill());

        skillCandidateDto.setOffersAmount(skill.getUsers().stream().count());

        return skillCandidateDto;
    }

    public List<Long> getSkillGuaranteeIds(Skill skill) {
        skillValidation.validateSkillExists(skill.getId());
        return skill.getGuarantees().stream()
                .map(userSkillGuarantee -> userSkillGuarantee.getGuarantor().getId())
                .toList();
    }

    public void addGuarantee(RecommendationDto recommendationDto) {
        Recommendation recommendation;
        if (recommendationService.checkIfRecommendationExistsById(recommendationDto.getId())) {
            recommendation = recommendationService.getRecommendationById(recommendationDto.getId());
        } else {
            recommendation = recommendationService.createRecommendationFromDto(recommendationDto);
        }

        List<Skill> userSkills = userService.findUserById(recommendation.getReceiver().getId()).getSkills();
        List<Long> recommendedSkillsIds = recommendation.getSkillOffers().stream().map(SkillOffer::getId).toList();

        userSkills.stream()
                .filter(skill -> filterSkillsForGuarantee(skill, recommendedSkillsIds, recommendation))
                .forEach(skill -> {
                    userSkillGuaranteeService.addSkillGuarantee(skill, recommendation);
                    skillRepository.save(skill);
                });
    }

    private boolean filterSkillsForGuarantee(Skill skill, List<Long> recommendedSkillsIds, Recommendation recommendation) {
        return recommendedSkillsIds.contains(skill.getId()) &&
                !getSkillGuaranteeIds(skill).contains(recommendation.getAuthor().getId());
    }
}
