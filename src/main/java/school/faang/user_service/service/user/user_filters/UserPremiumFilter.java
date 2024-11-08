package school.faang.user_service.service.user.user_filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Component
public class UserPremiumFilter implements UserFilter {


    @Override
    public boolean isApplicable(UserFilterDto filters) {
       return filters.getPremium()!=null;
    }

    @Override
    public List<User> apply(List<User> users, UserFilterDto filters) {
        return users.stream()
                .filter(user -> user.getPremium()!=null)
                .toList();
    }
}
