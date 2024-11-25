package school.faang.user_service.entity.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EventType {
    WEBINAR("Webinar"),
    POLL("Poll"),
    MEETING("Meeting"),
    GIVEAWAY("Giveaway"),
    PRESENTATION("Presentation"),
    ;
    private final String type;

    EventType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return type;
    }

    @JsonCreator
    public static EventType fromValue(String value) {
        for (EventType eventType : EventType.values()) {
            if (eventType.name().equalsIgnoreCase(value)) {
                return eventType;
            }
        }
        throw new IllegalArgumentException("Invalid EventType: " + value);
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}