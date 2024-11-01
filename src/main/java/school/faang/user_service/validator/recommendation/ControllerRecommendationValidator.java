package school.faang.user_service.validator.recommendation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.exeption.DataValidationException;

@Slf4j
@Component
public class ControllerRecommendationValidator {
    public void validateContentRecommendation(String content) {
        if (content.isEmpty() && content.isBlank()) {
            log.error("Recommendation content is empty {}", content);
            throw new DataValidationException("Recommendation content is empty");
        }
    }
}