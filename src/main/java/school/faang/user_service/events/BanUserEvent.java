package school.faang.user_service.events;

import lombok.Data;

@Data
public class BanUserEvent {
    long userId;

    long commentCount;
}
