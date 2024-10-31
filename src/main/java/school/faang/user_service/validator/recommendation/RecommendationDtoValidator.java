package school.faang.user_service.validator.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RequestRecommendationDto;
import school.faang.user_service.dto.recommendation.RequestSkillOfferDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ErrorMessage;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationDtoValidator {
    private static final int NUMBER_OF_MONTHS_AFTER_PREVIOUS_RECOMMENDATION = 6;
    private final RecommendationRepository recommendationRepository;
    private final SkillRepository skillRepository;

    public void validateRecommendation(RequestRecommendationDto recommendation) {
        checkIfAcceptableTimeForRecommendation(recommendation);
        checkIfOfferedSkillsExist(recommendation);
    }

    private void checkIfAcceptableTimeForRecommendation(RequestRecommendationDto requestRecommendationDto) {
        recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                        requestRecommendationDto.getAuthorId(),
                        requestRecommendationDto.getReceiverId())
                .ifPresent(recommendation -> {
                    if (recommendation.getCreatedAt().isAfter(LocalDateTime.now().minusMonths(NUMBER_OF_MONTHS_AFTER_PREVIOUS_RECOMMENDATION))) {
                        log.error("Recommendation creation failed: Time limit exceeded for author {} to recommend receiver {} ({} months).",
                                requestRecommendationDto.getAuthorId(),
                                requestRecommendationDto.getReceiverId(),
                                NUMBER_OF_MONTHS_AFTER_PREVIOUS_RECOMMENDATION);
                        throw new DataValidationException(
                                String.format(ErrorMessage.RECOMMENDATION_TIME_LIMIT,
                                        requestRecommendationDto.getAuthorId(),
                                        requestRecommendationDto.getReceiverId(),
                                        NUMBER_OF_MONTHS_AFTER_PREVIOUS_RECOMMENDATION));
                    }
                });
    }

    private void checkIfOfferedSkillsExist(RequestRecommendationDto requestRecommendationDto) {
        List<RequestSkillOfferDto> requestSkillOfferDtoList = requestRecommendationDto.getSkillOffers();
        if (requestSkillOfferDtoList == null || requestSkillOfferDtoList.isEmpty()) {
            log.warn("No skill offers found for recommendation creation.");
            return;
        }

        List<String> skillTitlesList = requestSkillOfferDtoList.stream()
                .map(RequestSkillOfferDto::getSkillTitle)
                .toList();

        for (String skillTitle : skillTitlesList) {
            if (!skillRepository.existsByTitle(skillTitle)) {
                log.error("Skill with title '{}' does not exist in the system. Recommendation creation failed.", skillTitle);
                throw new DataValidationException(String.format(ErrorMessage.SKILL_NOT_EXIST, skillTitle));
            }
        }
    }
}
