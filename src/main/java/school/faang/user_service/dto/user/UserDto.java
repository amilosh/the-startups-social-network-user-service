package school.faang.user_service.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    @NotNull
    private Long id;

    @NotNull
    private String username;

    @NotNull
    private String email;
    private String userProfilePicFileId;
    private Long premiumId;
}
