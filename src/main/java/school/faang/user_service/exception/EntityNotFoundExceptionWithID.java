package school.faang.user_service.exception;

import lombok.Getter;

public class EntityNotFoundExceptionWithID extends RuntimeException {
    private String message;
    @Getter
    private Long entityId;

    public EntityNotFoundExceptionWithID(String message, Long entityId) {
        super(message);
        this.entityId = entityId;
    }
}
