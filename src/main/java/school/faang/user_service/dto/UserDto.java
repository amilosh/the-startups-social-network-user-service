package school.faang.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    @NotNull(message = "Id is required")
    private Long id;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 16, message = "Username length should be min 3, max 16")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be in valid email format")
    @Size(min = 3, max = 16, message = "Email length should be min 3, max 16")
    private String email;
}
