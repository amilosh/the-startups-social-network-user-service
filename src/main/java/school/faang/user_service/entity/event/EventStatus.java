package school.faang.user_service.entity.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EventStatus {
    PLANNED("Planned"),
    IN_PROGRESS("In Progress"),
    CANCELED("Canceled"),
    COMPLETED("Completed");

    private final String status;

    EventStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return status;
    }

    @JsonCreator
    public static EventStatus fromValue(String value) {
        for (EventStatus status : EventStatus.values()) {
            if (status.status.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid EventStatus: " + value);
    }

    @JsonValue
    public String toValue() {
        return this.status;
    }
}