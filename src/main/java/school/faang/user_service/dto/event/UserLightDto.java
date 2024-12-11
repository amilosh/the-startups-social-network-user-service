package school.faang.user_service.dto.event;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserLightDto {
    @NotBlank
    private long id;
    @NotBlank
    private String username;
    @NotBlank
    @Email
    private String email;
}
