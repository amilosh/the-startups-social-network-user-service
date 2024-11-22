package school.faang.user_service.exception;

public class ControllerNotValidatesSkillException extends RuntimeException {
    public ControllerNotValidatesSkillException(String note) {
        super(note);
    }
}
