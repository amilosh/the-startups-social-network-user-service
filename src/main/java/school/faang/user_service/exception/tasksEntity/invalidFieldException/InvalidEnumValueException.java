package school.faang.user_service.exception.tasksEntity.invalidFieldException;

import school.faang.user_service.exception.tasksEntity.InvalidFieldException;

public class InvalidEnumValueException extends InvalidFieldException {
    public InvalidEnumValueException(String message) {
        super(message);
    }
}
