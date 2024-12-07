package school.faang.user_service.dto;

import lombok.Data;

@Data
public class UserGoogleInfoDto {
    private Long id;
    private boolean emailVerified;
    private String name;
    private String email;
    private Long userId;
}
