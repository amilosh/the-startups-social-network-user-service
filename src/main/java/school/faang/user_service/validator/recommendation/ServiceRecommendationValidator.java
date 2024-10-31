package school.faang.user_service.validator.recommendation;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exeption.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static java.lang.Math.abs;

@Slf4j
@Component
@RequiredArgsConstructor
public class ServiceRecommendationValidator {
    private final RecommendationRepository recommendationRepository;
    private final SkillRepository skillRepository;

    public void checkingThePeriodOfFasting(long authorId, long receiverId) {
        Optional<Recommendation> recommendation = recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(authorId, receiverId);
        if (recommendation.isPresent()) {
            int lastRecommendationMonth = recommendation.get().getCreatedAt().getMonthValue();
            int nowMonth = LocalDate.now().getMonthValue();

            if (abs(nowMonth - lastRecommendationMonth) < 6) {
                throw new DataValidationException("The creation date is less than 6 months");
            }
        }
    }

    public void checkingTheSkillsOfRecommendation(List<SkillOfferDto> skills) {
        skills.stream()
                .forEach(skillOfferDto -> {
                    int skillsAvailableInDB = skillRepository.countExisting(skillOfferDto.getSkillsId());
                    if (skillsAvailableInDB != skillOfferDto.getSkillsId().size()) {
                        throw new DataValidationException("These skills do not meet the conditions");
                    }
                });
    }

    public void checkingTheUserSkills(RecommendationDto recommendationDto, List<Long> skills) {
        Long receiverId = recommendationDto.getReceiverId();
        Long authorId = recommendationDto.getAuthorId();
        skills.forEach(skillId -> {
            Optional<Skill> skill = skillRepository.findUserSkill(skillId, receiverId);
            if (skill.isEmpty()) {
                skillRepository.assignSkillToUser(skillId, receiverId);
            } else {
                if ()
            }
        });

    }
}