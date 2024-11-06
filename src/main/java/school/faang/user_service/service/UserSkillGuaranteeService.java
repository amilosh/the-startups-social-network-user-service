package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.validation.recommendation.RecommendationValidator;
import school.faang.user_service.validation.skill.SkillValidator;

@Component
@RequiredArgsConstructor
public class UserSkillGuaranteeService {
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final RecommendationValidator recommendationValidator;
    private final SkillValidator skillValidator;
    private final UserService userService;

    public void addSkillGuarantee(Skill skill, Recommendation recommendation) {
        recommendationValidator.validateRecommendationExistsById(recommendation.getId());
        skillValidator.validateSkillExists(skill.getId());

        UserSkillGuarantee userSkillGuarantee = UserSkillGuarantee.builder()
                .user(userService.findUser(recommendation.getReceiver().getId()))
                .skill(skill)
                .guarantor(userService.findUser(recommendation.getAuthor().getId()))
                .build();
        skill.getGuarantees().add(userSkillGuarantee);
        userSkillGuaranteeRepository.save(userSkillGuarantee);
    }
}
