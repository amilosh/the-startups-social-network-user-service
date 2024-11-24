package school.faang.user_service.dto.event;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class EventDto {

    @Min(value = 1, message = "eventId должен быть больше нуля")
    private long id;
}
