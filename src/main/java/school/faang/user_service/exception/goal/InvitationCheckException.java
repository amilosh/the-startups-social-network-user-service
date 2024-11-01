package school.faang.user_service.exception.goal;

public class InvitationCheckException extends RuntimeException{
    public InvitationCheckException(String message, Object... args) {
        super(String.format(message, args));
    }
}
