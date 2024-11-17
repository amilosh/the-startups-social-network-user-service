package school.faang.user_service.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    @NotNull
    private Long id;
    private String username;
    private String email;
    private String userProfilePicFileId;
    private Long premiumId;
}
