package school.faang.user_service.exceptions;

public class RecommendationRequestValidationException extends RuntimeException {
    public RecommendationRequestValidationException(String message) {
        super(message);
    }
}
