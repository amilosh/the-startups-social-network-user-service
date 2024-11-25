package school.faang.user_service.validator.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ControllerRecommendationValidator {
    public void validateContentRecommendation(String content) {
        if (content == null){
            throw new DataValidationException("Recommendation content is null");
        }
        if (content.isEmpty() || content.isBlank()) {
            log.error("Recommendation content is empty {}", content);
            throw new DataValidationException("Recommendation content is empty");
        }
    }
}
