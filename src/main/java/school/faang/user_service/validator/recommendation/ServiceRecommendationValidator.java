package school.faang.user_service.validator.recommendation;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.exeption.DataValidationException;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.service.recommendation.RecommendationService;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.skill_offer.SkillOfferService;
import school.faang.user_service.service.user_skill_guarantee.UserSkillGuaranteeService;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

import static java.lang.Math.abs;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceRecommendationValidator {
    private final SkillService skillService;
    private final SkillOfferService skillOfferService;
    private final RecommendationMapper recommendationMapper;
    private final RecommendationService recommendationService;
    private final UserSkillGuaranteeService userSkillGuaranteeService;

    public void checkingThePeriodOfFasting(long authorId, long receiverId) {
        Optional<Recommendation> recommendation = recommendationService.
                findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(authorId, receiverId);

        if (recommendation.isPresent()) {
            RecommendationDto recommendationDto = recommendationMapper.toDto(recommendation.get());
            int lastRecommendationMonth = recommendationDto.getCreatedAt().getMonthValue();
            int nowMonth = LocalDate.now().getMonthValue();

            if (abs(nowMonth - lastRecommendationMonth) < 6) {
                throw new DataValidationException("The creation date is less than 6 months");
            }
        }
    }

    public void checkingTheSkillsOfRecommendation(List<SkillOfferDto> skills) {
        skills.forEach(skillOfferDto -> {
            int skillsAvailableInDB = skillService.countExisting(skillOfferDto.getSkillsId());
            if (skillsAvailableInDB != skillOfferDto.getSkillsId().size()) {
                throw new DataValidationException("These skills do not meet the conditions");
            }
        });
    }

    public void checkingTheUserSkills(@NotNull RecommendationDto recommendationDto) {
        Long receiverId = recommendationDto.getReceiverId();
        Long authorId = recommendationDto.getAuthorId();

        recommendationDto.getSkillOffers().stream()
                .flatMap(skillOfferDto -> skillOfferDto.getSkillsId().stream()
                        .map(skillId -> {
                            skillOfferService.create(skillId, recommendationDto.getId());
                            Optional<Skill> skill = skillService.findUserSkill(skillId, receiverId);
                            if (skill.isEmpty()) {
                                skillService.assignSkillToUser(skillId, receiverId);
                            } else {
                                Long guaranteeId = userSkillGuaranteeService.createGuarantee(authorId, skillId);
                                if (guaranteeId == null) {
                                    log.info("The author is already the guarantor of this skill {}", skill);
                                }
                            }
                            return 0;
                        }));
    }

    public void preparingBeforeDelete(RecommendationDto delRecommendationDto) {
        if (delRecommendationDto.getId() == null) {
            throw new DataValidationException("This recommendation does not exist");
        }
    }
}