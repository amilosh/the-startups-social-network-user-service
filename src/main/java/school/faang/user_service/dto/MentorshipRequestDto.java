package school.faang.user_service.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
public class MentorshipRequestDto {
    private long id;
    private String username;
    private String email;

}
