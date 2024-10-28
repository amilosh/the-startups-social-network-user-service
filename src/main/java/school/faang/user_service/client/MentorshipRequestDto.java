package school.faang.user_service.client;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@Getter
@Setter
public class MentorshipRequestDto {
    private Long id;
    private String username;
    private String email;

}
