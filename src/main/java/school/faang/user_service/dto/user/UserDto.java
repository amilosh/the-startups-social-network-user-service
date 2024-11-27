package school.faang.user_service.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;

    @NotBlank(message = "Username should not be blank")
    @NotEmpty(message = "Username should not be empty")
    @Size(min = 1, max = 64, message = "Username should be between 1 and 64 characters long")
    private String username;

    @NotBlank(message = "Email should not be blank")
    @NotEmpty(message = "Email should not be empty")
    @Email(message = "Email should be a valid email address")
    @Size(min = 1, max = 64, message = "Email should be between 1 and 64 characters long")
    private String email;

    @Size(max = 4096, message = "About Me section should not exceed 4096 characters")
    private String aboutMe;

    @Size(max = 32, message = "Phone number should not exceed 32 characters")
    private String phone;

    private Integer experience;

    @Size(min = 1, max = 64, message = "City name should be between 1 and 64 characters long")
    private String city;
}
