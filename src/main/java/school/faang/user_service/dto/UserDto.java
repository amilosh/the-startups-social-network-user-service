package school.faang.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
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

    private List<Long> menteesId;
    private List<Long> mentorsId;
    private List<Long> skillsId;
//    private String phone;
//    private String aboutMe;
//    private String city;
//    private Integer experience;
//    private LocalDateTime createdAt;
//    private LocalDateTime updatedAt;
}
