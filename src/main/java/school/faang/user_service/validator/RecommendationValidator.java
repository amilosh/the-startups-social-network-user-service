package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationValidator {
    public final static int PERIOD = 6;
    public final static String CONTENT_IS_EMPTY = "Recommendation content is empty";
    public final static String AUTHOR_NOT_FOUND = "Author with id = %s does not exist";
    public final static String RECEIVER_NOT_FOUND = "Receiver with id = %s does not exist";
    public final static String SKILL_DOES_NOT_EXIST = "Skill with id = %s does not exist";
    public final static String PERIOD_HAS_NOT_EXPIRED = "The author with id = %d made a recommendation to the user with id = %d less than 6 hours ago";
    public final static String RECOMMENDATION_NOT_EXIST_BY_ID = "Recommendation with id = %s does not exist";
    public final static String RECOMMENDATION_NOT_EXIST = "Recommendation with author id = %s and receiver id = %s does not exist";


    private final RecommendationRepository recommendationRepository;
    private final SkillValidator skillValidator;
    private final UserValidator userValidator;

    public void validateContent(String content) {

        if (content == null || content.isBlank())
            throw new DataValidationException(CONTENT_IS_EMPTY);
    }

    public void validateAuthorExist(Long authorId) {
        userValidator.validateUserExist(authorId, String.format(AUTHOR_NOT_FOUND, authorId));
    }

    public void validateReceiverExist(Long receiverId) {
        userValidator.validateUserExist(receiverId, String.format(RECEIVER_NOT_FOUND, receiverId));
    }

    public void validatePeriod(Long authorId, Long receiverId) {
        Optional<Recommendation> recommendationOptional =
                recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(authorId, receiverId);

        recommendationOptional.ifPresent(r -> {
            if (!r.getCreatedAt().isBefore(LocalDateTime.now().minusMonths(PERIOD))) {
                String error = String.format(PERIOD_HAS_NOT_EXPIRED, authorId, receiverId);
                log.error(error);
                throw new IllegalArgumentException(error);
            }
        });
    }

    public void validateRecommendationExist(RecommendationDto recommendation) {
        validateRecommendationExist(recommendation.getId());

        Optional<Recommendation> recommendationOptional =
                recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendation.getAuthorId(), recommendation.getReceiverId());

        recommendationOptional.ifPresent(r -> {
            String error = String.format(RECOMMENDATION_NOT_EXIST, recommendation.getAuthorId(), recommendation.getReceiverId());
            log.error(error);
            throw new IllegalArgumentException(error);
        });
    }

    public void validateRecommendationExist(Long recommendationId) {
        if (!recommendationRepository.existsById(recommendationId)) {
            String error = String.format(RECOMMENDATION_NOT_EXIST_BY_ID, recommendationId);
            log.error(error);
            throw new IllegalArgumentException(error);
        }
    }

    public void validateSkills(Stream<Long> skillIds) {
        skillIds.forEach(skillId -> {
            if (!skillValidator.existsById(skillId)) {
                String error = String.format(SKILL_DOES_NOT_EXIST, skillId);
                log.error(error);
                throw new IllegalArgumentException(error);
            }
        });
    }
}
