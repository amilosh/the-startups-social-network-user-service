package school.faang.user_service.exception;

public class InvalidMentorshipRequestException extends RuntimeException {

    public InvalidMentorshipRequestException(String message) {
        super(message);
    }
}
