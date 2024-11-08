package school.faang.user_service.validator.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ErrorMessage;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationDtoValidator {
    private static final int MONTH_DELAY_BETWEEN_RECOMMENDATIONS = 6;
    private final RecommendationRepository recRepository;
    private final SkillRepository skillRepository;

    public void validateExistedSkillsAndDate(RecommendationDto recDto) {
        checkSkillOfferExists(recDto);
        checkDateTimeRecommendationOlderSixMonth(recDto);
    }

    private void checkSkillOfferExists(RecommendationDto recDto) {
        if (recDto.getSkillOffers() == null || recDto.getSkillOffers().isEmpty()) {
            throw new NoSuchElementException(ErrorMessage.SKILL_OFFERS_IS_EMPTY);
        } else {
            List<String> skillTitlesList = recDto.getSkillOffers().stream()
                    .map(SkillOfferDto::getSkillTitle)
                    .toList();

            for (String skillTitle : skillTitlesList) {
                if (!skillRepository.existsByTitle(skillTitle)) {
                    log.error("Skill with title - {} does not exist in the system!", skillTitle);
                    throw new DataValidationException(String.format(ErrorMessage.SKILL_NOT_EXIST, skillTitle));
                }
            }
        }
    }

    private void checkDateTimeRecommendationOlderSixMonth(RecommendationDto recDto) {
        recRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recDto.getAuthorId(),
                recDto.getReceiverId()).ifPresent(recommendation -> {
            if (recommendation.getCreatedAt().isAfter(recDto.getCreatedAt().minusMonths(MONTH_DELAY_BETWEEN_RECOMMENDATIONS))) {
                throw new DataValidationException(String.format(ErrorMessage.RECOMMENDATION_WRONG_TIME,
                        recDto.getAuthorId(), recDto.getReceiverId(), MONTH_DELAY_BETWEEN_RECOMMENDATIONS));
            }
        });
    }
}
