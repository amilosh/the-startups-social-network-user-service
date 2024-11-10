package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

@Component
public class UserFilterName implements UserFilter {

    @Override
    public boolean apply(User user, UserFilterDto userFilterDto) {
        return userFilterDto.getNamePattern() == null || user.getUsername().contains(userFilterDto.getNamePattern());
    }
}