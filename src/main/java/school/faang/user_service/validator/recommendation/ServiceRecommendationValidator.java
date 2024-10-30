package school.faang.user_service.validator.recommendation;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
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

    public void checkingTheSkillsOfRecommendation(List<SkillOffer> skills) {
        skills.forEach(skillOffer -> {
            if (!skillRepository.existsByTitle(skillOffer.getSkill().getTitle())) {
                throw new DataValidationException("These skills do not correspond to the system ones");
            }
        });
    }
}