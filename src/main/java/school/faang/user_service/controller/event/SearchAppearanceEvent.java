package school.faang.user_service.controller.event;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class SearchAppearanceEvent {
    private Long userId;
    private Long searchingUserId;
    private LocalDateTime viewedAt;
}
