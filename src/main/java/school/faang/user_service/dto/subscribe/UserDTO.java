package school.faang.user_service.dto.subscribe;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    @NotNull
    private Long id;

    @NotNull
    private String username;

    @NotNull
    private String email;
}
