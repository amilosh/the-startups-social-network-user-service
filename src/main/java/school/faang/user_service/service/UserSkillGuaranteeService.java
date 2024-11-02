package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.validation.recommendation.RecommendationServiceValidator;
import school.faang.user_service.validation.skill.SkillValidation;

@Component
@RequiredArgsConstructor
public class UserSkillGuaranteeService {
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final RecommendationServiceValidator recommendationServiceValidator;
    private final SkillValidation skillValidation;
    private final UserService userService;

    public void addSkillGuarantee(Skill skill, Recommendation recommendation) {
        recommendationServiceValidator.validateRecommendationExistsById(recommendation.getId());
        skillValidation.validateSkillExists(skill.getId());

        UserSkillGuarantee userSkillGuarantee = UserSkillGuarantee.builder()
                .user(userService.findUserById(recommendation.getReceiver().getId()))
                .skill(skill)
                .guarantor(userService.findUserById(recommendation.getAuthor().getId()))
                .build();
        skill.getGuarantees().add(userSkillGuarantee);
        userSkillGuaranteeRepository.save(userSkillGuarantee);
    }
}
