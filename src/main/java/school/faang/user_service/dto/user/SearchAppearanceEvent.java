package school.faang.user_service.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class SearchAppearanceEvent {
    private Long userId;
    private Long searchingUserId;
    private LocalDateTime viewedAt;
}