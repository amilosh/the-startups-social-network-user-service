package school.faang.user_service.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@NoArgsConstructor
@AllArgsConstructor

@Data
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String aboutMe;
    private String city;
    private String experience;
}
