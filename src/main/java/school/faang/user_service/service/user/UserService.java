package school.faang.user_service.service.user;

import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;

import java.util.List;

public interface UserService {
    List<UserDto> getPremiumUsers(UserFilterDto filter);

    void deactivateUserProfile(long id);
}