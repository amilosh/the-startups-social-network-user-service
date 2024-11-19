package school.faang.user_service.validator.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RequestRecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.recommendation.ErrorMessage;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationValidator {
    private static final int NUMBER_OF_MONTHS_AFTER_PREVIOUS_RECOMMENDATION = 6;
    private final RecommendationRepository recommendationRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    public void validateRecommendation(RequestRecommendationDto recommendation) {
        checkIfAcceptableTimeForRecommendation(recommendation);
        checkIfOfferedSkillsExist(recommendation);
    }

    public User validateUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.error("Receiver with id {} not found", userId);
            return new NoSuchElementException(String.format("There isn't user with id = %d", userId));
        });
    }

    public void checkIfAcceptableTimeForRecommendation(RecommendationRequest recommendationRequest) {
        recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                        recommendationRequest.getReceiver().getId(),
                        recommendationRequest.getRequester().getId())
                .ifPresent(recommendation -> {
                    if (recommendation.getCreatedAt().isAfter(LocalDateTime.now().minusMonths(NUMBER_OF_MONTHS_AFTER_PREVIOUS_RECOMMENDATION))) {
                        log.error("Recommendation creation failed: Time limit exceeded for author {} to recommend receiver {} ({} months).",
                                recommendationRequest.getReceiver().getId(),
                                recommendationRequest.getRequester().getId(),
                                NUMBER_OF_MONTHS_AFTER_PREVIOUS_RECOMMENDATION);
                        throw new DataValidationException(
                                String.format(ErrorMessage.RECOMMENDATION_TIME_LIMIT,
                                        recommendationRequest.getReceiver().getId(),
                                        recommendationRequest.getRequester().getId(),
                                        NUMBER_OF_MONTHS_AFTER_PREVIOUS_RECOMMENDATION));
                    }
                });
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
        List<SkillOfferDto> skillOfferDtoList = requestRecommendationDto.getSkillOffers();
        if (skillOfferDtoList == null || skillOfferDtoList.isEmpty()) {
            log.warn("No skill offers found for recommendation creation.");
            throw new NoSuchElementException("No skill offers found for recommendation creation.");
        }

        List<String> skillTitlesList = skillOfferDtoList.stream()
                .map(SkillOfferDto::getSkillTitle)
                .toList();

        for (String skillTitle : skillTitlesList) {
            if (!skillRepository.existsByTitle(skillTitle)) {
                log.error("Skill with title '{}' does not exist in the system. Recommendation creation failed.", skillTitle);
                throw new DataValidationException(String.format(ErrorMessage.SKILL_NOT_EXIST, skillTitle));
            }
        }
    }
}
