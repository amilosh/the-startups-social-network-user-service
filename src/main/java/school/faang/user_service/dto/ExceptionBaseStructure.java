package school.faang.user_service.dto;

import lombok.Value;

@Value
public class ExceptionBaseStructure {
    String message;
    String codeError;
    String dateTime;
    String SystemID;
}