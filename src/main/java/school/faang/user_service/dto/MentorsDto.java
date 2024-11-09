package school.faang.user_service.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
public class MentorsDto {
    private String username;
    private Long userId;
}
