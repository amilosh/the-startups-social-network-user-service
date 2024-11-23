package school.faang.user_service.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Data
public class MentorsDto {
    private String username;
    private Long id;
}
