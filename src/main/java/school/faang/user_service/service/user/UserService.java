package school.faang.user_service.service.user;

import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;

public interface UserService {

    List<UserDto> getPremiumUsers(UserFilterDto filter);

    User findUserById(long userId);

    void deactivateUserProfile(long id);

    UserDto getUser(long userId);

    List<UserDto> getUsersByIds(List<Long> userIds);
}